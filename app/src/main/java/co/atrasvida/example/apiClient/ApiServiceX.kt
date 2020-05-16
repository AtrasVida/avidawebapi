package co.atrasvida.example.apiClient

import android.util.Log
import co.atrasvida.avidawebapi.database.AvidaAppDatabases
import co.atrasvida.avidawebapi.database.MCash
import co.atrasvida.avidawebapi.example.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

import java.util.concurrent.TimeoutException
import kotlin.math.pow

public class ApiServiceX : Consumer<Throwable> {
    private var networkApiService: NetworkApiService? = null
    private val Authorization = "ApiKey 75kY0sSlOILOlfLE6hQbFu57f2VA1JirTEy79Thy"
    private val ContentType = "application/json"
    private val TAG = "API_CLIENT"
    private var needToken: Boolean = true

    @Throws(Exception::class)
    override fun accept(throwable: Throwable) {
        val throwableClass: Class<*> = throwable.javaClass
        if (throwableClass == SocketTimeoutException::class.java || UnknownHostException::class.java == throwableClass) {
            Log.e(TAG, "accept: UnknownHostException")
        } else if (JsonSyntaxException::class.java.isAssignableFrom(throwableClass)) {
            Log.e(TAG, "accept: JsonSyntaxException ")
        }
    }


    private val apiCallTransformer: ObservableTransformer<*, *> =
        ObservableTransformer<Any, Any> { observable: Observable<*> ->
            observable.map { appResponse -> appResponse }
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay()).doOnError(this)
                .observeOn(AndroidSchedulers.mainThread())
        }

    private fun <T> configureApiCallObserver(): ObservableTransformer<T, T> {
        return apiCallTransformer as ObservableTransformer<T, T>
    }


    constructor()

    constructor(needToken: Boolean) {
        this.needToken = needToken
    }

    init {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        var okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (needToken) {
            val token = getToken()
            if (token != null) {
                okHttpClientBuilder.addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    request.addHeader("Authorization", "Bearer $token")
                    chain.proceed(request.build())
                }
            }
        }

        // Config Gson
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        gsonBuilder.registerTypeAdapter(BaseResponse::class.java, DeserializerX<BaseResponse<Any>>())

        // Init Retrofit
        networkApiService = Retrofit.Builder()
            .baseUrl(URL.BASE_URL)
            .client(okHttpClientBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build()
            .create(NetworkApiService::class.java)
    }

    private fun getToken(): String? {
        //  if (AppDataManager.usersInfo != null)
        //      return AppDataManager.usersInfo?.token
        //  else
        //      if (AppDatabases.getInstance() != null)
        //          if (AppDatabases.getInstance()!!.userDao().getOneById() != null)
        //              return AppDatabases.getInstance()!!.userDao().getOneById()!!.token
        return null

    }

    internal class RetryWithDelay :
        Function<Observable<out Throwable?>, ObservableSource<*>> {
        private val maxRetries = 3
        private var retryCount = 0
        @Throws(Exception::class)
        override fun apply(attempts: Observable<out Throwable?>): Observable<*> {
            return attempts
                .flatMap { throwable: Throwable? ->
                    if (throwable is TimeoutException || throwable is SocketTimeoutException) {
                        if (++retryCount < maxRetries) {
                            return@flatMap Observable.timer(
                                2.0.pow(retryCount.toDouble()).toLong(),
                                TimeUnit.SECONDS
                            )
                        }
                    }
                    Observable.error<Any?>(throwable)
                }
        }
    }

    fun getCarBrandsx(onSuccess: (BaseResponse<CarBrandsModel>) -> Unit) = networkApiService!!
        .getCarBrands()
        .compose(configureApiCallObserver())
        .subscribeWith(object : MyDisposableObserverX<BaseResponse<CarBrandsModel>>(onSuccess) {})


    internal fun getCarBrands(i: Int, onSuccess: (BaseResponse<CarBrandsModel>) -> Unit)
            : MyDisposableObserverX<BaseResponse<CarBrandsModel>> {

        var objByToken = AvidaAppDatabases.getInstance()!!.mCashDao().getObjByToken("getUserInfo"+i)

        var haveObj = false
        if (objByToken != null) {
            haveObj = true
            val e = Gson().fromJson(
                objByToken.data_val,
                co.atrasvida.example.apiClient.BaseResponse::class.java
            )
            onSuccess(e as BaseResponse<CarBrandsModel>)
        }

        var sb = object : MyDisposableObserverX<BaseResponse<CarBrandsModel>>(onSuccess) {
            override fun onNext(t: BaseResponse<CarBrandsModel>) {
                super.onNext(t)

                var jsonStr = Gson().toJson(t)

                if (haveObj) {
                    AvidaAppDatabases.getInstance()!!.mCashDao()
                        .updateObj("getUserInfo"+i, jsonStr)
                } else {
                    AvidaAppDatabases.getInstance()!!.mCashDao()
                        .insert(MCash(0, "getUserInfo"+i, jsonStr))
                }
            }
        }
        return networkApiService!!.getCarBrands()
            .compose(configureApiCallObserver())
            .subscribeWith(sb)
    }


}