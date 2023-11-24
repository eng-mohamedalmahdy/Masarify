import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {

    let routerContext: RouterContext

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(routerContext:routerContext)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let routerContext: RouterContext

    var body: some View {
        ComposeView(routerContext:routerContext)
               .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



