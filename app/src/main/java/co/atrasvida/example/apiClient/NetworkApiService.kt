package co.atrasvida.example.apiClient

import co.atrasvida.avidawebapi_annotations.CACHING
import co.atrasvida.avidawebapi_annotations.CachSetting
import co.atrasvida.avidawebapi_annotations.WebApi
import io.reactivex.Observable
import retrofit2.http.GET

@WebApi(value = "ApiService", config = ApiConfig::class)
internal interface NetworkApiService {

    @CACHING(CachSetting.first_cash_and_if_updated)
    @GET("/car/brands/")
    fun getCarBrands(): Observable<BaseResponse<CarBrandsModel>>

    @GET("/car/brands/")
    fun getCarBrands2(): Observable<BaseResponse<CarBrandsModel>>

}