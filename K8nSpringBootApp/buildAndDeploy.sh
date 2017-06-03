#!/usr/bin/env bash
./mvnw clean package dockerfile:build \
&& kubectl apply -f KubernetesObjects.yaml \
&& kubectl patch deployment k8n-spring-boot-app-deployment -p \
"{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"refreshTimestamp\":\"`date +'%s'`\"}}}}}"
