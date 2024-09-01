FROM eclipse-eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY /target/wordswap-0.0.0.jar wordswap-0.0.0.jar
EXPOSE 8080
CMD ["java", "-jar", "wordswap-0.0.0.jar"]

