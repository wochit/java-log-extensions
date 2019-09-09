plugins {
    java
    application
}

group = "com.newrelic.logging"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":log4j2"))

    implementation("org.apache.logging.log4j:log4j-api:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
    implementation("com.lmax:disruptor:3.4.2")
    implementation("com.newrelic.agent.java:newrelic-api:5.6.0")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "PerformanceMain"
    applicationDefaultJvmArgs += listOf(
            "-javaagent:${rootProject.projectDir}/lib/newrelic.jar",
            "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector",
            "-Dlog4j2.messageFactory=com.newrelic.log4j2.plugin.NewRelicMessageFactory"
    )
}

task("executeNoAgent", JavaExec::class) {
    main = "PerformanceMain"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "No_Agent")
    jvmArgs("-Xmx1024m")
    systemProperty("log4j2.messageFactory", "com.newrelic.log4j2.plugin.NewRelicMessageFactory")
}

task("executeWithAgent", JavaExec::class) {
    main = "PerformanceMain"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "With_Agent")
    jvmArgs("-Xmx1024m", "-javaagent:${rootProject.projectDir}/lib/newrelic.jar")
    systemProperty("log4j2.messageFactory", "com.newrelic.log4j2.plugin.NewRelicMessageFactory")
}

task("executeNoAgentAsync", JavaExec::class) {
    main = "PerformanceMain"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "No_Agent_Async")
    jvmArgs("-Xmx1024m")
    systemProperty("log4j2.messageFactory", "com.newrelic.log4j2.plugin.NewRelicMessageFactory")
    systemProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")
}

task("executeWithAgentAsync", JavaExec::class) {
    main = "PerformanceMain"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("INFO", "With_Agent_Async")
    jvmArgs("-Xmx1024m", "-javaagent:${rootProject.projectDir}/lib/newrelic.jar")
    systemProperty("log4j2.messageFactory", "com.newrelic.log4j2.plugin.NewRelicMessageFactory")
    systemProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")
}

task("runPerformanceTests") {
    dependsOn("jar")
    dependsOn("executeNoAgent")
    dependsOn("executeWithAgent")
    dependsOn("executeNoAgentAsync")
    dependsOn("executeWithAgentAsync")
}
