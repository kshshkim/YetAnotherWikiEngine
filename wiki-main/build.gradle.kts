plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    isEnabled = true
}

dependencies {
    implementation(project(":common:plain"))
    implementation(project(":common:uuid"))
    implementation(project(":common:util-web"))
    implementation(project(":common:util-jpa"))
    implementation(project(":common:api-schema"))
    testImplementation(project(":common:util-test"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.security:spring-security-test")

    // testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mysql:1.19.3")

    // BCRYPT
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    // Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    implementation("org.jetbrains:annotations:24.1.0")
}

tasks.compileJava {
    options.compilerArgs.add("-Aquerydsl.generatedAnnotationClass=com.querydsl.core.annotations.Generated")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
