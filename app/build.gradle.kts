plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.furmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.furmate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.gridlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.ui.storage)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.google.firebase.auth)

    // MDC (Material Design Components)
    implementation("com.google.android.material:material:1.9.0")

    // Picasso
    implementation(libs.picasso)

    // Google identity library
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Facebook SDK
    implementation(libs.facebook.android.sdk)

}