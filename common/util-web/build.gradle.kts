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

    implementation("org.springframework.boot:spring-boot")
}

tasks.test {
    useJUnitPlatform()
}
