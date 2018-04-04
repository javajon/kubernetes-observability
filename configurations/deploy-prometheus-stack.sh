#!/bin/sh
set -ex

# Roadmap: Ideally the incubating helm/charts/monitoring umbrella chart may
# be a better solution, but it's not working yet.

helm repo add coreos https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/

helm install coreos/prometheus-operator --wait --name prometheus-operator --namespace monitoring --set rbacEnable=false
helm install coreos/kube-prometheus     --wait --name kube-prometheus     --namespace monitoring

kubectl patch service kube-prometheus-prometheus   --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-alertmanager --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-grafana      --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'

minikube service list -n monitoring
