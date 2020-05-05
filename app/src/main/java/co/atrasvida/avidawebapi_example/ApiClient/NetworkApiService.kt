package co.atrasvida.avidawebapi_example.ApiClient


import co.atrasvida.avidawebapi_annotations.WebApi
import io.reactivex.Observable
import retrofit2.http.*

@WebApi("ApiService", baseUrl = URL.BASE_URL)
internal interface NetworkApiService {

    @GET("/api/user_info/")
    fun getUserInfo(): Observable<BaseResponse<Any>>

    @POST("/api/ads/requests/")
    @FormUrlEncoded
    fun getAd(@Field("ads_type") adType: Int, @Field("ads_type1") adType1: Int): Observable<BaseResponse<RequestAdModel>>

    @POST("/api/ads/redis/clicks/")
    fun reportForClick(@Body reportClickRequest: ReportClickRequest): Observable<BaseResponse<Any>>

    @POST("/api/ads/redis/seens/")
    fun reportForSeens(@Body reportClickRequest: ReportClickRequest): Observable<BaseResponse<Any>>


    /** getAppKys **/
    @POST("/api/ads/app_keys/")//todo save in base64
    @FormUrlEncoded
    fun oOoo(@Field("app_key") app_key: String): Observable<BaseResponse<AppKysResponse>>


    @GET("/api/user_info/")
    fun getUserInfo2(): Observable<BaseResponse<Any>>

}