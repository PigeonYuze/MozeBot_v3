plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"

    id("me.him188.kotlin-jvm-blocking-bridge") version "2.1.0-170.1"
}

group = "com.pigeonyuze.moze-bot"
version = "3.0.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}


mirai {
    jvmTarget = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.jetbrains.kotlinx:atomicfu:0.20.2")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.7.10")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-text
    implementation("org.apache.commons:commons-text:1.10.0")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}
