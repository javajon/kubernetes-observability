#!/bin/sh

export K8S_IP=$(minikube ip)
export SERVICE_PORT=$(kubectl get svc hello-world -n monitoring-demo -o jsonpath='{.spec.ports[0].nodePort}')
echo http://$K8S_IP:$SERVICE_PORT
