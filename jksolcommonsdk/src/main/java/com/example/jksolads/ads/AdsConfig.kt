package com.example.jksolads.ads

import android.app.Activity
import android.app.Application
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class AdsConfig(var context: Application, var adsProvider: AdsProvider) {

    private var currentActivity: Activity? = null
    var isShowAppOpenAds: Boolean = false
    private var isShowSplashAppOpenAds: Boolean = false
    var mActivity: Array<out Class<*>>? = null
    var resumeAdId: String? = null
    private var splashAppOpenAdId: String? = null

    fun initializeOneSignal(oneSignalId: String){
        OneSignal.initWithContext(context, oneSignalId)
    }

    fun initializeAppMetrica(metricaId: String) {
        val config = YandexMetricaConfig.newConfigBuilder(metricaId).build()
        YandexMetrica.activate(context, config)
        YandexMetrica.enableActivityAutoTracking(context)
    }

    fun removeActivityEnableShowResumeAd(vararg activity: Class<*>) {
        mActivity = activity
    }

}