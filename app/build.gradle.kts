import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import org.siouan.frontendgradleplugin.infrastructure.gradle.InstallFrontendTask
import java.nio.file.Files
import kotlin.io.path.Path

group = "com.cloudogu"
version = "1.10.1"
description = "usermgt"

plugins {
    id("java")
    id("maven-publish")
    id("war")
    id("org.siouan.frontend-jdk11")
    kotlin("jvm") version "1.7.20"  // for gradle file integration
}

buildscript {
    extra.apply {
        set("powermockVersion", "1.5.3")
        set("slf4jVersion", "1.7.36")
        set("resteasyVersion", "3.15.6.Final")
        set("guiceVersion", "3.0")
        set("shiroVersion", "1.12.0")
        set("legmanVersion", "1.6.2")
        set("jacocoVersion", "0.8.10")
        set("jacksonVersion", "1.9.13")
        set("endorsedPath", "${projectDir}/endorsed")
        set("frontendPath", "src/main/ui")
    }
}

repositories {
    maven {
        url = uri("https://packages.scm-manager.org/repository/public")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("http://packages.scm-manager.org/nexus/content/groups/public")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation("javax.mail:mail:1.5.0-b01")
    implementation("com.google.inject:guice:${project.extra.get("guiceVersion")}")
    implementation("com.google.inject.extensions:guice-servlet:${project.extra.get("guiceVersion")}")
    implementation("com.google.inject.extensions:guice-multibindings:${project.extra.get("guiceVersion")}")
    implementation("org.apache.shiro:shiro-core:${project.extra.get("shiroVersion")}")
    implementation("org.apache.shiro:shiro-web:${project.extra.get("shiroVersion")}")
    implementation("org.apache.shiro:shiro-guice:${project.extra.get("shiroVersion")}")
    implementation("org.apache.shiro:shiro-cas:${project.extra.get("shiroVersion")}") // TODO replace with pack4j
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.opensaml:opensaml:1.1")
    implementation("org.apache.santuario:xmlsec:2.3.3")
    implementation("org.jboss.resteasy:resteasy-servlet-initializer:${project.extra.get("resteasyVersion")}")
    implementation("org.jboss.resteasy:resteasy-guice:${project.extra.get("resteasyVersion")}")
    implementation("org.jboss.resteasy:resteasy-jackson-provider:${project.extra.get("resteasyVersion")}")
    constraints {
        implementation("org.codehaus.jackson:jackson-core-asl:${project.extra.get("jacksonVersion")}") {
            because("wrong dependency is loaded for resteasy-jackson-provider / cannot be resolved")
        }

        implementation("org.codehaus.jackson:jackson-mapper-asl:${project.extra.get("jacksonVersion")}") {
            because("wrong dependency is loaded for resteasy-jackson-provider / cannot be resolved")
        }

        implementation("org.codehaus.jackson:jackson-jaxrs:${project.extra.get("jacksonVersion")}") {
            because("wrong dependency is loaded for resteasy-jackson-provider / cannot be resolved")
        }

        implementation("org.codehaus.jackson:jackson-xc:${project.extra.get("jacksonVersion")}") {
            because("wrong dependency is loaded for resteasy-jackson-provider / cannot be resolved")
        }
    }
    implementation("org.jboss.resteasy:resteasy-multipart-provider:${project.extra.get("resteasyVersion")}")
    implementation("com.unboundid:unboundid-ldapsdk:4.0.14")
    implementation("com.github.legman:core:${project.extra.get("legmanVersion")}")
    implementation("com.github.legman.support:guice:${project.extra.get("legmanVersion")}")
    implementation("com.github.legman.support:shiro:${project.extra.get("legmanVersion")}")
    implementation("org.hibernate.validator:hibernate-validator:6.2.5.Final")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("joda-time:joda-time:2.12.5")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:jcl-over-slf4j:1.7.36")
    implementation("org.slf4j:log4j-over-slf4j:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("org.kohsuke.metainf-services:metainf-services:1.9")
    implementation("com.opencsv:opencsv:5.8")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("com.github.sdorra:ldap-unit:1.0.0")
    testImplementation("com.github.sdorra:shiro-unit:1.0.2")
    testImplementation("org.glassfish:javax.el:3.0.0")
    testImplementation("uk.org.lidalia:slf4j-test:1.2.0")
    providedCompile("javax.servlet:javax.servlet-api:3.0.1")
    providedCompile("commons-logging:commons-logging:1.1.1")
    providedCompile("javax.el:javax.el-api:3.0.0")
}

// TODO modify publishing later (talk with team if universe triology deployment is still relevant)
//publishing {
//    publications {
//        create<MavenPublication>("usermgt") {
//            from(components["java"])
//            pom {
//                developers {
//                    developer {
//                        id.set("sdorra")
//                        name.set("Sebastian Sdorra")
//                        email.set("sebastian.sdorra@triology.de")
//                        timezone.set("Europe/Berlin")
//                    }
//                }
//                scm {
//                    connection.set("scm:git:https://universe.triology.de/scm/git/SCM-Manager/universeadm")
//                    developerConnection.set("scm:git:https://universe.triology.de/scm/git/SCM-Manager/universeadm")
//                    url.set("https://universe.triology.de/scm/git/SCM-Manager/universeadm")
//                    tag.set("HEAD")
//                }
//            }
//        }
//    }
//}

java {
    sourceCompatibility = JavaVersion.VERSION_11 // todo update to java 17 once migration to gradle done
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.compileJava {
    // replaces animal sniffer plugin
    options.compilerArgs.addAll(mutableListOf("--release",  JavaVersion.VERSION_11.toString()));
}


tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Javadoc::class) {
    options.encoding = "UTF-8"
}

tasks.war {
    webAppDirectory.set(file("src/main/ui/dist"))
    webInf { from("src/main/webapp/WEB-INF") }
    metaInf { from("src/main/webapp/META-INF") }
}

tasks.named<Test>("test") {
    useJUnit()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}

frontend {
    packageJsonDirectory.set(project.extra.get("frontendPath")?.let { file(it) })
    nodeVersion.set("18.7.0")
    assembleScript.set("run build")
    cleanScript.set("run clean")
    checkScript.set("run lint")
    verboseModeEnabled.set(true)
}

tasks.named<InstallFrontendTask>("installFrontend") {
    val frontendDir = project.extra.get("frontendPath")
    val lockFilePath = "$frontendDir/yarn.lock"
    val metadataFileNames = mutableSetOf("$frontendDir/package.json")
    if (Files.exists(Path(lockFilePath))) {
        metadataFileNames.add(lockFilePath)
    }
    outputs.file(lockFilePath).withPropertyName("lockFile")
    inputs.files(metadataFileNames).withPropertyName("metadataFiles")
    outputs.dir("$frontendDir/node_modules").withPropertyName("nodeModulesDirectory")
}