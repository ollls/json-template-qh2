#docker build -t name:Dockerfile . 
#docker run -p 8443:8443 -t name:Dockerfile
FROM amazoncorretto:22-alpine-jdk

# Install zip for handling JAR files
RUN apk add --no-cache zip unzip

WORKDIR /app
COPY keystore.jks .
COPY target/scala-3.3.3/qh2-http-run.jar .

# Remove signature files that cause JNI errors
RUN mkdir -p temp_dir && \
    unzip qh2-http-run.jar -d temp_dir && \
    rm -f temp_dir/META-INF/*.SF temp_dir/META-INF/*.DSA temp_dir/META-INF/*.RSA && \
    cd temp_dir && \
    zip -r ../fixed-app.jar . && \
    cd .. && \
    rm -rf temp_dir && \
    mv fixed-app.jar qh2-http-run.jar

EXPOSE 8443
CMD java -jar qh2-http-run.jar
