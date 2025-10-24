plugins { id("java") }

java {
    // Usa 11 (o 17 si tu JDK es 17)
    toolchain { languageVersion.set(JavaLanguageVersion.of(11)) }
}

repositories { mavenCentral() }

dependencies {
    // --- Appium / Selenium (evita errores de MobileBy, WebDriverWait, etc.)
    implementation("org.seleniumhq.selenium:selenium-java:4.23.0")
    implementation("io.appium:java-client:9.2.3")

    // --- Conexión a BD + logging
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.6")
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11")

    // --- Tests: soporta JUnit 5 y 4 en la misma build
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.2")
}

tasks.test {
    // JUnit 5 + Vintage (para ejecutar JUnit 4 también)
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "failed", "skipped")
    }
}

// ✅ Fuerza compilación en UTF-8 (arregla “unmappable character … windows-1252”)
tasks.compileJava { options.encoding = "UTF-8" }
tasks.compileTestJava { options.encoding = "UTF-8" }

// ⛔️ (Temporal) excluye ImageComparison si aún no vas a usar OpenCV
sourceSets {
    named("test") {
        java {
            exclude("**/ImageComparison.java")
        }
    }
}
