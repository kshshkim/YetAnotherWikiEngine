plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.bootJar {
    isEnabled = true
}

dependencies {
    implementation(project(":common:plain"))
    implementation(project(":common:uuid"))
    implementation(project(":common:util-web"))
    testImplementation(project(":common:util-test"))

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mysql:1.19.3")
}

tasks.test {
    useJUnitPlatform()
}
