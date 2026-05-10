package tech.lightfeather.masarify.fcm

import kotlinx.browser.window
import org.w3c.dom.events.Event

/**
 * VAPID key from Firebase Console → Project Settings → Cloud Messaging → Web Push certificates.
 * Replace this placeholder before deploying.
 */
const val WEB_PUSH_VAPID_KEY = "REPLACE_WITH_VAPID_KEY_FROM_FIREBASE_CONSOLE"

@Suppress("UnusedParameter")
private fun jsGetDetailAsString(event: JsAny?): String? =
    js("event && event.detail ? String(event.detail) : null")

@Suppress("UnusedParameter")
private fun jsRegisterWebPush(vapidKey: String): Unit =
    js(
        """
        (function() {
            if (!('serviceWorker' in navigator)) {
                console.warn('[Masarify] Service workers not supported');
                return;
            }
            navigator.serviceWorker.register('/firebase-messaging-sw.js')
                .then(function(registration) {
                    // Forward background sync messages from SW → main thread as CustomEvents
                    navigator.serviceWorker.addEventListener('message', function(event) {
                        if (event.data && event.data.type === 'sync') {
                            window.dispatchEvent(new CustomEvent('masarify-sync'));
                        }
                    });
                    // Firebase Messaging foreground handling
                    // Requires firebase-app-compat.js + firebase-messaging-compat.js in index.html
                    if (typeof firebase === 'undefined') {
                        console.warn('[Masarify] Firebase SDK not loaded in index.html');
                        return;
                    }
                    var messaging = firebase.messaging();
                    messaging.getToken({
                        vapidKey: vapidKey,
                        serviceWorkerRegistration: registration
                    }).then(function(token) {
                        if (token) {
                            window.dispatchEvent(new CustomEvent('masarify-fcm-token', { detail: token }));
                        }
                    }).catch(function(e) {
                        console.error('[Masarify] FCM getToken error:', e);
                    });
                    messaging.onMessage(function(payload) {
                        if (payload.data && payload.data.type === 'sync') {
                            window.dispatchEvent(new CustomEvent('masarify-sync'));
                        }
                    });
                })
                .catch(function(e) {
                    console.error('[Masarify] Service worker registration failed:', e);
                });
        })()
        """,
    )

/**
 * Initializes Firebase Web Push:
 * 1. Registers [firebase-messaging-sw.js] service worker.
 * 2. Retrieves FCM token via VAPID key → calls [NotificationBridge.onNewFcmToken].
 * 3. Listens for foreground and background sync events → calls [NotificationBridge.onSyncRequested].
 *
 * Prerequisites:
 * - Add Firebase compat scripts to index.html (firebase-app-compat.js + firebase-messaging-compat.js)
 * - Replace [WEB_PUSH_VAPID_KEY] with the actual VAPID key
 * - [firebase-messaging-sw.js] must be served from the root path
 */
fun initWebPush(vapidKey: String = WEB_PUSH_VAPID_KEY) {
    window.addEventListener("masarify-sync") { _: Event ->
        NotificationBridge.onSyncRequested()
    }
    window.addEventListener("masarify-fcm-token") { event: Event ->
        val token = jsGetDetailAsString(event as JsAny)
        if (token != null) {
            NotificationBridge.onNewFcmToken(token, "web")
        }
    }
    jsRegisterWebPush(vapidKey)
}
