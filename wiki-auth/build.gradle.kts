plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
