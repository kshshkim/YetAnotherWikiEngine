plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common:plain"))
    implementation(project(":common:uuid"))
    implementation(project(":common:util-jpa"))
    testImplementation(project(":common:util-test"))
    implementation(project(":wiki-auth:member"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mysql:1.19.3")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
}

tasks.test {
    useJUnitPlatform()
}
