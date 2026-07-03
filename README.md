### About Application
This is a Kotlin Multiplatform project targeting Android, iOS.
This app give opportunity to load actual news for different categories, on russian or english languages.
User can Search for news with different filters, also add news to favorites and open map with news sorted by city locations.

### Used libraries 
App based on **MVVM** architecture, using some MVI features, like: event, intent, state, for unidirectional data flow.
Application uses clean architecture pattern to build layers like: domain, data, presentation, ui.
Database **Room** act as single source of truth, **Ktor** used for api calls, **Koin** for dependency injection, **Coroutines** with **flow** for receiving data.
Map is drown using **MapLibre**, for navigation in app used *Jetpack Compose Navigation*

### Modules
* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Add API_KEY=your_api_key into local.properties file, this api_key could be taken from WorldNewsApi.com.
But be aware, it give only 50 tokens in a day.

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
