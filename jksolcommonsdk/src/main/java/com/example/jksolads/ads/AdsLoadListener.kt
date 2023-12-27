package com.example.jksolads.ads

interface AdsLoadListener {
   fun onAdsLoadFail(errorCode:Int)
   fun onAdsLoaded()
}