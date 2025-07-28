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
    mavenLocal()
    mavenCentral()
}

val includeInJar: Configuration by configurations.creating
configurations["compileOnly"].extendsFrom(includeInJar)

dependencies {
    annotationProcessor("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("com.newrelic.agent.java:newrelic-api:8.9.1")
    includeInJar(project(":core"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("com.google.guava:guava:33.0.0-jre")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation(project(":core"))
    testImplementation(project(":core-test"))
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
    (options as? CoreJavadocOptions)?.addStringOption("link", "https://logging.apache.org/log4j/2.x/log4j-api/apidocs/")
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