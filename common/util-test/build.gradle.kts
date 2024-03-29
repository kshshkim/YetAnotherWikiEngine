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
    implementation("net.bytebuddy:byte-buddy:1.14.10")
}

tasks.test {
    useJUnitPlatform()
}
