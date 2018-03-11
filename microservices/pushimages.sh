#!/bin/sh
set -ex

cd quotes && ./gradlew pushImage && cd ..
cd biographies && ./gradlew pushImage && cd ..
cd authors && ./gradlew pushImage && cd ..

curl -X GET $(minikube service -n kube-system registry --url)/v2/_catalog

set +ex && printf '\nAll the microservices are now in the Docker container registry on the cluster.\n'
