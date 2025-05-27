FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM --platform=linux/amd64 openjdk:17-alpine

WORKDIR /usr/src/app

COPY --from=builder /usr/src/app/target/*.jar /usr/src/app/api-clean-bot.jar 

CMD ["java", "-jar", "/usr/src/app/api-clean-bot.jar"]