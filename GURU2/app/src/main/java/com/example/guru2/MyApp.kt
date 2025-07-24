package com.example.guru2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "7722d093047cf8f0c349e545ce78969d")
    }
}