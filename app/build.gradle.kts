plugins {
    id("com.android.application")
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.example.petcareapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.petcareapp"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

// --- Estratégia global de resolução de dependências ---
configurations.all {
    // 1) Exclui módulos leves ou conflitantes
    exclude(group = "com.google.protobuf", module = "protobuf-lite")
    // (opcional) exclude(group = "com.google.protobuf", module = "protobuf-java")

    resolutionStrategy {
        // 2) Força as versões corretas
        force("com.google.protobuf:protobuf-java:3.25.1")
        force("com.google.protobuf:protobuf-javalite:3.25.1")

        // 3) Substitui qualquer outra versão transitiva
        dependencySubstitution {
            substitute (module("com.google.protobuf:protobuf-java"))
                .using( module("com.google.protobuf:protobuf-java:3.25.1"))
            substitute(module("com.google.protobuf:protobuf-javalite"))
                .using (module("com.google.protobuf:protobuf-javalite:3.25.1"))
        }
    }
}

dependencies {
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.10.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.navigation:navigation-fragment:2.8.9")
    implementation("androidx.navigation:navigation-ui:2.8.9")
    implementation("androidx.webkit:webkit:1.10.0")
    implementation("androidx.fragment:fragment:1.6.1")
    implementation("androidx.core:core:1.13.0")

    // Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Protobuf Lite (Android-friendly)
    implementation("com.google.protobuf:protobuf-javalite:3.25.1")

    // use a versão 5.1.49 do Connector/J para não quebrar no Android
    // Conector MySQL (excluindo Protobuf conflituoso)
    implementation("mysql:mysql-connector-java:5.1.49") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }


    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
