# easy-retrofit2
a simple library for using retrofit2 simpler 3

# easy retrofit2   [![](https://jitpack.io/v/AtrasVida/avidawebapi.svg)](https://jitpack.io/#AtrasVida/avidawebapi)



## Installation
Use the JitPack package repository.

Add `jitpack.io` repository to your root `build.gradle` file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Next add library to your project `build.gradle` file:
**Gradle:**
```groovy
   
    // easy-retrofit
    implementation 'com.github.AtrasVida:avidawebapi:{last_release_version}'



    // Retrofit
    implementation "com.squareup.retrofit2:retrofit:2.5.0"
    implementation "com.squareup.retrofit2:adapter-rxjava2:2.4.0"
    implementation "com.squareup.retrofit2:converter-moshi:2.4.0"
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.11.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.5.0'

    // Rx
    implementation "io.reactivex.rxjava2:rxjava:2.2.2"
    implementation "io.reactivex.rxjava2:rxandroid:2.0.2"
    implementation 'com.jakewharton.rxrelay2:rxrelay:2.1.0'
    implementation 'io.reactivex:rxkotlin:0.21.0'

    // room
    implementation 'androidx.room:room-runtime:2.2.3'
    kapt 'androidx.room:room-compiler:2.2.3'

```


## Usage


then create ApiConfig.kt :
```kotlin

import co.atrasvida.avidawebapi_annotations.WebApiConfig

class ApiConfig : WebApiConfig() {
    override fun getBaseUrl()= URL.BASE_URL

    override fun getBaseModel()= BaseResponse::class.java // can be String or Object

    override fun getToken()=  null //"Bearer \$token"

    override fun getReadTimeout()=  20L

    override fun getConnectTimeout()= 20L

    override fun getWriteTimeout()= 20L

    override fun isDebugMode()= BuildConfig.DEBUG
}
```

BaseResponse.class :
```kotlin

public class BaseResponse<T> {

    @SerializedName("IsSuccess")
    boolean isSuccess = false;

    @SerializedName("Item")
    T item = null;

    @SerializedName("ListItems")
    List<T> listItems = null;

    @SerializedName("ErrorCode")
    int errorCode = 0;

    @SerializedName("Message")
    String message = null;
    
}

```

now create your retrofit Api interface :
```kotlin

import co.atrasvida.avidawebapi_annotations.WebApi
import io.reactivex.Observable
import retrofit2.http.GET

@WebApi(value = "ApiService", config = ApiConfig::class)
internal interface NetworkApiService {

    @GET("/car/brands/")
    fun getCarBrands(): Observable<BaseResponse<CarBrandsModel>>

}
```

now you can use every where :
```kotlin
ApiService().getCarBrands() {
    print(it.toString())
}

```

for use cashing system :
```kotlin
ApiService().getCarBrands() {
    print(it.toString())

}.fromCash("someKey") {
    print(it.toString())
}
```

handle error :

```kotlin
ApiService().getCarBrands() {
     print(it.toString())

}.fromCash("someKey") {
     print(it.toString())

}.onError {
     print(it.toString())
}
```

using Disposable:

```kotlin
var myDisposable = CompositeDisposable()

ApiService().getCarBrands() {
     print(it.toString())
}.fromCash("as") {
     print(it.toString())

}.onError {
     print(it.toString())

}.addToDisposable(myDisposable)
```



