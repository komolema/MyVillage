import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "org.village"
version = "1.0-SNAPSHOT"

val koinVersion = "4.0.2"
val exposedVersion = "0.59.0"
val arrowVersion = "2.0.1"
val kotestVersion = "6.0.0.M2"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    // Compose dependencies
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
    implementation("io.github.epicarchitect:epic-calendar-compose:1.0.8")
    implementation("org.jdatepicker:jdatepicker:1.3.4")

    // Coroutines for Swing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")


    // Exposed ORM and SQLite
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")


    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    // Arrow dependencies
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    implementation("com.seanproctor:data-table:0.10.1")

    // Koin for dependency injection
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("io.insert-koin:koin-test:$koinVersion")
    implementation("io.insert-koin:koin-test-junit5:$koinVersion")

    implementation("io.insert-koin:koin-compose:$koinVersion")
    implementation("io.insert-koin:koin-compose-viewmodel:$koinVersion")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koinVersion")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MyVillage"
            packageVersion = "1.0.0"
        }
    }
}
