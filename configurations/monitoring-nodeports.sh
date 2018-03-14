#!/bin/sh
# Roadmap: Ideally the helm chart should be setting these ports as NodePort
# Not working yet, so we use patch instead to acheive same NodePort exposure goal.
kubectl patch service mon-prometheus   --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service mon-alertmanager --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'
kubectl patch service mon-grafana      --namespace=monitoring --type='json' -p='[{"op": "replace",  "path": "/spec/type", "value":"NodePort"}]'

minikube service list -n monitoring
