FROM openjdk:latest
COPY . /var/www/java
WORKDIR /var/www/java/src
RUN javac *.java
RUN jar cf RMIClient.jar .
CMD ["java", "-jar", "RMIClient.jar"]
