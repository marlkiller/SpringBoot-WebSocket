FROM java:8 
VOLUME /tmp 
COPY target/web-websocket.jar .
ADD web-websocket.jar /web-websocket.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/web-websocket.jar"]
