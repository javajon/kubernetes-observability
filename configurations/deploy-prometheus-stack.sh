#!/bin/sh
# Roadmap: Ideally the incubating helm/charts/monitoring chart should really
# be the solution, but its not working yet.
kubectl create namespace monitoring

helm repo add coreos https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/

helm install coreos/prometheus-operator --wait --name prometheus-operator --namespace monitoring --set rbacEnable=false
helm install coreos/kube-prometheus     --wait --name kube-prometheus     --namespace monitoring

kubectl patch service kube-prometheus-prometheus   --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-alertmanager --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service kube-prometheus-grafana      --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
