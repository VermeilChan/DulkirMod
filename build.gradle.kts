import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.gradleup.shadow") version "9.0.0-beta7"
    kotlin("jvm") version "2.1.20-Beta2"
}

group = "com.example.archloomtemplate"
version = "1.2.9"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            arg("--mixin", "mixins.dulkirmod.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.dulkirmod.json")
    }
    mixin {
        defaultRefmapName.set("mixins.dulkirmod.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(layout.buildDirectory.dir("classes/java/main").get().asFile)
}

val packageLib by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.essential.gg/repository/maven-public/")
    maven("https://repo.polyfrost.cc/releases")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    compileOnly("org.spongepowered:mixin:0.8.7") {
        isTransitive = false
    }
    annotationProcessor("net.fabricmc:sponge-mixin:0.15.5+mixin.0.8.7")

    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    shadowImpl("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    doLast {
        configurations.get().forEach {
            println("Config: ${it.incoming.files}")
        }
    }

    fun relocate(name: String) = relocate(name, "com.dulkirmod.deps.$name")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        optIn.add("kotlin.RequiresOptIn")
    }
}

tasks.withType<Jar> {
    archiveBaseName.set("dulkirmod")
    manifest.attributes.apply {
        put("FMLCorePluginContainsFMLMod", "true")
        put("ForceLoadAsMod", "true")
        put("TweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
        put("MixinConfigs", "mixins.dulkirmod.json")
    }
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("all")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.shadowJar {
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.get().forEach {
            println("Config: ${it.incoming.files}")
        }
    }

    fun relocate(name: String) = relocate(name, "com.dulkirmod.deps.$name")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.assemble.get().dependsOn(tasks.remapJar)
