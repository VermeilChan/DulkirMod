
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
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

    compileOnly("org.spongepowered:mixin:0.8.5") {
        isTransitive = false
    }
    annotationProcessor("net.fabricmc:sponge-mixin:0.13.4+mixin.0.8.5")

    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    shadowImpl("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("dev") // TODO: machete gets confused by the `dev` prefix.
    configurations = listOf(shadowImpl)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType(Jar::class) {
    archiveBaseName.set("dulkirmod")
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        this["TweakClass"] = "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker"
        this["MixinConfigs"] = "mixins.dulkirmod.json"
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
        configurations.forEach {
            println("Config: ${it.files}")
        }
    }

    fun relocate(name: String) = relocate(name, "com.dulkirmod.deps.$name")
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }

tasks.assemble.get().dependsOn(tasks.remapJar)
