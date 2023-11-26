FROM openjdk:17-jdk-alpine
LABEL authors="momongo12"

WORKDIR /app

COPY fintech/build/libs/fintech-1.0-SNAPSHOT.jar fintech.jar

CMD ["java", "-jar", "fintech.jar"]