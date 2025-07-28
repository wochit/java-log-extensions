plugins {
    java
}

group = "com.newrelic.logging"
version = "3.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.10.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation(project(":core"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
