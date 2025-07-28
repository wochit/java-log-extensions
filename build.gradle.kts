group = "com.newrelic.logging"
version = "3.0.0"

repositories {
    mavenCentral()
}

subprojects {
    tasks.withType<Test>().all {
        useJUnitPlatform()
        // flag for tests reports
        reports.junitXml.required.set(false)
    }

    tasks.withType<Javadoc>().all {
        enabled = false
    }

    tasks.withType(JavaCompile::class) {
        options.compilerArgs.add("-Xlint:unchecked")
        options.isDeprecation = true
    }
}

