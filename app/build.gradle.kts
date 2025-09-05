plugins {
	id("com.android.application") version "8.5.2"
	id("org.jetbrains.kotlin.android") version "1.9.24"
	id("com.google.devtools.ksp") version "1.9.24-1.0.20"
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
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
}

ksp {
	arg("room.schemaLocation", "$projectDir/schemas")
	arg("room.incremental", "true")
	arg("room.generateKotlin", "true")
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

	// Navigation (Fragments)
	implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
	implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

	// Jetpack Compose BOM
	implementation(platform("androidx.compose:compose-bom:2024.10.01"))
	androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))

	// Jetpack Compose core
	implementation("androidx.activity:activity-compose:1.9.2")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
	implementation("androidx.navigation:navigation-compose:2.8.2")
	implementation("androidx.compose.material:material-icons-extended")

	// Room
	implementation("androidx.room:room-runtime:2.6.1")
	implementation("androidx.room:room-ktx:2.6.1")
	ksp("androidx.room:room-compiler:2.6.1")
	// Optional: Paging support
	// implementation("androidx.room:room-paging:2.6.1")

	// Compose tooling & tests
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")

	// Testing
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

