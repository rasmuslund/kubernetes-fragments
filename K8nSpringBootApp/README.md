* Build maven project: ```mvn clean package```
* Copy Dockerfile into target dir: ```cp ./src/main/docker/Dockerfile ./target```
* Change to target dir: ```cd /target```
* Make sure Minikube is running: ```minikube start```
* Switch to using Minikube's Docker deamon: ```eval $(minikube docker-env)```
  * Note: Later (don't do it now) you can switch back to use your host's Docker deamon with: ```eval $(minikube docker-env -u)```
* Build a Docker image: ```docker build -t k8n_spring_boot_app:v1 .```
  * Repository path will be: k8n_spring_boot_app
  * Tag will be: v1
* Optionally see docker images: ```docker images```
* Copy K8s file into target dir: ```cp ../src/main/kubernetes/K8nSpringBootApp.yaml .```
* Apply the K8s resources file to minikube: ```kubectl apply -f ./K8nSpringBootApp.yaml```
* Optionally see K8s resources:
  * ```kubectl get pods```
  * ```kubectl get deployments```
  * ```kubectl get services```
* Find IP of ingress: Find IP of ingress: ```kubectl describe ingress```
* Access the service on: ```http://<ingress-ip>/spring/messages/random```
* Follow logs: ```kubectl log -f k8n-spring-boot-app-deployment-...``` (tab to see pod names)
