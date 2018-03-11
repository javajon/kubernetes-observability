#!/bin/sh
kubectl create -f minikube-tiller-rbac.yaml
helm init --upgrade --service-account cluster-admin
