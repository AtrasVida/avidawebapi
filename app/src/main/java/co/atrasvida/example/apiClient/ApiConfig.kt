package co.atrasvida.example.apiClient

import co.atrasvida.avidawebapi_annotations.WebApiConfig
import java.lang.reflect.Type

class ApiConfig : WebApiConfig() {
    override fun getBaseUrl(): String {
        return URL.BASE_URL
    }

    override fun getBaseModel(): Class<*> {
        return BaseResponse::class.java
    }

    override fun getToken(): String? {
        return null //"Bearer \$token"
    }

    override fun getReadTimeout(): Long {
        return 20
    }

    override fun getConnectTimeout(): Long {
        return 20
    }

    override fun getWriteTimeout(): Long {
        return 20
    }

    override fun isDebugMode(): Boolean {
        return false
    }
}