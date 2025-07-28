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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
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

// this is done because JUL can't be cleanly reset between tests run on the same JVM.
tasks.withType<Test> {
    setForkEvery(1)
}

tasks.withType<Javadoc> {
    enabled = true
    (options as? CoreJavadocOptions)?.addStringOption("link", "https://docs.oracle.com/javase/8/docs/api/")
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
    excludeFilter.set(file("spotbugs-filter.xml"))
    reports.create("html") {
        isEnabled = true
    }
}