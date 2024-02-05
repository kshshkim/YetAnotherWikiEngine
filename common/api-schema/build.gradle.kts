plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-context")
}

tasks.test {
    useJUnitPlatform()
}
