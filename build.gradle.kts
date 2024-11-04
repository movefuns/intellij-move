import org.jetbrains.intellij.platform.gradle.Constants.Constraints
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.util.*

val publishingToken = System.getenv("JB_PUB_TOKEN") ?: null
val publishingChannel = System.getenv("JB_PUB_CHANNEL") ?: "default"
// set by default in Github Actions
val isCI = System.getenv("CI") != null

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties for environment `$shortPlatformVersion`")

fun propOrNull(name: String): String? = extra.properties[name] as? String

fun gitCommitHash(): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-parse --short HEAD".split(" ")
//            commandLine = "git rev-parse --abbrev-ref HEAD".split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim().also {
        if (it == "HEAD")
            logger.warn("Unable to determine current branch: Project is checked out with detached head!")
    }
}

fun gitTimestamp(): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = "git show --no-patch --format=%at HEAD".split(" ")
//            commandLine = "git rev-parse --abbrev-ref HEAD".split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim().also {
        if (it == "HEAD")
            logger.warn("Unable to determine current branch: Project is checked out with detached head!")
    }
}

val psiViewerPlugin: String by project
val shortPlatformVersion = prop("shortPlatformVersion")
val useInstaller = prop("useInstaller").toBooleanStrict()
val codeVersion = "1.6.2"
var pluginVersion = "$codeVersion.$shortPlatformVersion"
if (publishingChannel != "default") {
    // timestamp of the commit with this eaps addition
    val start = 1714498465
    val commitTimestamp = gitTimestamp().toInt() - start
    pluginVersion = "$pluginVersion-$publishingChannel.$commitTimestamp"
}

val pluginGroup = "org.sui"
val pluginName = "intellij-sui-move"
val pluginJarName = "intellij-sui-move-$pluginVersion"
val javaVersion = JavaVersion.VERSION_17

val kotlinReflectVersion = "1.9.10"

group = pluginGroup
version = pluginVersion

plugins {
    id("java")
    kotlin("jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.0.1"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
    id("net.saliman.properties") version "1.5.2"
    id("org.gradle.idea")
    id("de.undercouch.download") version "5.5.0"
}

allprojects {
    apply {
        plugin("kotlin")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij.platform")
        plugin("de.undercouch.download")
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")

        implementation("io.sentry:sentry:7.2.0") {
            exclude("org.slf4j")
        }
        implementation("com.github.ajalt.clikt:clikt:3.5.2")
        testImplementation("junit:junit:4.13.2")

        intellijPlatform {
//            plugins(listOf(psiViewerPlugin))
            pycharmCommunity("2024.2.4", useInstaller = true)
//            create(prop("platformType"), prop("platformVersion"), useInstaller = useInstaller)
            testFramework(TestFrameworkType.Platform)
            pluginVerifier(Constraints.LATEST_VERSION)
            bundledPlugin("org.toml.lang")
            jetbrainsRuntime("17.0.11b1207.30")
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    sourceSets {
        main {
            java.srcDirs("src/main/gen")
        }
    }

    kotlin {
        if (file("src/$shortPlatformVersion/main/kotlin").exists()) {
            sourceSets {
                main {
                    kotlin.srcDirs("src/$shortPlatformVersion/main/kotlin")
                }
            }
        }
    }

    intellijPlatform {
        pluginConfiguration {
            version.set(pluginVersion)
            ideaVersion {
                sinceBuild.set(prop("pluginSinceBuild"))
                untilBuild.set(prop("pluginUntilBuild"))
            }
            val codeVersionForUrl = codeVersion.replace('.', '-')
            changeNotes.set(
                """
                    <body>
                        <p><a href="https://intellij-move.github.io/$codeVersionForUrl.html">
                            Changelog for the Intellij-Move $codeVersion
                            </a></p>
                    </body>
                """
            )

        }

        instrumentCode.set(false)

        publishing {
            token.set(publishingToken)
            channels.set(listOf(publishingChannel))
        }

        pluginVerification {
            ides {
                recommended()
            }
            //if("SNAPSHOT"!inshortPlatformVersion){
            //ides{
            //ide(prop("verifierIdeVersion").trim())
            //}
            //}
            failureLevel.set(
                EnumSet.complementOf(
                    EnumSet.of(
                        //these are the only issue swetolerate
                        VerifyPluginTask.FailureLevel.DEPRECATED_API_USAGES,
                        VerifyPluginTask.FailureLevel.EXPERIMENTAL_API_USAGES,
                        VerifyPluginTask.FailureLevel.SCHEDULED_FOR_REMOVAL_API_USAGES,
                    )
                )
            )
        }
    }
    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "17"
                languageVersion = "1.9"
                apiVersion = "1.9"
                freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }

        jar {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        generateLexer {
            sourceFile.set(file("src/main/grammars/MoveLexer.flex"))
            targetOutputDir.set(file("src/main/gen/org/sui/lang"))
//            purgeOldFiles.set(true)
        }
        generateParser {
            sourceFile.set(file("src/main/grammars/MoveParser.bnf"))
            targetRootOutputDir.set(file("src/main/gen"))
            // not used if purgeOldFiles set to false
            pathToParser.set("/org/sui/lang/MoveParser.java")
            pathToPsiRoot.set("/org/sui/lang/core/psi")
//            purgeOldFiles.set(true)
        }

        withType<KotlinCompile> {
            dependsOn(generateLexer, generateParser)
        }

    }

    val runIdeWithPlugins by intellijPlatformTesting.runIde.registering {
        plugins {
            plugin("com.google.ide-perf:1.3.1")
//            plugin("PsiViewer:PsiViewer 241.14494.158-EAP-SNAPSHOT")
        }
        task {
            systemProperty("org.sui.debug.enabled", true)
            systemProperty("org.sui.types.highlight.unknown.as.error", true)
//            systemProperty("org.move.external.linter.max.duration", 30)  // 30 ms
//            systemProperty("org.move.aptos.bundled.force.unsupported", true)
//            systemProperty("idea.log.debug.categories", "org.move.cli")
        }

        prepareSandboxTask {
            // dependsOn("downloadAptosBinaries")
            // copyDownloadedAptosBinaries(this)
        }
    }

    task("resolveDependencies") {
        doLast {
            rootProject.allprojects
                .map { it.configurations }
                .flatMap { it.filter { c -> c.isCanBeResolved } }
                .forEach { it.resolve() }
        }
    }

    idea {
        pathVariables(mapOf("USER_HOME" to file("/Users/yu")))
        module {
            name = "intellij-sui-move.main"
        }
    }
}

val Project.dependencyCachePath
    get(): String {
        val cachePath = file("${rootProject.projectDir}/deps")
        // If cache path doesn't exist, we need to create it manually
        // because otherwise gradle-intellij-plugin will ignore it
        if (!cachePath.exists()) {
            cachePath.mkdirs()
        }
        return cachePath.absolutePath
    }
