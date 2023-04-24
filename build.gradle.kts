plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "com.chenyilei"
version = "1.0.7"

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


sourceSets {
    main {
        java {
            // 忽略所有的 src/main/java/test目录
//            exclude 'test/**'
        }
        resources {
            // 打包忽略目录: src/main/resources/img
//            exclude 'img/**'
            // 打包忽略文件: src/main/resources/icons/scanner.vsdx
//            exclude 'icons/scanner.vsdx'
        }
    }
}
