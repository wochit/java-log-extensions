apply(plugin = "maven-publish")
apply(plugin = "signing")

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set(project.name)
                description.set("Decorate logs with trace data for New Relic Logging.")
                url.set("https://github.com/newrelic/java-log-extensions")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("newrelic")
                        name.set("New Relic")
                        email.set("opensource@newrelic.com")
                    }
                }
                scm {
                    url.set("git@github.com:newrelic/java-log-extensions.git")
                    connection.set("scm:git:git@github.com:newrelic/java-log-extensions.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://nexus.wochit.com/repository/releases/")
            val snapshotsRepoUrl = uri("https://nexus.wochit.com/repository/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            configure<SigningExtension> {
                val signingKeyId: String? by project
                val signingKey: String? by project
                val signingPassword: String? by project
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                setRequired({ gradle.taskGraph.hasTask("uploadArchives") })
                sign(publications["mavenJava"])
            }

            credentials {
                username = "ronen"
                password = "zt4zuvYjecmqXa9z4WET"
            }
        }
    }
}

// This makes it difficult to use modern Java and produce usable output.
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
