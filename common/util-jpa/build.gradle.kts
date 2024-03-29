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
    testImplementation(project(":common:util-test"))

    implementation("org.hibernate.orm:hibernate-core")
}

tasks.test {
    useJUnitPlatform()
}
