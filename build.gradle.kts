plugins {
    java
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    jacoco
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.bootJar { enabled = false }

allprojects {
    group = "dev.prvt"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.bootJar { enabled = false }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
    }

    tasks.withType<JacocoReport> {
        configurations {
            reports {
                html.required.set(true)
                //            xml.required.set(true)
                //            csv.required.set(true)
            }

            val excludeDirectories = listOf(
                    "**/*Exception.class", // 예외 클래스 제외
            )

            classDirectories.setFrom(
                    files(classDirectories.files.map {
                        fileTree(it).exclude(excludeDirectories)
                    })
            )
        }

        finalizedBy("jacocoTestCoverageVerification")

    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

}
