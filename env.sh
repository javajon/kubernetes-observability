#!/bin/sh
# Be sure to run this script with sourcing:  ". ./env.sh"

eval $(minikube docker-env --shell=sh)
minikube version
minikube status
echo 'This command prompt is now aware of Minikube.'
