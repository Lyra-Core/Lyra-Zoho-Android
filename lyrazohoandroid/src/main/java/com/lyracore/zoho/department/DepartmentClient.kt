package com.lyracore.zoho.department

import android.content.Context
import com.lyracore.zoho.core.CoreInitializer
import com.lyracore.zoho.core.models.ExceptionEvent
import com.lyracore.zoho.core.models.enums.ExceptionLocation
import com.lyracore.zoho.department.models.Department
import com.lyracore.zoho.utilities.FileUtils
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import kotlinx.serialization.json.Json

object DepartmentClient {
    internal suspend fun getAllDepartments(): List<Department> {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            val jsonString: String = FileUtils.fetchDepartmentFile(CoreInitializer.getEnvironment())
            return Json.decodeFromString<Array<Department>>(jsonString).toList()
        } catch (ex: Exception) {
            // Handle exception
            if (CoreInitializer.isZohoInitialized())
                    CoreInitializer.getExceptionHandlingCallback()
                            ?.onException(ExceptionEvent(ex, ExceptionLocation.DEPARTMENT_GET_ALL))
            return emptyList<Department>()
        }
    }

    internal suspend fun getDefaultDepartment(): Department? {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            val jsonString: String = FileUtils.fetchDepartmentFile(CoreInitializer.getEnvironment())
            val obj = Json.decodeFromString<Array<Department>>(jsonString)
            return obj.firstOrNull { o -> o.default }
        } catch (ex: Exception) {
            // Handle exception
            if (CoreInitializer.isZohoInitialized())
                    CoreInitializer.getExceptionHandlingCallback()
                            ?.onException(ExceptionEvent(ex, ExceptionLocation.DEPARTMENT_GET_DEFAULT))
            return null
        }
    }

    internal suspend fun getDepartmentByCountry(countryCode: String): Department? {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            val jsonString: String = FileUtils.fetchDepartmentFile(CoreInitializer.getEnvironment())
            val obj = Json.decodeFromString<Array<Department>>(jsonString)
            return obj.firstOrNull() { o -> o.codes.contains(countryCode) }
        } catch (ex: Exception) {
            // Handle exception
            if (CoreInitializer.isZohoInitialized())
                CoreInitializer.getExceptionHandlingCallback()
                        ?.onException(ExceptionEvent(ex, ExceptionLocation.DEPARTMENT_GET_BY_COUNTRY))
            return null
        }
    }
}
