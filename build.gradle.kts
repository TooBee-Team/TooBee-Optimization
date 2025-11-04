plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    mods {
        register("optimization") {
            sourceSet("main")
        }
    }
}

repositories {
    maven("https://api.modrinth.com/maven")
    maven("https://maven.bawnorton.com/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    //mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("maven.modrinth:lithium:mc${project.property("lithium_version")}-fabric")

    annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${project.property("mixin_squared_version")}")
        ?.let { implementation(it) }?.let { include(it) }
    //modImplementation(fabricApi.module("fabric-lifecycle-events-v1", project.property("fabric_version") as String))
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    val map = mapOf(
        "id" to project.property("mod_id"),
        "version" to project.version,
        "minecraft_version" to project.property("minecraft_version"),
        "loader_version" to project.property("loader_version"),
    )
    filesMatching("fabric.mod.json") {
        expand(map)
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
