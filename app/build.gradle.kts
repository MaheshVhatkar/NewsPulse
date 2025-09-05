plugins {
	id("com.android.application") version "8.5.2"
	id("org.jetbrains.kotlin.android") version "1.9.24"
}

android {
	namespace = "com.example.smartexpensetracker"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.smartexpensetracker"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}

	buildFeatures {
		viewBinding = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")

	// AndroidX Core & AppCompat
	implementation("androidx.core:core-ktx:1.13.1")
	implementation("androidx.appcompat:appcompat:1.7.0")
	implementation("com.google.android.material:material:1.12.0")
	implementation("androidx.constraintlayout:constraintlayout:2.2.0")

	// Lifecycle & ViewModel
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

	// Navigation
	implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
	implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

	// Testing
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

