plugins {
    java
    id("org.springframework.boot") version "2.7.10"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.liquibase.gradle") version "2.2.0"
}

group = "com.guava"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.6"
val feignReactorSpringCloudStarter = "3.2.6"
val modelMapperVersion = "3.1.1"
val testcontainersVersion = "1.17.3"
val wiremockVersion = "2.35.0"

dependencies {
    // JWT
    implementation("com.auth0:java-jwt:4.3.0")
    // ===
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(project(mapOf("path" to ":guava-parcel-courier")))
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:${feignReactorSpringCloudStarter}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // ===
    // Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    // ===
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // ===
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    testImplementation("org.testcontainers:kafka:${testcontainersVersion}")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wiremockVersion")
    // ===
    // Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    // ===
    // Mapper
    implementation("org.modelmapper:modelmapper:${modelMapperVersion}")
    // ===
    // Kafka
    implementation("io.projectreactor.kafka:reactor-kafka")
    // ===
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-webflux-core:1.6.15")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.15")
    // ===
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
