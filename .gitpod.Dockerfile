FROM java:8 
VOLUME /tmp 
ADD web-websocket-jar-with-dependencies.jar web-websocket.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/web-websocket.jar"]
