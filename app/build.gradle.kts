plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projectdexv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectdexv2"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("org.json:json:20230227")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("mysql:mysql-connector-java:5.1.49")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("org.nanohttpd:nanohttpd:2.3.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}