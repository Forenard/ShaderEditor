plugins {
	alias(libs.plugins.android.application)
}

android {
	namespace = "de.markusfisch.android.shadernerdeditor"

	compileSdk = 34

	defaultConfig {
		applicationId = "de.markusfisch.android.shadernerdeditor"
		minSdk = 21
		targetSdk = compileSdk

		versionCode = 87
		versionName = "2.35.0"

		vectorDrawables {
			useSupportLibrary = true
		}
	}

	signingConfigs {
		create("release") {
			keyAlias = System.getenv("ANDROID_KEY_ALIAS")
			keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
			storePassword = System.getenv("ANDROID_STORE_PASSWORD")
			storeFile = System.getenv("ANDROID_KEYFILE")?.let { file(it) }
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
		}

		release {
			isMinifyEnabled = true
			isShrinkResources = true
			signingConfig = signingConfigs["release"]
		}
	}

	buildFeatures {
		buildConfig = true
		viewBinding = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

dependencies {
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.preference)

	implementation (libs.androidx.camera.core)
	implementation(libs.androidx.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)
}
