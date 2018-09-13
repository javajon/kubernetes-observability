#!/bin/sh
set -e

# Wait for the registry pod to be running, then port forward on it

echo 'Waiting for registry addon to be running...'
kubectl wait pods --for condition=ready -n kube-system -l kubernetes.io/minikube-addons=registry --timeout=180s
sleep 5
registryPodName=$(kubectl get pods -n kube-system -l kubernetes.io/minikube-addons=registry --output=jsonpath={.items..metadata.name})
status=''
while [ "$status" != "Running" ]
do
    echo "Waiting for Registry pod to be 'Running'..."
    sleep 15
    status="$(kubectl get pods -n kube-system $registryPodName -o jsonpath="{.status.phase}")"
done
kubectl port-forward -n kube-system $(kubectl get pods -n kube-system -l kubernetes.io/minikube-addons=registry --output=jsonpath={.items..metadata.name}) 5000:5000 &
