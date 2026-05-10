importScripts('https://www.gstatic.com/firebasejs/10.14.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.14.1/firebase-messaging-compat.js');

firebase.initializeApp({
    apiKey: "AIzaSyBvQw26WcbM0mm-c5EQ7-TiW77ZoM24iAg",
    authDomain: "masarify.firebaseapp.com",
    projectId: "masarify",
    storageBucket: "masarify.firebasestorage.app",
    messagingSenderId: "937375111830",
    appId: "1:937375111830:web:42e4fa31ca8ac80bf900b7"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    if (payload.data && payload.data.type === 'sync') {
        // Notify all active clients to trigger sync
        self.clients.matchAll({ includeUncontrolled: true, type: 'window' }).then(function(clients) {
            clients.forEach(function(client) {
                client.postMessage({ type: 'sync' });
            });
        });
    } else {
        const title = payload.notification && payload.notification.title ? payload.notification.title : 'Masarify';
        const body = payload.notification && payload.notification.body ? payload.notification.body : '';
        self.registration.showNotification(title, { body: body });
    }
});
