import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                   DeeplinkBridge.shared.handleDeepLink(url: url.absoluteString)
                }
        }
    }
}
