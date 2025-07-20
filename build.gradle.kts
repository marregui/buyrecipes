plugins {
    id("java")
    id("jacoco")
    alias(libs.plugins.micronaut.application)
}

group = "co.piter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

micronaut {
    version(libs.versions.micronautFramework.get())
    processing {
        incremental(true)
        annotations("co.piter.buyrecipes.*")
    }
}

application {
    mainClass.set("co.piter.buyrecipes.Application")
}

dependencies {
    annotationProcessor(libs.bundles.micronaut.processors)

    implementation(libs.bundles.micronaut.core)
    implementation(libs.bundles.micronaut.data)
    implementation(libs.micronaut.openapi)
    implementation(libs.jetbrains.annotations)
    compileOnly(libs.swagger.annotations)
    runtimeOnly(libs.bundles.runtime)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.testing)
    testAnnotationProcessor(libs.micronaut.inject.java)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("cleanupDatabase")
}

tasks.register("cleanupDatabase") {
    group = "cleanup"
    description = "Clean up H2 database files that might be locked"
    doLast {
        delete(fileTree("data") {
            include("*.mv.db", "*.trace.db")
        })
        println("Cleaned up H2 database files")
    }
}