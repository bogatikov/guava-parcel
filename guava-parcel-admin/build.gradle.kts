plugins {
    java
    id("org.springframework.boot") version "2.7.8"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.guava"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.5"
val feignReactorSpringCloudStarter = "3.2.6"
val modelMapperVersion = "3.1.1"

dependencies {
    // JWT
    implementation("com.auth0:java-jwt:4.3.0")
    // ===
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
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
    testImplementation("io.projectreactor:reactor-test")
    // ===
    // Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    // ===
    // Mapper
    implementation("org.modelmapper:modelmapper:${modelMapperVersion}")
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
