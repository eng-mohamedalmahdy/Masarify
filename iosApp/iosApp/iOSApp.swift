import SwiftUI
import shared

class DefaultRouterHolder : ObservableObject {
  let defaultRouterContext: RouterContext = DefaultRouterContextKt.defaultRouterContext()

  deinit {
    // Destroy the root component before it is deallocated
    defaultRouterContext.destroy()
  }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    let holder: DefaultRouterHolder = DefaultRouterHolder()
}

@main
struct iOSApp: App {

  @UIApplicationDelegateAdaptor var delegate: AppDelegate
  @Environment(\.scenePhase)  var scenePhase: ScenePhase
  var defaultRouterContext: RouterContext { delegate.holder.defaultRouterContext }
   init() {
       HelperKt.doInitKoin()
   }
	var body: some Scene {
		WindowGroup {
			ContentView(routerContext: defaultRouterContext)
		}.onChange(of: scenePhase) { newPhase in
                 switch newPhase {
                 case .background: defaultRouterContext.stop()
                 case .inactive: defaultRouterContext.pause()
                 case .active: defaultRouterContext.resume()
                 @unknown default: break
                 }
             }
	}
}

