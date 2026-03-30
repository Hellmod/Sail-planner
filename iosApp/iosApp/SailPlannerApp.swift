import SwiftUI
import Shared
import GoogleSignIn

@main
struct SailPlannerApp: App {

    init() {
        // Initialize Koin for iOS
        KoinHelperKt.doInitKoin()
        // Configure Google Sign-In
        // GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: "YOUR_IOS_CLIENT_ID")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
