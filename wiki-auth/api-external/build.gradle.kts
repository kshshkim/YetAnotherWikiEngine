plugins {
    java
}

group = "dev.prvt.yawiki"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":wiki-auth:member"))
    implementation(project(":wiki-auth:auth-processor"))
    implementation(project(":common:api-schema"))
    implementation(project(":common:plain"))
    testImplementation(project(":common:util-test"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.test {
    useJUnitPlatform()
}
