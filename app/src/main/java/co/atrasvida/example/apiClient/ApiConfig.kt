package co.atrasvida.example.apiClient

import co.atrasvida.avidawebapi.BuildConfig
import co.atrasvida.avidawebapi_annotations.WebApiConfig

class ApiConfig : WebApiConfig() {
    override fun getBaseUrl()= URL.BASE_URL

    override fun getBaseModel()= BaseResponse::class.java

    override fun getToken()=  null //"Bearer \$token"

    override fun getReadTimeout()=  20L

    override fun getConnectTimeout()= 20L

    override fun getWriteTimeout()= 20L

    override fun isDebugMode()= BuildConfig.DEBUG
}