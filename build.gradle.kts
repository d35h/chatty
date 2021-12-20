import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.openapi.generator") version "5.3.0"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}

group = "cz.daniil.zaru"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.swagger:swagger-annotations:1.6.3")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("io.springfox:springfox-swagger2:3.0.0")
    implementation("org.openapitools:jackson-databind-nullable:0.2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.32.0")
    testImplementation("io.mockk:mockk:1.12.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val apiCodeGeneratedDir = "${project.buildDir}/generated"

java {
    sourceSets["main"].apply {
        java.srcDir("$apiCodeGeneratedDir/src/main/java")
    }
}

openApiGenerate {
    generatorName.set("spring")
    configOptions.apply {
        put("reactive", "true")
        put("delegatePattern", "true")
    }
    inputSpec.set("${project.rootDir}/src/main/resources/openapi.yaml")
    generateApiTests.set(false)
    generateModelTests.set(false)
    outputDir.set(apiCodeGeneratedDir)
    apiPackage.set("cz.daniil.zaru.chatty.api")
    modelPackage.set("cz.daniil.zaru.chatty.model")
    ignoreFileOverride.set("${project.rootDir}/src/main/.openapi-generator-ignore")
}

tasks.create("removeOpenApiTools", Delete::class) {
    delete(files("$apiCodeGeneratedDir/src/main/java/org/openapitools"))
}

tasks.getByName("openApiGenerate").finalizedBy("removeOpenApiTools")
tasks.getByName("compileKotlin").dependsOn(tasks.getByName("openApiGenerate"))
