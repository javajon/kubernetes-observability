#!/bin/sh
set -ex

# These steps follow the readme here: https://github.com/coreos/prometheus-operator/tree/master/helm
# With RBAC disabled
helm repo add coreos https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/

helm install coreos/prometheus-operator --name prometheus-operator --namespace monitoring
helm install coreos/kube-prometheus     --name kube-prometheus     --namespace monitoring --wait

kubectl patch service kube-prometheus              --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-alertmanager --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-grafana      --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'

minikube service list -n monitoring
