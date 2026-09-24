import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val targetAbis = (project.findProperty("targetAbi") as String?)
    ?.split(',')
    ?.map(String::trim)
    ?.filter(String::isNotEmpty)
    ?: listOf("arm64-v8a", "armeabi-v7a", "x86_64")
val releaseKeystore = project.findProperty("aetheryKeystore") as String?

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.msnguard.vpn"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "26.3.11579264"

    defaultConfig {
        applicationId = "com.msnguard.vpn"
        minSdk = 26
        targetSdk = 36
        versionCode = 231
        versionName = "2.1.21"
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        // Fonts deflate well (~30-40%); letting AAPT compress them keeps the
        // arm64 APK under Telegram's 50 MB document cap while load cost stays
        // negligible (ResourcesCompat caches the Typeface).
        // noCompress is intentionally NOT set for ttf.
    }

    packaging {
        jniLibs {
            // REQUIRED for Tor. Not a size tweak — the feature does not work
            // without it.
            //
            // libtor.so and libobfs4proxy.so are executables that we exec() as
            // processes, because the built libtor.so exports no tor_run_main to
            // dlopen. Android 10+ forbids exec() from the app's writable home
            // directory (a W^X violation), and the only place a packaged binary
            // may be executed from is the read-only nativeLibraryDir under
            // /data/app.
            //
            // AGP 8 defaults this to false, which leaves native libs compressed
            // inside the APK and mapped straight out of it — nothing is ever
            // written to nativeLibraryDir, so there is no file to exec and Tor
            // fails with ENOENT no matter how correct the binary is.
            //
            // Side effect, in our favour: legacy packaging stores the libs
            // deflated instead of uncompressed, so the APK gets smaller even
            // with Tor added. The cost moves to installed size, since the system
            // then keeps an extracted copy alongside the APK.
            useLegacyPackaging = true
        }
    }

    if (releaseKeystore != null) {
        val envProps = Properties().apply {
            val envFile = rootProject.file("keystore.env")
            if (envFile.exists()) {
                envFile.inputStream().use { load(it) }
            }
        }
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(releaseKeystore)
                storePassword = System.getenv("AETHERY_KEYSTORE_PASSWORD")
                    ?: envProps.getProperty("storePassword")
                keyAlias = System.getenv("AETHERY_KEY_ALIAS")
                    ?: envProps.getProperty("keyAlias")
                keyPassword = System.getenv("AETHERY_KEY_PASSWORD")
                    ?: envProps.getProperty("keyPassword")
            }
        }
        buildTypes.named("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
        }
    }
}

    dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
}

