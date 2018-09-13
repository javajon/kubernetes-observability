## Logging with Kubernetes and EFK ###

This is a quick example to help you get started with logging on Kubernetes with Elasticsearch, Fluentd and Kibana. These instructions are specific toward [Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/), but can easily be adjusted for other Kubernetes clusters.

### Install Logging Solution Stack ###
```
git clone https://github.com/prameswar/efk-helm
cd efk-helm
helm init && helm init update
helm install --name fluentd fluentd --set elaticsearch.host=elasticsearch.logging     (<- leave misspelling as-is)
helm install --namespace logging --name elasticsearch elasticsearch
helm install --namespace logging --name kibana kibana 
```

### Generate Logging Events ###

This container will continuously generate random logging sample events.
```
kubectl run random-logger --image=chentex/random-logger --namespace=logging
```

### Explore Kibana Dashboard ###

The Kibana service takes a few minutes to initialize.

```
minikube service kibana --namespace logging
```

Once you get to the Kibana web interface under the Management Index Patterns change the filter from 'logstash-*' to '*'. Uncheck "Index contains time-based events" and click "Create". Next, click "Discover" and select the "kubernetes.pod_name" under "Available Fields". In the sub list of nodes, find the "random-logger-xxx" and click on the plus button. The logs from the logger will then list. Further Kibana dashboard configurations can be explored from here.