package com.example.jksolads.ads

interface AdsListener {
   fun onAdsShowFail(errorCode:Int)
   fun onAdsDismiss()
}