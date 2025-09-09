import com.linecorp.support.project.multi.recipe.configureByLabels

plugins {
    id("io.spring.dependency-management") version Versions.springDependencyManagementPlugin apply false
    id("org.springframework.boot") version Versions.springBoot apply false
    id("io.freefair.lombok") version Versions.lombokPlugin apply false
    id("com.coditory.integration-test") version Versions.integrationTestPlugin apply false
    id("com.linecorp.build-recipe-plugin") version Versions.lineRecipePlugin
}

allprojects {
    group = "com.donation.carebridge"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

configureByLabels("java") {
    apply(plugin = "org.gradle.java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")

    the<JavaPluginExtension>().apply {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    tasks.withType<Test> { useJUnitPlatform() }

    dependencies {
        val implementation by configurations

        val testImplementation by configurations
        val testRuntimeOnly by configurations

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        testImplementation("org.assertj:assertj-core")
        testImplementation("org.mockito:mockito-core")
        testImplementation("org.mockito:mockito-junit-jupiter")
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.springBoot}")
        }
    }
}

configureByLabels("boot") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<Jar>("jar") {
        enabled = false
    }

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = true
        archiveClassifier.set("boot")
    }
}

configureByLabels("library") {
    apply(plugin = "java-library")

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}

configureByLabels("test-logging") {
    dependencies {
        val implementation by configurations
        val testRuntimeOnly by configurations

        implementation("org.slf4j:slf4j-api")
        testRuntimeOnly("ch.qos.logback:logback-classic")
    }
}