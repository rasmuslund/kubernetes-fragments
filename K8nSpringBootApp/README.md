* Required tool installed:
  * JDK 1.8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  * VirtualBox (https://www.virtualbox.org/wiki/Downloads)
  * minikube (https://github.com/kubernetes/minikube#installation)
  * kubectl (https://kubernetes.io/docs/tasks/tools/install-kubectl/)
  * (Docker is probably not required - but nice to be able to run Docker commands)
* Steps below works on Linux - don't know about other OS'es
* Make sure Minikube is running: ```minikube start```
* Switch to using Minikube's Docker deamon: ```eval $(minikube docker-env)```
  * Note: Later (don't do it now) you can switch back to use your host's Docker deamon with: ```eval $(minikube docker-env -u)```
* Build maven project (Spring Boot fat JAR and docker image containing the fat JAR): ```./mvnw clean package dockerfile:build```
  * where ```clean package``` builds the Spring Boot fat jar
  * and ```dockerfile:build``` builds the docker image (and puts it into the Minikube's Docker Deamon's image cache)
    * the ```dockerfile:build``` is provided by the ```com.spotify:dockerfile-maven-plugin``` registered in ```pom.xml```
    * the name/repository path of the image is specified in the ```com.spotify:dockerfile-maven-plugin``` plugin config in ```pom.xml```
    * the tag of the image will be ```latest``` (the default)
    * running ```./mvnw dockerfile:build``` is therefore equivalent to running: 
    ```docker build -t test/k8n-spring-boot-app:latest .```
* Optionally see docker images: ```docker images```
* Optionally see info about an image: ```docker inspect test/k8n-spring-boot-app:latest```, e.g.:
  * ExposedPorts
  * Env (env vars)
  * Entrypoint (process started in the container)
* Apply the K8s resources file to minikube: ```kubectl apply -f KubernetesObjects.yaml```
* A K8s deployment will not do a rolling update, unless either the image path:tag or something in the pod specification has changed
  * It is annoying to change the path:tag of the image, when deploying on a local minikube (although it makes perfect sense for real deploys
    to a K8s cluster)
  * One can force a rolling update/redeploy by adding a modified label to the pod specification via a patch command - e.g.: 
    ```kubectl patch deployment k8n-spring-boot-app-deployment -p "{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"refreshTimestamp\":\"`date +'%s'`\"}}}}}"```
* Optionally see K8s resources:
  * ```kubectl get pods```
  * ```kubectl get deployments```
  * ```kubectl get services```
* Optionally watch pods while a rolling update/redeploy happens:
  * ```watch kubectl get pods```
  * or ```kubectl get pods -w``` (messes up the whitespace formatting)
* Find IP of ingress: Find IP of ingress: ```kubectl describe ingress```
* Access the service on: ```http://<ingress-ip>/spring/messages/fixed```
* Follow logs: ```kubectl log -f k8n-spring-boot-app-deployment-...``` (tab to see pod names)
  * ```-f``` for ```follow```
* To test redeploy, this is an easy change to make to the app:
  * Change the string returned by: ```FixedMessageEndpoint.java```
  * Remember to also change the test: ```FixedMessageEndpointTest.java```
* Complete build and redeployment in one go (also in ```buildAndDeploy.sh```):
  ```
    ./mvnw clean package dockerfile:build \
    && kubectl apply -f KubernetesObjects.yaml \
    && kubectl patch deployment k8n-spring-boot-app-deployment -p \
    "{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"refreshTimestamp\":\"`date +'%s'`\"}}}}}"
  ```

