group = "com.newrelic.logging"
version = "2.3.5"

repositories {
    mavenCentral()
}

subprojects {
    tasks.withType<Test>().all {
        useJUnitPlatform()
        // flag for tests reports
        reports.junitXml.isEnabled = false
    }

    tasks.withType<Javadoc>().all {
        enabled = false
    }

    tasks.withType(JavaCompile::class) {
        options.compilerArgs.add("-Xlint:unchecked")
        options.isDeprecation = true
    }

}

