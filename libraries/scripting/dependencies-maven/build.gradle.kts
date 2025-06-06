import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
    id("jps-compatible")
}

project.updateJvmTarget("1.8")

dependencies {
    implementation(kotlinStdlib())
    api(project(":kotlin-scripting-dependencies"))

    implementation("org.apache.maven:maven-core:3.8.8")
    implementation("org.apache.maven.wagon:wagon-http:3.5.3")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.22")
    implementation("org.apache.maven.resolver:maven-resolver-transport-file:1.9.22")
    implementation("org.apache.maven.resolver:maven-resolver-transport-wagon:1.9.22")
    implementation("org.apache.maven.resolver:maven-resolver-impl:1.9.22")
    implementation(libs.apache.commons.io)

    testImplementation(projectTests(":kotlin-scripting-dependencies"))
    testImplementation(libs.junit4)
    testRuntimeOnly("org.slf4j:slf4j-nop:1.7.36")
    testImplementation(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
    testImplementation(libs.kotlinx.coroutines.core)
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xallow-kotlin-package")
}

publish()

runtimeJar()
sourcesJar()
javadocJar()
