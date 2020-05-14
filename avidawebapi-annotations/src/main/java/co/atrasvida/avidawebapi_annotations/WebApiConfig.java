package co.atrasvida.avidawebapi_annotations;

import java.lang.reflect.Type;

abstract public class WebApiConfig {

    public abstract String getBaseUrl();

    public abstract Type  getBaseModel();

    public abstract String getToken();

    public abstract long getReadTimeout();

    public abstract long getConnectTimeout();

    public abstract long getWriteTimeout();

    public abstract Boolean isDebugMode();


}
