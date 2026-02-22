package com.personalfinance.sdk.bridge

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.personalfinance.sdk.receiver.FinanceSdkDependencies

class FinanceSdkPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        val deps = reactContext.applicationContext as FinanceSdkDependenciesProvider
        return listOf(
            FinanceSdkModule(
                reactContext = reactContext,
                repository = deps.repository,
                syncStatusStore = deps.syncStatusStore,
            ),
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> = emptyList()
}

interface FinanceSdkDependenciesProvider : FinanceSdkDependencies {
    val syncStatusStore: com.personalfinance.sdk.sync.SyncStatusStore
}
