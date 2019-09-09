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
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.9")
    implementation("com.lmax:disruptor:3.4.2")
    implementation("com.newrelic.agent.java:newrelic-api:5.6.0")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "com.newrelic.testapps.log4j2.Main"
    applicationDefaultJvmArgs += listOf(
            "-javaagent:${rootProject.projectDir}/lib/newrelic.jar",
            "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector",
            "-Dlog4j2.messageFactory=com.newrelic.logging.log4j2.NewRelicMessageFactory"
    )
}
