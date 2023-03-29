FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM openjdk:17.0.2

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/guava-courier.jar

ENTRYPOINT ["java", "-jar", "/app/guava-courier.jar",  "--spring.profiles.active=prod"]
