plugins {
    java
    application
}

group = "com.newrelic.logging"
version = "3.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":log4j2"))

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.lmax:disruptor:3.4.4")
    implementation("com.newrelic.agent.java:newrelic-api:8.9.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("PerformanceMain")
    applicationDefaultJvmArgs += listOf(
            "-javaagent:${rootProject.projectDir}/lib/newrelic.jar",
            "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
    )
}

tasks.register<JavaExec>("executeNoAgent") {
    mainClass.set("PerformanceMain")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "No_Agent")
    jvmArgs("-Xmx1024m")
}

tasks.register<JavaExec>("executeWithAgent") {
    mainClass.set("PerformanceMain")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "With_Agent")
    jvmArgs("-Xmx1024m", "-javaagent:${rootProject.projectDir}/lib/newrelic.jar")
}

tasks.register<JavaExec>("executeNoAgentAsync") {
    mainClass.set("PerformanceMain")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "No_Agent_Async")
    jvmArgs("-Xmx1024m")
    systemProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")
}

tasks.register<JavaExec>("executeWithAgentAsync") {
    mainClass.set("PerformanceMain")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "With_Agent_Async")
    jvmArgs("-Xmx1024m", "-javaagent:${rootProject.projectDir}/lib/newrelic.jar")
    systemProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")
}

tasks.register("runPerformanceTests") {
    dependsOn("jar")
    dependsOn("executeNoAgent")
    dependsOn("executeWithAgent")
    dependsOn("executeNoAgentAsync")
    dependsOn("executeWithAgentAsync")
}
