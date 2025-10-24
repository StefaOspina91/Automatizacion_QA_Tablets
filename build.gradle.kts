plugins {
    java
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Cliente de Appium
    implementation("io.appium:java-client:8.6.0")
    testImplementation("io.appium:java-client:8.6.0")

    // Selenium
    implementation("org.seleniumhq.selenium:selenium-java:4.11.0")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.11.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    // OpenCV .jar local
    implementation(files("libs/opencv-481.jar"))
    testImplementation(files("libs/opencv-481.jar"))

    // SQL Server JDBC driver
    implementation("com.microsoft.sqlserver:mssql-jdbc:9.4.0.jre8")

    // Pool de conexiones
    implementation("com.zaxxer:HikariCP:5.1.0")
    // Logs (API estándar)
    implementation("org.slf4j:slf4j-api:2.0.13")

    // Drivers (SQL)
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11") // SQL Server

    // 🧩 Agrega esta línea para habilitar JUnit 4 (que usa @Test)
    testImplementation("junit:junit:4.13.2")


    // 👉 binding para que SLF4J no quede en NOP
    runtimeOnly("ch.qos.logback:logback-classic:1.5.6")

}

tasks.test {
    useJUnitPlatform()
    useJUnit() // 👈 importante para JUnit 4 en Gradle 8
    testLogging {
        showStandardStreams = true
        events("passed", "failed", "skipped")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
