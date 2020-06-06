package co.atrasvida.example.apiClient

import co.atrasvida.avidawebapi_annotations.WebApi
import io.reactivex.Observable
import retrofit2.http.GET

@WebApi(value = "ApiService", config = ApiConfig::class)
internal interface NetworkApiService {

    @GET("/car/brands/")
    fun getCarBrands(): Observable<BaseResponse<CarBrandsModel>>

}