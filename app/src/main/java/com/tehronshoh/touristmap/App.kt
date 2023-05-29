package com.tehronshoh.touristmap

import android.app.Application
import com.tehronshoh.touristmap.BuildConfig.YANDEX_MAP_KIT_API_KEY
import com.yandex.mapkit.MapKitFactory

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(YANDEX_MAP_KIT_API_KEY)
    }
}