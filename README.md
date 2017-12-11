# Monitoring Clusters and Containers #

Kubernetes is a complex container management system. Your application running in containers is also a complex system
as it embraces the distributed architecture of highly modular and cohesive services. As these containers run things may
not always run as smoothly as you hope. Embracing the notions of antifragility and designing a system to be resilient
despite the realities of resource limitations, network failures, hardware failures and failed software logic. All of this
demands a mature and robust monitoring system to give views into the behaviors and health of your applications
running in a cluster.

Three important types of monitoring are metric data events, log streams and tracing. Over time there are services that 
gather, store and export the metrics and logs. There are time series databases that append each temporal piece of data. 
There are monitors that evaluate alerting rules against key changes in data. Lastly, there are dashboards and reports 
offering views into states based on the metrics and logs gathered during a time range. Ultimately, to provide you 
viewports into the state and health of your cluster and its hosted applications.

For metrics and data events in Kubernetes it's common to use:
- Prometheus to gather runtime data events and fire alerts
- Alertmanager to gather and route alerts based on changing data
- Grafana to visually aggregate and decipher the Prometheus data

For logging in Kubernetes it's common to use:
- Fluentd to gather the various log streams across the containers
- ElasticSearch to store the collated stream data into a searchable time series database
- Kabana to visually interact with frequent searches against the ElasticSearch data

For tracing in Kubernetes it's possible to use:
- ZOP stack (ZipKin, OpenTracing, Prometheus) 
- JOP stack (Jaeger, OpenTracing, Prometheus) (http://www.hawkular.org/blog/2017/06/26/opentracing-appmetrics.html)


## What will these following instructions do? ##

- Start a personal Kubernetes cluster
- Add a private Docker registry with a UI to Kubernetes 
- Add Prometheus, Alertmanager and Grafana to Kubernetes
- Compile and push 3 microservices to the private registry
- Start the 3 microservices on Kubernetes
- Monitor the microservices metrics through Prometheus
- Roadmap: Logging demonstration
- Roadmap: Tracing demonstration


## How do I get set up? ##

- Clone this project from GitHub
- Install [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) (or any other Kubernetes solution)
  <br>Try not to start Minikube, just install the CLI tool and VirtualBox. Below the start script will start it.
- Install Kubectl and verify `kubectl version` runs correctly
- From project root run `./start.sh`. This will provision a personal Kubernetes cluster for you.


## Enhanced UI for the private Docker Registry ##

There is a Minikube addon called "registry" that has been enabled. It is a private Docker registry,
but it lacks a user interface. There is a container that will front this registry with a helpful UI.
From the project root run 

`
kubectl create -f cluster/registry-ui
`

In a few minutes a service will give you access to a browser based UI. Here you can verify the three microservices 
images have been deployed to the cluster's private Docker registry. To see the UI run

`
minikube service -n kube-system registry-ui
`

-----------------
## Prometheus ###
### Installing Prometheus Operator ###

The Github project [coreos/prometheus-operator](https://github.com/coreos/prometheus-operator) 
provides a well configured Grafana dashboards and Prometheus settings for many Kubernetes 
clusters. It also has documented installation procedures that can provision you cluster, even 
a small one on Minikube with an effective monitoring solution.

From the readme in that project these shortened steps can be followed.

Follow these steps to install Prometheus to Minikube:

`cd cluster/prometheus`

Get the Prometheus operator

`git clone https://github.com/coreos/prometheus-operator`

`cd prometheus-operator`

Deploy the Prometheus operator

`
kubectl create namespace monitoring
`

`
kubectl apply -n monitoring -f bundle.yaml
`

### Install Prometheus ###

`cd contrib/kube-prometheus`

`hack/cluster-monitoring/minikube-deploy`

Observe all the resources that get created. 

--------------------
## Microservices ##

### Deploy some microservices to be monitored ###

There are three SpringBoot microservices that communicate with each other: 
Quotes, Authors and Biographies. The above start.sh script enables the Minikube addon 
called "registry". This is a private Docker registry running as a container in your 
cluster. Build and deploy the 3 microservices and their Docker images will be pushed to
this registry. In the project root there is a script 

`./pushImages.sh` 

that will run the Gradle task `pushImages` on each microservice project. You can explore
the docker.gradle file for each service to see how the plugin builds, tags and pushes the
Docker images. Once the images are pushed you can verify they are now in the registry 
with the command:

`
curl -X GET $(minikube service -n kube-system registry --url)/v2/_catalog
`

This curl request will return: {"repositories":["authors","biographies","quotes"]}

You can also see the 3 microservice Docker images in the registry UI:

'
minikube service -n kube-system registry-ui
'


### Start the microservices ###

Only the microservices images have been deployed to the private registry, the services now 
need to be started. Create a namespace where the microservice containers will run.

`
kubectl create namespace quotes
`

Now, into that namespace, deploy the 3 microservice containers

`
kubectl apply -f cluster/microservices
`


### What do these microservices do? ###

This project contains three microservices based on Java SpringBoot. They provide JSON based REST APIs and 
coordinate to return random famous quotes, biographies and authors from the respective services. 

You can now see those 3 services are not exposed via a NodePort:

'minikube service list -n quotes`  

While the services are instantly ready, it will take a minute before the pods are ready.           

`curl $(minikube service -n quotes authors     --url)/author/random`
`curl $(minikube service -n quotes biographies --url)/bio/random`
`curl $(minikube service -n quotes quotes      --url)/quote/random`

Invoking a get /quotes/full on the Quotes service will return a random author's small biography and a random 
quote from their notable curation.

`curl $(minikube service -n quotes quotes --url)/quote/full`

This is a invokes the 3 microservices in a chained transaction. Each REST request will produce a random quote 
from a random author.

You can also observe the metrics for each service at the relative REST location "/metrics".

`
curl $(minikube service -n quotes quotes --url)/metrics
`

### Intracting with Prometheus ###

It will take a few minutes for the containers to download and start. You can
observe them starting in dashboard in the monitoring namespace with 

`
kubectl -n monitoring get deployment,svc,pod,StatefulSet
`
or

`minikube dashboard`

Once running you can access the three UI dashboard with these commands:

`
minikube service -n monitoring prometheus-k8s
`

`
minikube service -n monitoring alertmanager-main
`

`
minikube service -n monitoring grafana
`

Activity in the namespace 'quotes' can be observed in these interfaces as the services are 
excercized. Pod replications can be increased and the services can be requested to generate metrics. 

`
while true; do curl -w " ms: %{time_total}\n\n" $(minikube service -n quotes quotes --url)/quote/full; done
`

The class MetricStatics defines the exported metrics such as counters for "http_requests_total". These 
changing metrics can be observed in Prometheus as the services are requested. 

Next, increase the scale value for each microservice pod to 3 in the Kubernetes dashboard. Grafana will show the
scaling of the pods. Run the above command line and determine if there are faster response times.

Error metrics can be observed with 
  
`
curl $(minikube service -n quotes quotes --url)/forceError
`



### Technology stack ###

* Kubernetes and Docker with Minikube
* Java
* Gradle and a few helpful plugins
* SpringBoot
* Prometheus
* Alert Manager
* Grafana
* (roadmap: sleuth, fluentd, ElasticSearch, Kabana)
* (roadmap: ZipKin)


### Additional information ###

* Visit the [No Fluff Just Stuff tour](https://www.nofluffjuststuff.com/home/main) and see this example in action. [Monitoring Clusters and Containers](https://archconf.com/conference/clearwater/2017/12/session?id=40272)
* [Installing the Prometheus Operator, a CoreOS open source contribution](https://github.com/coreos/prometheus-operator)
* [SpringBoot correlationIds](https://blog.jdriven.com/2017/04/correlate-services-logging-spring-boot),
* This solution was inspired from the opinionated [Prometheus installation provided by CoreOS Tectonic.](https://coreos.com/tectonic/docs/latest/tectonic-prometheus-operator/tectonic-monitoring.html)
