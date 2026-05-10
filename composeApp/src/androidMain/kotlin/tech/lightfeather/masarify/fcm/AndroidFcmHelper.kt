package tech.lightfeather.masarify.fcm

import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import io.github.aakira.napier.Napier
import tech.lightfeather.domain.repository.FCMHelper

class AndroidFcmHelper : FCMHelper {
    @Suppress("TooGenericExceptionCaught")
    override fun getFirebaseToken(): String? =
        try {
            Tasks.await(FirebaseMessaging.getInstance().token)
        } catch (e: Exception) {
            Napier.e("Error getting Firebase token", e)
            null
        }
}
