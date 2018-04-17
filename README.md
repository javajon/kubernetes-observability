# Monitoring Clusters and Containers #

Kubernetes is a complex container management system. Your application running in containers is also a complex system as it embraces the distributed architecture of loosely coupled, highly cohesive services. As these modular containers run, things may not always behave as smoothly as you hope. Embracing the notions of antifragility and designing a system to be resilient despite the realities of resource limitations, network failures, hardware failures and failed software logic. All of this demands a robust monitoring system to open views into the behaviors and health of your applications running in a cluster.

Three important types of monitoring are metric data events, log streams and tracing. Over time there are services that gather, store and export the metrics and logs. There are Time Series Databases (TSDBs) that append each temporal piece of data. There are monitors that evaluate alerting rules against key changes in data. Lastly, there are dashboards and reports offering views into states based on the metrics and logs gathered during a time range. Ultimately, to provide viewports into the state and health of your cluster and its hosted applications.

For metrics and data events in Kubernetes it is common to use:
- Prometheus to gather runtime data events and fire alerts
- Alertmanager to gather and route alerts based on changing data
- Grafana to visually aggregate and decipher the Prometheus data.

For logging in Kubernetes it's common to use:
- Fluentd to gather the various log streams across the containers
- ElasticSearch to store the collated stream data into a searchable time series database
- Kabana to visually interact with frequent searches against the ElasticSearch data.

For tracing in Kubernetes it's possible to use:
- ZOP stack (ZipKin, OpenTracing, Prometheus)
- JOP stack [(Jaeger, OpenTracing, Prometheus)](http://www.hawkular.org/blog/2017/06/26/opentracing-appmetrics.html)


## What these following instructions do? ##

- Start a personal Kubernetes cluster
- Add a private Docker registry along with a dashboard
- Build and push 3 Spring Boot microservices to the registry
- Add a monitoring tool stack with Prometheus, Alertmanager and Grafana
- Start the 3 microservices on Kubernetes
- Monitor the microservices metrics through Prometheus and Grafana

-----------
## Setup ##

### How do I get set up? ###

#### GitHub project ####
- Clone this project from GitHub

#### Install Kubernetes, a container manager ####
- Install [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/). Other Kubernetes solutions are available, however these steps below are specific to Minikube). Do not start Minikube, just install the command line tool and VirtualBox. Below the script will start Minikube with the correct sizing and configuration.
- Install [KubeCtl] (https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- From project root run `./start.sh`. This provisions a personal Kubernetes cluster with a few addons for you.
- Verify `minikube status` and `kubectl version` run correctly

#### Install Helm, a Kubernetes package manager ####

The typical tool used to interact with Kubernetes is KubeCtl. However, the Helm tool further simplifies deployments and makes the provisioning experience more consistent. The quick start doc provides the best instructions:

- Install the Kubernetes package manager [Helm](https://docs.helm.sh/using_helm/).

Helm is a command line tool that also installs a small complimentary service called Tiller onto your cluster. To setup Tiller for this demonstration run the shell script from within the `helm` directory:

```
cd helm
helm-init-rbac.sh
```

This enables Tiller and gives permissions to add the registry UI and monitoring tool stack in the next steps.

At this point serveral pods are starting and it takes a few minutes before the deployment is healthy. The startup progress can be observed in the kube-system namespace in the dashboard.
```
kubectl get pods -n kube-system
or
minikube dashboard
```

--------------------
## Container Registry ##
### Enhanced UI for private Docker registry ###

There is a Minikube addon called *registry* that was enabled start.sh script. It's a private Docker registry, but it lacks a user interface. There is a container that fronts this registry with a helpful dashboard. Start the [Docker registry UI](https://github.com/mkuchin/docker-registry-web) from the project `helm` directory by running:

```
helm install charts/docker-registry-web --namespace kube-system --name ui
```

In a few minutes a service allots access to a browser based UI. Here you can verify the dashboard service is running and displays the registry contents. At this step the registry is empty but shortly you will deploy three microservices to this Docker registry and are listed with this command:

```
minikube service -n kube-system ui-docker-registry-web
```
The above command opens your default browser to this Docker registry UI page.

As an aside topic, this browser URL brings you to the same page:

```
http://registry-ui.minikube.st81ess.com
```
This uses a public DNS server that maps minikube.st81ess.com to 192.168.99.100 (the likely, but not guaranteed, Minikube IP on VirtualBox) then with an [ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) it maps registry-ui to the service.


--------------------
## Microservices ##

### Deploy microservices for monitoring ###

There are three Spring Boot microservices that communicate with each other: Quotes, Authors and Biographies. The above start.sh script enables the Minikube addon called "registry". This is a private Docker registry running as a container in your cluster. A simple script builds and deploys the 3 microservices and their Docker images to this registry. In the project directory `<project>/microservices` there is the source for the microservices and a script that builds and pushes the Docker images. Run:

```
./pushImages.sh
```

and it runs the Gradle task `pushImages` on each microservice project. If exceptions occur when attempting to push the image to the registry make sure you have run the `env.sh` script at the command line.

Explore the docker.gradle file for each service to see how the plugin builds, tags and pushes the Docker images. Once the images are pushed verify they are in the registry with the command:

```
curl -X GET $(minikube service -n kube-system registry --url)/v2/_catalog
```

This curl request returns: {"repositories":["authors","biographies","quotes"]}

Observe the 3 microservice Docker images in the registry UI:

```
minikube service -n kube-system ui-docker-registry-web
```

-----------------
## Monitoring ###
### Install Prometheus ###

The GitHub project [coreos/prometheus-operator](https://github.com/coreos/prometheus-operator) provides a configured Grafana dashboards and Prometheus settings for many Kubernetes clusters. It also has documented installation procedures that
provision your cluster, even a small one on Minikube is an effective monitoring solution.

Before continuing ensure the Helm Tiller is configured by running the script mentioned above `helm\helm-init-rbac.sh`.

There are two key charts combined to offer a helpful monitoring stack: prometheus-operator and kube-prometheus. To install these charts run this script:

```
cd ../configurations
deploy-prometheus-stack.sh
```
You will see a list of exposed http endpoints for Prometheus, Alertmanager and Grafana. The containers become available after downloaded and initialized. Observe the resources created for this monitoring stack:

```
minikube service list
or
minikube dashboard
```


### Start microservices ###

Now that microservice images are deployed to the private registry, the services can now start. Start the 3 microservice containers by changing to the `helm` directory and running this install:

```
cd helm
helm install charts/microservices --namespace quotes --name ms
```

Next, tell Prometheus there is a new target for metrics to scrape
```
cd configurations
kubectl create -n monitoring -f prometheus-targets.yaml
```

### Exercise microservices ###

This project contains three microservices based on Java Spring Boot. They provide JSON based REST APIs and coordinate to return random famous quotes, biographies and authors from the respective services.

To view the 3 microservices exposed via their service's NodePorts:

```
minikube service list -n quotes
```

While services are instantly available, the pods may take longer to enter their ready states.

```
curl $(minikube service -n quotes ms-authors     --url)/author/random
curl $(minikube service -n quotes ms-biographies --url)/bio/random
curl $(minikube service -n quotes ms-quotes      --url)/quote/random
```

Invoking a get /quotes/full on the ms-quotes service returns a random author's small biography and a random quote from their notable curation.

```
curl $(minikube service -n quotes ms-quotes --url)/quote/full
```

This invokes the 3 microservices in a chained transaction. Each REST request produces a random quote from a random author.

Observe the metrics for each service at the relative REST location `/metrics`.

```
curl $(minikube service -n quotes ms-quotes --url)/metrics
```

### Explore Prometheus ###

As you are exercising these images the monitoring data is being collected by Prometheus. Access the three monitoring dashboards with these commands:

```
minikube service -n monitoring kube-prometheus-prometheus
minikube service -n monitoring kube-prometheus-alertmanager
minikube service -n monitoring kube-prometheus-grafana
```

Activity in the namespace 'quotes' is observed in these interfaces as the services are exercised. Pod replications can be increased and the services can be requested to generate metrics.

```
while true; do curl -w " ms: %{time_total}\n" $(minikube service -n quotes ms-quotes --url)/quote/full; done
```

The class MetricStatics defines the exported metrics such as counters for `http_requests_total`. These changing metrics are observed in Prometheus as the services are requested.

Next, increase the scale value for each microservice pod to 3:

```
helm upgrade --set authors.replicaCount=3 --namespace quotes ms ./microservices
```

Grafana shows the scaling of the pods. Run the above command line observe the faster response times.

Next, increase the latency of the Authors service:

```
helm upgrade --set authors.container.latency=599 --namespace quotes ms ./microservices
```

Error metrics can be observed with

```
curl $(minikube service -n quotes ms-quotes --url)/forceError
```

The JVM metrics are present because of the line: `DefaultExports.initialize();`

Prometheus scrapes all these metrics with the relative `/metrics` call. If the service exposes a URL for metrics, then it listed in the Prometheus "Targets" catalog. The URL definition is defined in the spring-beans-config.xml Spring Boot file for each microservice project.

-------------
## Sub finem ##
Reflect on what you have just explored. You stood up a personal cluster with a private Docker registry. You deployed and started three microservices wrapped in Docker containers. Next, you stood up a monitoring system and observed how the behaviors of the microservices are tracked with Prometheus metrics. Along the way you have seen examples of Spring Boot, Gradle, Helm and the helpful features Kubernetes.

My hope is this gives you a deeper appreciation of Kubernetes as a powerful software architecture movement.  -- Jonathan Johnson

### Technology stack ###

* VirtualBox 5.2.8
* Minikube 0.25.2 (Kubernetes + Docker)
* Kubectl 1.10.0
* Helm 2.8.2
* Prometheus Operator
* Kube-Prometheus (Alertmanager + Grafana)
* Java 1.8
* Spring Boot 2.0.1
* Gradle 4.6 and a few helpful plugins for building and deploying containers

### Project roadmap ###
* Add YAML for Alertmanager rules.
* Add logging: fluentd, ElasticSearch, Kabana via the EFK addon in Minikube
* Add tracing: ZipKin and Sleuth for demonstration of tracing solutions
* Defect: There is an monitoring umbrella chart in this project that installs both prometheus-operator and kube-prometheus, but it's failing in its current form. Normally it's invoked with:
```
kubectl create namespace monitoring
cd helm
helm repo add coreos https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/
helm install charts/monitoring --namespace monitoring --name mon
```

### Presentation short instructions ###
#### Pre-talk setup ####
| Step                     | Command
|--------------------------|---------
| Fresh Minikube           | `minikube delete`
| Initialize               | `./start.sh`
| CLI env                  | `. ./env.sh`
| Init Helm                | `cd helm && ./helm-init-rbac.sh`
| Tiller startibf ~15 min. | `helm status`
| Start Docker UI          | `helm install charts/docker-registry-web --namespace kube-system --name ui`
| Change dir               | `cd ../configurations`
| Start Docker UI          | `./deploy-prometheus-stack.sh`
| Start Prometheus         | `kubectl create -n monitoring -f prometheus-targets.yaml`
| Publish microservices    | `cd microservices && ./pushImages`

#### Demonstrate ####

| Step                     | Command
|--------------------------|---------
| Start microservices      | `cd ../helm && helm install charts/microservices --namespace quotes --name ms`
| Extract url       | `export SERVICE=$(minikube service -n quotes ms-quotes --url) && echo $SERVICE`
| Exercise services        | `curl -w " ms: %{time_total}\n" $SERVICE/quote/full`
| Loop services            | `while true; do curl -w " ms: %{time_total}\n" $SERVICE/quote/full; done`
| Loop and watch time      | `while true; do curl -w " ms: %{time_total}\n" $SERVICE/quote/full : head -c10; done`

Observe in Prometheus the statistic `http_requests_total` growing.
### References ###

* Visit the [No Fluff Just Stuff tour](https://www.nofluffjuststuff.com/home/main) and explore many ideas like this. [Monitoring Clusters and Containers](https://archconf.com/conference/clearwater/2017/12/session?id=40272)
* [Installing the Prometheus Operator, a CoreOS open source contribution](https://github.com/coreos/prometheus-operator)
* [Spring Boot correlationIds](https://blog.jdriven.com/2017/04/correlate-services-logging-spring-boot)
* Thank you [Max Kuchin](https://github.com/mkuchin) for the [Docker Registry Web container](https://github.com/mkuchin/docker-registry-web).
* This solution is inspired from the [Prometheus installation provided by CoreOS Tectonic.](https://coreos.com/tectonic/docs/latest/tectonic-prometheus-operator/tectonic-monitoring.html)
* [Chris Ricci, Solutions Engineer, CoreOS/Red Hat](https://www.linkedin.com/in/christopher-ricci) provides a [helpful demonstration of Prometheus](https://www.brighttalk.com/webcast/14601/293915). Also, a talk on the [advancements in version 2](https://www.brighttalk.com/webcast/14601/289815).
