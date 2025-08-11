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
    implementation ("io.appium:java-client:8.6.0'")
    testImplementation ("io.appium:java-client:8.6.0")

    implementation ("org.seleniumhq.selenium:selenium-java:4.11.0")
    testImplementation ("org.seleniumhq.selenium:selenium-java:4.11.0")
    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    // OpenCV .jar local
    implementation(files("libs/opencv-481.jar"))
    testImplementation(files("libs/opencv-481.jar"))

    // Driver JDBC para SQL Server
    implementation("com.microsoft.sqlserver:mssql-jdbc:9.4.0.jre8")
}




tasks.test {
    useJUnitPlatform()
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
