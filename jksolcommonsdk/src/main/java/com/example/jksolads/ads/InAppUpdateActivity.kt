package com.example.jksolads.ads

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.text.DecimalFormat
import kotlin.math.ceil


open class InAppUpdateActivity : AppCompatActivity() {

    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    var newVersion = 0
    var appUpdateType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppVersion()
    }

    fun updateApp(appVersion: Int) {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo: AppUpdateInfo ->
            appUpdateType =
                if (appVersion.equals(newVersion - 1)) AppUpdateType.FLEXIBLE
                else if (appVersion <= (newVersion - 3))
                    AppUpdateType.IMMEDIATE
                else AppUpdateType.FLEXIBLE

            val isUpdateTypeAllowed =
                if (appVersion
                        .equals(newVersion - 1)
                ) appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                else if (appVersion <= (newVersion - 3))
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) else false

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && isUpdateTypeAllowed
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,  // an activity result launcher registered via registerForActivityResult
                    activityResultLauncher as ActivityResultLauncher<IntentSenderRequest>,  // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(appUpdateType).build()
                )
            }
        })
    }

    private fun getAppVersion() {

//        if (!FirebaseApp.getApps(this).isEmpty()) {
        // Not in crash process.  Do your Remote Config init here.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(7)
            .build()
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)

        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                            boolean updated = task.getResult();
                    newVersion = mFirebaseRemoteConfig!!.getString("appVersion").toInt()

                    Log.e("apiNAme", java.lang.String.valueOf(newVersion))
                } else {
                }
            }
    }

    var activityResultLauncher: ActivityResultLauncher<*> =
        registerForActivityResult<IntentSenderRequest, ActivityResult>(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result!!.resultCode != Activity.RESULT_OK && appUpdateType == AppUpdateType.IMMEDIATE) {
                finish()
                finishAffinity()
            }else if (result.resultCode == Activity.RESULT_OK){
               val intent = intent
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                finishAffinity()
                startActivity(intent)
            }
        }
}