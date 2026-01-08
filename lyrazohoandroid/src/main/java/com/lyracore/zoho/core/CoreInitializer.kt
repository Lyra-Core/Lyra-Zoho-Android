package com.lyracore.zoho.core

import android.app.Application
import com.lyracore.zoho.core.interfaces.ExceptionHandlingCallback
import com.lyracore.zoho.core.models.ErrorEvent
import com.lyracore.zoho.core.models.ExceptionEvent
import com.lyracore.zoho.core.models.ZohoConfig
import com.lyracore.zoho.core.models.enums.Environment
import com.lyracore.zoho.core.models.enums.ErrorLocation
import com.lyracore.zoho.core.models.enums.ExceptionLocation
import com.lyracore.zoho.utilities.FileUtils
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ

object CoreInitializer {
    @Volatile private var zohoInitialized = false
    private var zohoAppKey: String? = null
    private var zohoAccessKey: String? = null
    private lateinit var exceptionHandlingCallback: ExceptionHandlingCallback

    private var environment: Environment = Environment.PRODUCTION

    /** Initialize Zoho-specific keys. This is called by ChatClient after successful Zoho init. */
    internal fun initializeZoho(application: Application, zohoConfig: ZohoConfig) {
        try {
            val initConfig = InitConfig()
            this.exceptionHandlingCallback = zohoConfig.exceptionHandlingCallback
            ZohoSalesIQ.init(
                    application,
                    zohoConfig.appKey,
                    zohoConfig.accessKey,
                    initConfig,
                    object : InitListener {
                        override fun onInitSuccess() {
                            ZohoSalesIQ.Chat.showOperatorImageInLauncher(false)
                            // Mark core as initialized with the keys
                            synchronized(this) {
                                if (!this@CoreInitializer.zohoInitialized) {
                                    this@CoreInitializer.zohoAppKey = zohoConfig.appKey
                                    this@CoreInitializer.zohoAccessKey = zohoConfig.accessKey
                                    this@CoreInitializer.zohoInitialized = true
                                }
                            }
                        }

                        override fun onInitError(errorCode: Int, errorMessage: String) {
                            // Handle initialization error
                            if (this@CoreInitializer::exceptionHandlingCallback.isInitialized)
                                this@CoreInitializer.exceptionHandlingCallback.onError(
                                        ErrorEvent(
                                                errorCode,
                                                errorMessage,
                                                ErrorLocation.CORE_INITIALIZE
                                        )
                                )
                        }
                    }
            )
        } catch (ex: Exception) {
            // Handle exception
            if (this::exceptionHandlingCallback.isInitialized)
                this.exceptionHandlingCallback.onException(
                    ExceptionEvent(ex, ExceptionLocation.CORE_INITIALIZE)
                )
        }
    }

    internal fun setEnvironment(environment: Environment) {
        try {
            if (!this.isZohoInitialized()) throw Exception("Zoho not initialized")

            this.environment = environment
            FileUtils.clearCache()
        } catch (ex: Exception) {
            // Handle exception
            if (this::exceptionHandlingCallback.isInitialized)
                getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CORE_SET_ENVIRONMENT))
        }
    }

    internal fun isZohoInitialized(): Boolean = zohoInitialized
    internal fun getZohoAppKey(): String? = zohoAppKey
    internal fun getZohoAccessKey(): String? = zohoAccessKey
    internal fun getExceptionHandlingCallback(): ExceptionHandlingCallback? {
        if (this::exceptionHandlingCallback.isInitialized)
            return exceptionHandlingCallback

        return null;
    }
    internal fun getEnvironment(): Environment = environment

    /**
     * Reset the CoreInitializer state. This method is intended for testing purposes only.
     * @VisibleForTesting
     */
    internal fun reset() {
        synchronized(this) {
            zohoInitialized = false
            zohoAppKey = null
            zohoAccessKey = null
        }
    }
}
