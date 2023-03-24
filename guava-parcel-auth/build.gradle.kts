plugins {
    java
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.liquibase.gradle") version "2.2.0"
}

group = "com.guava"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    liquibase {
        activities.register("main")
        runList = "main"
    }
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.liquibase:liquibase-gradle-plugin:2.2.0")
    }
}

extra["springCloudVersion"] = "2022.0.1"
val testcontainersVersion = "1.17.3"
val modelMapperVersion = "3.1.1"

dependencies {
    // JWT
    implementation("com.auth0:java-jwt:4.3.0")
    // ===
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // ===
    // Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    // ===
    // Liquibase
    implementation("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
    liquibaseRuntime("info.picocli:picocli:4.7.1")
    // ===
    // Postgresql
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    // ===
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // ===
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    testImplementation("org.testcontainers:r2dbc:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
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
