package co.atrasvida.example.ApiClient;


import co.atrasvida.avidawebapi_annotations.WebApi;
import io.reactivex.Observable;
import retrofit2.http.GET;

@WebApi(value = "ApiService", baseUrl = URL.BASE_URL)
interface NetworkApiService {

    @GET("/api/user_info/")
    Observable<BaseResponse<Object>> getUserInfo();

}