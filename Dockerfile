#docker build -t name:Dockerfile . 
#docker run -p 8443:8443 -t name:Dockerfile
FROM amazoncorretto:22-alpine-jdk
WORKDIR /app
COPY  keystore.jks .
COPY target/scala-3.3.3/qh2-http-run.jar .
EXPOSE 8443
CMD java -jar qh2-http-run.jar
