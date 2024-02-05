plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.uuid:java-uuid-generator:4.2.0")
}

tasks.test {
    useJUnitPlatform()
}
