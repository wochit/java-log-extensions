plugins {
    java
    id("com.github.spotbugs").version("5.2.1")
}

group = "com.newrelic.logging"

// -Prelease=true will render a non-snapshot version
// All other values (including unset) will render a snapshot version.
val release: String? by project
val releaseVersion: String by project
version = releaseVersion + if ("true" == release) "" else "-SNAPSHOT"

repositories {
    mavenCentral()
}

val includeInJar: Configuration by configurations.creating
configurations["compileOnly"].extendsFrom(includeInJar)

dependencies {
    implementation("io.dropwizard:dropwizard-logging:4.0.13")
    implementation("io.dropwizard:dropwizard-request-logging:4.0.13")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("com.newrelic.agent.java:newrelic-api:8.9.1")
    implementation("ch.qos.logback:logback-access:1.4.14")
    includeInJar(project(":logback")) {
        isTransitive = false
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation(project(":logback"))
}

val jar by tasks.getting(Jar::class) {
    from(configurations["includeInJar"].flatMap {
        when {
            it.isDirectory -> listOf(it)
            else -> listOf(zipTree(it))
        }
    })
}

tasks.withType<Javadoc> {
    enabled = true
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

apply(from = "$rootDir/gradle/publish.gradle.kts")

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") {
        isEnabled = true
    }
}