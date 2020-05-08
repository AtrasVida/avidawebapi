package co.atrasvida.example.ApiClient


import co.atrasvida.avidawebapi_annotations.WebApi
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


    @GET("/car/company/")
    fun companyLit(): Observable<BaseResponse<CompanyModel>>

    @POST("/car/calculate/")
    fun calculate(@Body calculateRequest: CalculateRequest): Observable<BaseResponse<CalculateResponce>>

    @POST("/car/body/")
    fun calculate(@Body calculateRequest: CalculateBodyRequest): Observable<BaseResponse<CalculateResponce>>

    @GET("/car/brands/")
    fun getCarBrands(): Observable<BaseResponse<CarBrandsModel>>

    @GET("/car/brands/{carId}/models/")
    fun getBrandModels(@Path("carId") carId: Int): Observable<BaseResponse<BrandModelsModel>>

    @GET("/car/discount/year/")
    fun getDiscountYear(): Observable<BaseResponse<DiscountModel>>

    @GET("/car/discount/driver/")
    fun getDiscountDriver(): Observable<BaseResponse<DiscountModel>>

    @GET("/car/discount/body/")
    fun getDiscountBody(): Observable<BaseResponse<DiscountModel>>

    @GET("/car/damage/financial/")
    fun getDamageFinancial(): Observable<BaseResponse<DiscountModel>>

    @GET("/car/damage/life/")
    fun getDamageLife(): Observable<BaseResponse<DiscountModel>>

    @GET("/car/damage/driver/")
    fun getDamageDriver(): Observable<BaseResponse<DiscountModel>>

    @POST("/api/token/")
    fun getToken(@Body loginRequest: LoginRequest): Observable<BaseResponse<Any>>

    @POST("/api/verification_code/")
    fun sendVerificationCodeRequest(@Body verificationCodeRequest: VerificationCodeRequest): Observable<BaseResponse<Any>>

    @POST("/contract/")
    fun contract(@Body contractModel: ContractModel): Observable<BaseResponse<ContractResponceModel>>

    @POST("/contract/")
    fun contract(@Body contractModel: ContractCarBodyModel): Observable<BaseResponse<ContractResponceModel>>

    @POST("/contract/")
    fun contract(@Body finalContractModel: FinalContractModel): Observable<BaseResponse<Any>>

    @GET("/contract/")
    fun getContractList(): Observable<BaseResponse<UserContractsModel>>

    @GET("/contract/{orderId}/")
    fun getContract(@Path("orderId") orderId: Int): Observable<BaseResponse<OrderDetailModel>>

    @POST("/contract/")
    @Multipart
    fun sentImage(
        @Part("contract_status") contract_status: RequestBody,
        @Part("contract_step") contract_step: Int,
        @Part("contract_id") contract_id: Int,
        @Part("image_type") image_type: Int,
        @Part image: MultipartBody.Part

    ): Observable<BaseResponse<UploadImageResponce>>

}