package co.atrasvida.avidawebapi.compiler

import co.atrasvida.avidawebapi_annotations.GreetingGenerator
import co.atrasvida.avidawebapi_annotations.WebApi
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(FileGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class FileGenerator : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GreetingGenerator::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }


    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment?
    ): Boolean {

        for (typeElement in set!!) {
            System.out.println("------------------------>" + typeElement)
        }

        //  roundEnvironment?.getElementsAnnotatedWith(GreetingGenerator::class.java)
        //      ?.forEach {
        //          val className = it.simpleName.toString()
        //          val pack = processingEnv.elementUtils.getPackageOf(it).toString()
        //          generateClass(className, pack)
        //      }

        roundEnvironment?.getElementsAnnotatedWith(WebApi::class.java)
            ?.forEach {
                var webapi: WebApi = it.getAnnotation(WebApi::class.java)
                System.out.println("------------->" + webapi.value)

                val className = it.simpleName.toString()
                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                //generateClass(className, pack)
                //ElementFilter.methodsIn(typeElement.getEnclosedElements())

                var metodsString = getApiClientClassData(className, webapi.baseUrl)
                for (executableElement in ElementFilter.methodsIn(it.enclosedElements)) {

                    executableElement.getSimpleName()
                    executableElement.defaultValue
                    System.out.println("------------->" + executableElement.defaultValue)

                    var parametrString = ""
                    var parameterSize = executableElement.parameters!!.size
                    for (i in 0 until parameterSize) {
                        var parameter = executableElement.parameters!![i]
                        parametrString += (parameter.simpleName.toString() + ":" + toKatlin(
                            parameter.asType()
                        ) + ",")
                    }
                    var parametrStringVal = ""
                    for (i in 0 until parameterSize) {
                        var parameter = executableElement.parameters!![i]
                        parametrStringVal += (parameter.simpleName.toString())
                        if (i < parameterSize - 1) {
                            parametrStringVal += ","
                        }
                    }

                    var returnType =
                        //handleList
                        (executableElement.returnType)//.javaClass//.genericSuperclass

                    var javaClass = returnType.toString().substring(
                        "io.reactivex.Observable<".length,
                        returnType.toString().lastIndex
                    )

                    javaClass = javaClass.replace("java.lang.Object", "Any")

                    metodsString +=
                        "       internal fun ${executableElement.simpleName}( $parametrString onSuccess: ($javaClass) -> Unit) = networkApiService!!\n" +
                                "             .${executableElement.simpleName}($parametrStringVal)\n" +
                                "             .compose(configureApiCallObserver())\n" +
                                "             .subscribeWith(object : MyDisposableObserver<$javaClass>(onSuccess) {})\n\n\n"
                }
                generateClass(webapi.value, pack, metodsString)
            }
        try {
            getDOClass(
                processingEnv.elementUtils.getPackageOf(
                    roundEnvironment?.getElementsAnnotatedWith(
                        WebApi::class.java
                    )?.elementAt(0)
                ).toString()
            )
        } catch (e: Exception) {
        }

        try {
            getDeserializer(
                processingEnv.elementUtils.getPackageOf(
                    roundEnvironment?.getElementsAnnotatedWith(
                        WebApi::class.java
                    )?.elementAt(0)
                ).toString()
            )
        } catch (e: Exception) {
        }
        return true
    }

    inline fun <reified T> handleList(l: Class<T>) {
        //T::class.qualifiedName
        l.javaClass.genericSuperclass
    }

    private fun toKatlin(asType: TypeMirror?): String? {
        var type = asType.toString()

        return when (type) {
            "int" -> "Int"
            "java.lang.String" -> "kotlin.String"
            else -> type
        }

    }

    private fun generateClass(
        className: String, pack: String,
        greeting: String = "Merry Christmas!!"
    ) {
        val fileName =className
        val fileContent = KotlinClassBuilder(fileName, pack, getImports(), greeting).getContent()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$fileName.kt")

        file.writeText(fileContent)
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private fun getApiClientClassData(className: String,baseApi: String): String {
        return "private var networkApiService: $className? = null\n" +
                "    private val ContentType = \"application/json\"\n" +
                "    private val TAG = \"API_CLIENT\"\n" +
                "\n" +
                "    @Throws(Exception::class)\n" +
                "    override fun accept(throwable: Throwable) {\n" +
                "        val throwableClass: Class<*> = throwable.javaClass\n" +
                "        if (throwableClass == SocketTimeoutException::class.java || UnknownHostException::class.java == throwableClass) {\n" +
                "            Log.e(TAG, \"accept: UnknownHostException\")\n" +
                "        } else if (JsonSyntaxException::class.java.isAssignableFrom(throwableClass)) {\n" +
                "            Log.e(TAG, \"accept: JsonSyntaxException \")\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    private val apiCallTransformer: ObservableTransformer<*, *> =\n" +
                "        ObservableTransformer<Any, Any> { observable: Observable<*> ->\n" +
                "            observable.map { appResponse -> appResponse }\n" +
                "                .subscribeOn(Schedulers.io())\n" +
                "                .retryWhen(RetryWithDelay()).doOnError(this)\n" +
                "                .observeOn(AndroidSchedulers.mainThread())\n" +
                "        }\n" +
                "\n" +
                "    private fun <T> configureApiCallObserver(): ObservableTransformer<T, T> {\n" +
                "        return apiCallTransformer as ObservableTransformer<T, T>\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    init {\n" +
                "        val logging = HttpLoggingInterceptor()\n" +
                "        if (BuildConfig.DEBUG) {\n" +
                "            logging.level = HttpLoggingInterceptor.Level.BODY\n" +
                "        }\n" +
                "        var okHttpClientBuilder = OkHttpClient.Builder()\n" +
                "            .addInterceptor(logging)\n" +
                "            .readTimeout(20, TimeUnit.SECONDS)\n" +
                "            .connectTimeout(20, TimeUnit.SECONDS)\n" +
                "            .writeTimeout(30, TimeUnit.SECONDS)\n" +
                "\n" +
                "        val token = getToken()\n" +
                "        if (token != null) {\n" +
                "            okHttpClientBuilder.addInterceptor { chain ->\n" +
                "                val request = chain.request().newBuilder()\n" +
                "                request.addHeader(\"Authorization\", \"Bearer \$token\")\n" +
                "                chain.proceed(request.build())\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        // Config Gson\n" +
                "        val gsonBuilder = GsonBuilder()\n" +
                "        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)\n" +
                "        gsonBuilder.registerTypeAdapter(BaseResponse::class.java, Deserializer<BaseResponse<Any>>())\n" +
                "\n" +
                "        // Init Retrofit\n" +
                "        networkApiService = Retrofit.Builder()\n" +
                "            .baseUrl(\"$baseApi\")\n" +
                "            .client(okHttpClientBuilder.build())\n" +
                "            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())\n" +
                "            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))\n" +
                "            .build()\n" +
                "            .create($className::class.java)\n" +
                "    }\n" +
                "\n" +
                "    private fun getToken(): String? {\n" +
                "        //   if (AppDataManager.usersInfo != null)\n" +
                "        //       return AppDataManager.usersInfo?.token\n" +
                "        //   else\n" +
                "        //       if (AppDatabases.getInstance() != null)\n" +
                "        //           if (AppDatabases.getInstance()!!.userDao().getOneById() != null)\n" +
                "        //               return AppDatabases.getInstance()!!.userDao().getOneById()!!.token\n" +
                "        return null\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    internal class RetryWithDelay :\n" +
                "        Function<Observable<out Throwable?>, ObservableSource<*>> {\n" +
                "        private val maxRetries = 3\n" +
                "        private var retryCount = 0\n" +
                "        @Throws(Exception::class)\n" +
                "        override fun apply(attempts: Observable<out Throwable?>): Observable<*> {\n" +
                "            return attempts\n" +
                "                .flatMap { throwable: Throwable? ->\n" +
                "                    if (throwable is TimeoutException || throwable is SocketTimeoutException) {\n" +
                "                        if (++retryCount < maxRetries) {\n" +
                "                            return@flatMap Observable.timer(\n" +
                "                                2.0.pow(retryCount.toDouble()).toLong(),\n" +
                "                                TimeUnit.SECONDS\n" +
                "                            )\n" +
                "                        }\n" +
                "                    }\n" +
                "                    Observable.error<Any?>(throwable)\n" +
                "                }\n" +
                "        }\n" +
                "    }\n"

    }

    fun getImports(): String {
        return "import android.util.Log\n" +
                // "import MyDisposableObserver\n" +
                "import co.atrasvida.avidawebapi.BuildConfig\n" +
                "import com.google.gson.FieldNamingPolicy\n" +
                "import com.google.gson.GsonBuilder\n" +
                "import com.google.gson.JsonSyntaxException\n" +
                //"import com.pintoads.himasdk.BuildConfig\n" +
                //"import com.pintoads.himasdk.webservice.models.AppKysResponse\n" +
                //"import com.pintoads.himasdk.webservice.models.BaseResponse\n" +
                //"import com.pintoads.himasdk.webservice.models.ReportClickRequest\n" +
                //"import com.pintoads.himasdk.webservice.models.RequestAdModel\n" +
                "import io.reactivex.Observable\n" +
                "import io.reactivex.ObservableSource\n" +
                "import io.reactivex.ObservableTransformer\n" +
                "import io.reactivex.android.schedulers.AndroidSchedulers\n" +
                "import io.reactivex.functions.Consumer\n" +
                "import io.reactivex.functions.Function\n" +
                "import io.reactivex.schedulers.Schedulers\n" +
                "import okhttp3.OkHttpClient\n" +
                "import okhttp3.logging.HttpLoggingInterceptor\n" +
                "import retrofit2.Retrofit\n" +
                "import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory\n" +
                "import retrofit2.converter.gson.GsonConverterFactory\n" +
                "import java.net.SocketTimeoutException\n" +
                "import java.net.UnknownHostException\n" +
                "import java.util.concurrent.TimeUnit\n" +
                "import java.util.concurrent.TimeoutException\n" +
                "import kotlin.math.pow"
    }

    fun getDeserializer(pack: String) {
        var fileContent =
            "package $pack\n" +
                    "import com.google.gson.Gson\n" +
                    "import com.google.gson.JsonDeserializationContext\n" +
                    "import com.google.gson.JsonDeserializer\n" +
                    "import com.google.gson.JsonElement\n" +
                    "import java.lang.reflect.Type\n" +
                    "\n" +
                    "open class Deserializer<T> : JsonDeserializer<T> {\n" +
                    "    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {\n" +
                    "        val content = json!!.asJsonObject\n" +
                    "\n" +
                    "        // Deserialize it. You use a new instance of Gson to avoid infinite recursion to this deserializer\n" +
                    "        return Gson().fromJson(content, typeOfT)\n" +
                    "    }\n" +
                    "}"
        val fileName = "Deserializer"

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$fileName.kt")

        file.writeText(fileContent)
    }

    fun getDOClass(pack: String) {
        var fileContent = "\n" +
                "package $pack\n" +
                "import android.util.Log\n" +
                "import com.google.gson.GsonBuilder\n" +
                "import io.reactivex.disposables.CompositeDisposable\n" +
                "import io.reactivex.observers.DisposableObserver\n" +
                "import retrofit2.HttpException\n" +
                "\n" +
                "/**\n" +
                " * Created by AvidA\n" +
                " */\n" +
                "abstract class MyDisposableObserver<T>(var onSuccess: (T) -> Unit) : DisposableObserver<T>() {\n" +
                "\n" +
                "    override fun onNext(t: T) {\n" +
                "        onSuccess(t)\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    override fun onError(throwable: Throwable) {\n" +
                "        if (::mThrowable.isInitialized)\n" +
                "            mThrowable(throwable)\n" +
                "\n" +
                "\n" +
                "        Log.e(\n" +
                "            TAG,\n" +
                "            \"onError() called with: throwable = [\$throwable]\"\n" +
                "        )\n" +
                "        Log.e(\"\", GsonBuilder().setPrettyPrinting().create().toJson(throwable))\n" +
                "        val throwableClass: Class<*> = throwable.javaClass\n" +
                "        if (HttpException::class.java.isAssignableFrom(throwableClass)) {\n" +
                "            Log.e(\n" +
                "                TAG,\n" +
                "                \"There is an error in network call\"\n" +
                "            )\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    override fun onComplete() {}\n" +
                "\n" +
                "\n" +
                "    lateinit private var mThrowable: (Throwable) -> Unit\n" +
                "    fun onError(throwable: (Throwable) -> Unit): MyDisposableObserver<T> {\n" +
                "        mThrowable = throwable\n" +
                "        return this\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    fun addToDisposable(function: CompositeDisposable) {\n" +
                "        function.add(this)\n" +
                "    }\n" +
                "\n" +
                "    companion object {\n" +
                "        private const val TAG = \"MyDisposableObserver\"\n" +
                "    }" +
                "}\n"

        val fileName = "MyDisposableObserver"

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$fileName.kt")

        file.writeText(fileContent)


    }
}