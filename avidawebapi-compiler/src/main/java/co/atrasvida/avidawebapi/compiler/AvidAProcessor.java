package co.atrasvida.avidawebapi.compiler;

import com.google.auto.service.AutoService;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import co.atrasvida.avidawebapi_annotations.WebApi;


@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@SuppressWarnings("NullAway")
public final class AvidAProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(WebApi.class);
        return annotations;
    }

    @Override

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (TypeElement typeElement : set) {
            System.out.println("------------------------>" + typeElement);
        }

        //  roundEnvironment?.getElementsAnnotatedWith(GreetingGenerator::class.java)
        //      ?.forEach {
        //          val className = it.simpleName.toString()
        //          val pack = processingEnv.elementUtils.getPackageOf(it).toString()
        //          generateClass(className, pack)
        //      }

        boolean isFirst = true;
        for (Element it : roundEnvironment.getElementsAnnotatedWith(WebApi.class)) {
            String pack = processingEnv.getElementUtils().getPackageOf(it).toString();
            if (isFirst) {
                getDOClass(pack);
                getDeserializer(pack);
            }

            WebApi webapi = it.getAnnotation(WebApi.class);
            System.out.println("------------->" + webapi.value());

            String className = it.getSimpleName().toString();

            //generateClass(className, pack)
            //ElementFilter.methodsIn(typeElement.getEnclosedElements())

            String metodsString = getApiClientClassData(className, webapi.baseUrl());
            for (ExecutableElement executableElement : ElementFilter.methodsIn(it.getEnclosedElements())) {

                executableElement.getSimpleName();
                executableElement.getDefaultValue();
                System.out.println("------------->" + executableElement.getDefaultValue());

                String parametrString = "";
                int parameterSize = executableElement.getParameters().size();
                for (int i = 0; i < parameterSize; i++) {
                    VariableElement parameter = executableElement.getParameters().get(i);
                    parametrString += (parameter.getSimpleName().toString() + ":" + toKatlin(
                            parameter.asType()
                    ) + ",");
                }
                String parametrStringVal = "";
                for (int i = 0; i < parameterSize; i++) {
                    VariableElement parameter = executableElement.getParameters().get(i);
                    parametrStringVal += (parameter.getSimpleName().toString());
                    if (i < parameterSize - 1) {
                        parametrStringVal += ",";
                    }
                }

                TypeMirror returnType = (executableElement.getReturnType());
                String javaClass = returnType.toString().substring(
                        "io.reactivex.Observable<".length(), returnType.toString().length() - 1
                );

                javaClass = javaClass.replace("java.lang.Object", "Any");

                metodsString +=
                        "       internal fun " + executableElement.getSimpleName() + "( " + parametrString + " onSuccess: (" + javaClass + ") -> Unit) = networkApiService!!\n" +
                                "             ." + executableElement.getSimpleName() + "(" + parametrStringVal + ")\n" +
                                "             .compose(configureApiCallObserver())\n" +
                                "             .subscribeWith(object : MyDisposableObserver<" + javaClass + ">(onSuccess) {})\n\n\n";
            }
            generateClass(webapi.value(), pack, metodsString);
        }


        return true;
    }

   /*     inline fun

    <reified T>handleList(l:Class<T>){
            //T::class.qualifiedName
            l.javaClass.genericSuperclass
        }*/

    private String toKatlin(TypeMirror asType) {
        String type = asType.toString();

        if (type.equals("int")) return "Int";
        else if (type.equals("java.lang.String")) return "kotlin.String";
        else return type;

    }

    private void generateClass(String className, String pack, String greeting) {
        String fileName = className;
        String fileContent = new KotlinClassBuilder(fileName, pack, getImports(), greeting).getContent();

        //String kaptKotlinGeneratedDir = processingEnv.getOptions().get(KAPT_KOTLIN_GENERATED_OPTION_NAME);
        FileWr(processingEnv, fileName, fileContent);
        //writeText(kaptKotlinGeneratedDir + fileName + ".kt", fileContent);
    }


    static final String KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated";


    private String getApiClientClassData(String className, String baseApi) {
        return "private var networkApiService: " + className + "? = null\n" +
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
                "                request.addHeader(\"Authorization\", \"Bearer $token\")\n" +
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
                "            .baseUrl(\"" + baseApi + " \")\n" +
                "            .client(okHttpClientBuilder.build())\n" +
                "            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())\n" +
                "            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))\n" +
                "            .build()\n" +
                "            .create(" + className + "::class.java)\n" +
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
                "    }\n";

    }

    String getImports() {
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
                "import kotlin.math.pow";
    }

    void getDeserializer(String pack) {
        String fileContent =
                "package " + pack + "\n" +
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
                        "}";
        String fileName = "Deserializer";

        //String kaptKotlinGeneratedDir = processingEnv.getOptions().get(KAPT_KOTLIN_GENERATED_OPTION_NAME);
        FileWr(processingEnv, fileName, fileContent);
        //writeText(kaptKotlinGeneratedDir + fileName + ".kt", fileContent);
    }

    void getDOClass(String pack) {
        String fileContent = "\n" +
                "package " + pack + "\n" +
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
                "            \"onError() called with: throwable = [$throwable] \" \n" +
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
                "}\n";

        String fileName = "MyDisposableObserver";

        String kaptKotlinGeneratedDir = processingEnv.getOptions().get(KAPT_KOTLIN_GENERATED_OPTION_NAME);

        //writeText(kaptKotlinGeneratedDir + fileName + ".kt", fileContent);
        FileWr(processingEnv, fileName, fileContent);
    }


    public void FileWr(ProcessingEnvironment processingEnv, String fileName, String fileContent) {

        String kaptKotlinGeneratedDir = processingEnv.getOptions().get(KAPT_KOTLIN_GENERATED_OPTION_NAME);
        // File file = File(kaptKotlinGeneratedDir, fileName+".kt");

        try {
            FileWriter myWriter = new FileWriter(kaptKotlinGeneratedDir + "/" + fileName + ".kt");
            myWriter.write(fileContent);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}