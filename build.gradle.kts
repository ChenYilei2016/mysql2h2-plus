import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "com.chenyilei"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/nexus/content/repositories/central/")
//    mavenCentral()
}

dependencies {
//    implementation(files("lib/druid-parser-1.1.23.jar"))

//    implementation("com.alibaba:druid:1.2.12")
    implementation("com.alibaba:druid:1.1.23")
    implementation("junit:junit:4.13.2")
}

//dependencies {
//    implementation files('lib/druid-parser-1.1.23.jar')
//    testCompile group: 'junit', name: 'junit', version: '4.12'
//}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))

    updateSinceUntilBuild.set(false)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
