#!/bin/sh
# exit immediately when a command fails
set -e
# only exit with zero if all commands of the pipeline exit successfully
set -o pipefail
# error on unset variables
set -u
# print each command before executing it
# set -x

# This project was tested using: (see versions in readme)

# Start Minikube
# Ensure security for demonstration container registry is off
# Adjust cpu and memory resources to work with your target machine
minikube start --cpus 4 --memory 8192 \
--insecure-registry '192.168.99.0/24' \
--extra-config=apiserver.authorization-mode=RBAC

# Retrieves IP address of running cluster, checks it with IP in kubeconfig, and corrects kubeconfig if incorrect.
minikube update-context

# See https://github.com/kubernetes/minikube/tree/master/deploy/addons
minikube addons enable ingress
minikube addons enable registry
minikube addons enable metrics-server

# TODO, in v0.28.2 EFK elasticsearch replication controller is failing 
# minikube addons disable efk

# After registry is added, map it to port 5000 so all images can be pulled from localhost:5000
# More about this technique here: https://blog.hasura.io/sharing-a-local-registry-for-minikube-37c7240d0615
kubectl apply -f configurations/registry-proxy.yaml

# Wait for the registry pod to be running, then port forward on it
./registry-port-forward.sh

minikube status
echo "$(minikube version) is now ready"

# Troubleshooting:
# If Minikube does not start correctly, try wiping it clean with `minikube delete`,
# then run this script again. If this does not help sometimes a deeper cleaning
# such as removing `~/.minikube`, `~/.kube` or `~/.virtualbox` may help.
