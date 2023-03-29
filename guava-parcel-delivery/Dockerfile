FROM openjdk:17.0.2
WORKDIR /app/
ARG JAR_FILE=./build/libs/guava-parcel-delivery-1.0.jar
COPY ${JAR_FILE} .
ENTRYPOINT ["java", "-jar", "guava-parcel-delivery-1.0.jar"]
