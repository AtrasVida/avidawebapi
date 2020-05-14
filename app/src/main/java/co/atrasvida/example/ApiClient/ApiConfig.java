package co.atrasvida.example.ApiClient;

import java.lang.reflect.Type;

import co.atrasvida.avidawebapi_annotations.WebApiConfig;

public class ApiConfig extends WebApiConfig {

    @Override
    public String getBaseUrl() {
        return URL.BASE_URL;
    }

    @Override
    public Type getBaseModel() {
        return BaseResponse.class;
    }

    @Override
    public String getToken() {
        return "Bearer $token";
    }

    @Override
    public long getReadTimeout() {
        return 20;
    }

    @Override
    public long getConnectTimeout() {
        return 20;
    }

    @Override
    public long getWriteTimeout() {
        return 20;
    }

    @Override
    public Boolean isDebugMode() {
        return false;
    }


}
