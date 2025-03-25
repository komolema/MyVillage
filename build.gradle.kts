import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.village"
version = "1.0-SNAPSHOT"

val koinVersion = "4.0.2"
val exposedVersion = "0.59.0"
val arrowVersion = "2.0.1"
val pdfboxVersion = "2.0.29"
val javafxVersion = "21.0.2"

repositories {
    mavenCentral()
    google()
}

application {
    mainClass.set("MainKt")
}

javafx {
    version = javafxVersion
    modules("javafx.controls", "javafx.fxml", "javafx.web", "javafx.graphics")
}

dependencies {
    // JavaFX dependencies
    implementation("org.controlsfx:controlsfx:11.1.2")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("org.jdatepicker:jdatepicker:1.3.4")

    // TornadoFX - Kotlin wrapper for JavaFX
    implementation("no.tornado:tornadofx:1.7.20")

    // Coroutines for JavaFX
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.7.3")

    // Exposed ORM and SQLite
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")

    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    // Arrow dependencies
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    // Koin for dependency injection
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-test:$koinVersion")
    implementation("io.insert-koin:koin-test-junit5:$koinVersion")

    // PDF handling with Apache PDFBox
    implementation("org.apache.pdfbox:pdfbox:$pdfboxVersion")
    implementation("org.apache.pdfbox:fontbox:$pdfboxVersion")

    // BCrypt for password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.0")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile> {
    targetCompatibility = "17"
    sourceCompatibility = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
