package co.atrasvida.avidawebapi.compiler;

import com.google.auto.service.AutoService;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import co.atrasvida.avidawebapi_annotations.BindView;
import co.atrasvida.avidawebapi_annotations.CACHING;
import co.atrasvida.avidawebapi_annotations.CachSetting;
import co.atrasvida.avidawebapi_annotations.Keep;
import co.atrasvida.avidawebapi_annotations.OnClick;
import co.atrasvida.avidawebapi_annotations.WebApi;


@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@SuppressWarnings("NullAway")
public final class AvidAProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
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

        boolean isFirst = true;
        for (Element it : roundEnvironment.getElementsAnnotatedWith(WebApi.class)) {
            String pack = processingEnv.getElementUtils().getPackageOf(it).toString();
            if (isFirst) {
                isFirst = false;

                FileWr(pack, "MyDisposableObserver", getDOClass(pack), roundEnvironment, it);
                FileWr(pack, "Deserializer", getDeserializer(pack), roundEnvironment, it);
                FileWr(pack, "AvidaAppDatabases", getDatabaseClass(pack), roundEnvironment, it);
                FileWrJava(pack, "AvidaAppDatabases_Impl", getDatabaseImplClass(pack), roundEnvironment, it);
                FileWr(pack, "MCashDao", getMCashDaoClass(pack), roundEnvironment, it);
                FileWr(pack, "MCash", getMCashClass(pack), roundEnvironment, it);

            }

            WebApi webapi = it.getAnnotation(WebApi.class);
            String configClassName = "";
            try {
                Class a = webapi.config(); // this should throw
            } catch (MirroredTypeException mte) {
                configClassName = mte.getTypeMirror().toString();
                System.out.println("------------->" + mte.getTypeMirror());
            }

            String className = it.getSimpleName().toString();
            System.out.println("------------->" + className);


            StringBuilder metodsString = new StringBuilder(getApiClientClassData(className, configClassName));
            for (ExecutableElement executableElement : ElementFilter.methodsIn(it.getEnclosedElements())) {

                executableElement.getSimpleName();
                executableElement.getDefaultValue();
                System.out.println("------------->" + executableElement.getDefaultValue());

                StringBuilder parametrString = new StringBuilder();
                int parameterSize = executableElement.getParameters().size();
                for (int i = 0; i < parameterSize; i++) {
                    VariableElement parameter = executableElement.getParameters().get(i);
                    parametrString.append(parameter.getSimpleName().toString()).append(":").append(toKatlin(
                            parameter.asType()
                    )).append(",");
                }
                StringBuilder parametrStringVal = new StringBuilder();
                for (int i = 0; i < parameterSize; i++) {
                    VariableElement parameter = executableElement.getParameters().get(i);
                    parametrStringVal.append(parameter.getSimpleName().toString());
                    if (i < parameterSize - 1) {
                        parametrStringVal.append(",");
                    }
                }

                TypeMirror returnType = (executableElement.getReturnType());
                String javaClass = returnType.toString().substring(
                        "io.reactivex.Observable<".length(), returnType.toString().length() - 1
                );

                CachSetting annotationsOfMetud = CachSetting.first_From_Cash_Then_Api;
                CACHING annotation = executableElement.getAnnotation(CACHING.class);

                if (annotation != null)
                    annotationsOfMetud = annotation.value();

                String cashPolocy = "";
                String onSuccess = "";
                if (annotationsOfMetud == CachSetting.first_From_Cash_Then_Api) {
                    cashPolocy = "  super.onNext(t)\n";
                } else if (annotationsOfMetud == CachSetting.first_cash_and_if_updated) {
                    cashPolocy = " " +
                            "                    if (objByToken == null) {\n" +
                            "                        super.onNext(t)\n" +
                            "                    } else if (objByToken!!.data_val != Gson().toJson(t)) {\n" +
                            "                        super.onNext(t)\n" +
                            "                    }";
                }

                javaClass = javaClass.replace("java.lang.Object", "Any");

                metodsString.append("       internal fun ").append(executableElement.getSimpleName())
                        .append("( ").append(parametrString)
                        .append(" onSuccess: (").append(javaClass).append(") -> Unit) ")
                        .append(": MyDisposableObserver<").append(javaClass).append(">{\n")

                        .append("        var sb = object : MyDisposableObserver<" + javaClass + ">(onSuccess) {\n" +

                                "            var objByToken : MCash? = null \n \n " +
                                "            override fun onNext(t: " + javaClass + ") {\n" +
                                "                " + cashPolocy + " \n" +
                                " \n" +
                                "                if (isCacheableInitialized()) {\n" +
                                "                    var mToken= \"" + executableElement.getSimpleName() + "\"+ mCacheableToken\n\n " +
                                "                    var objByToken =\n" +
                                "                        AvidaAppDatabases.getInstance()!!.mCashDao().getObjByToken(mToken)\n" +
                                "\n" +
                                "                    var jsonStr = Gson().toJson(t)\n" +
                                "\n" +
                                "                    if (objByToken != null) {\n" +
                                "                        AvidaAppDatabases.getInstance()!!.mCashDao()\n" +
                                "                            .updateObj(mToken, jsonStr)\n" +
                                "                    } else {\n" +
                                "                        AvidaAppDatabases.getInstance()!!.mCashDao()\n" +
                                "                            .insert(MCash(null, mToken, jsonStr))\n" +
                                "                    }\n" +
                                "                }" +
                                "            }\n" +
                                "\n" +
                                "            override fun onStart() {\n" +
                                "                super.onStart()\n" +
                                "                if (isCacheableInitialized()) {\n" +
                                "                    var mToken= \"" + executableElement.getSimpleName() + "\"+ mCacheableToken\n\n " +
                                "                     objByToken =\n" +
                                "                        AvidaAppDatabases.getInstance()!!.mCashDao().getObjByToken(mToken)\n" +
                                "\n" +
                                "                    if (objByToken != null) {\n" +
                                "                        val e : " + javaClass + " = Gson().fromJson(\n" +
                                "                            objByToken?.data_val,\n" +
                                "                              object : com.google.gson.reflect.TypeToken<" + javaClass + ">(){}.type \n" +
                                // "                            " + configClassName + " ().getBaseModel()\n" +
                                "                        )\n" +
                                "                         mCacheable(e as  " + javaClass + ")\n" +
                                "                    }\n" +
                                "                }\n" +
                                "            }\n\n" +

                                "        }\n\n")
                        .append("        android.os.Handler().post {\n" +
                                "             networkApiService!!." + executableElement.getSimpleName() + "(" + parametrStringVal + ") \n" +
                                "            .compose(configureApiCallObserver())\n" +
                                "            .subscribeWith(sb)\n        }\n\n")
                        .append("        return sb" + " \n" +
                                " }");
            }

            String fileName = webapi.value();
            String fileContent = new KotlinClassBuilder(fileName, pack, getImports(pack), metodsString.toString()).getContent();

            FileWr(pack, fileName, fileContent, roundEnvironment, it);
        }


        return true;
    }

    private String toKatlin(TypeMirror asType) {
        String type = asType.toString();
        if (type.equals("int")) return "Int";
        if (type.equals("boolean")) return "Boolean";
        if (type.equals("long")) return "Long";
        else if (type.equals("java.lang.String")) return "kotlin.String";
        else return type;

    }

    private String getApiClientClassData(String className, String conf) {
        return "private var networkApiService: " + className + "? = null\n" +
                "    private val ContentType = \"application/json\"\n" +
                "    private val TAG = \"API_CLIENT\"\n" +
                "    private var needToken: Boolean = true \n" +
                "    private var conf = " + conf + "()\n" +
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
                "    constructor()\n" +
                "\n" +
                "    constructor(needToken: Boolean) {\n" +
                "        this.needToken = needToken\n" +
                "    }\n" +
                "    init {\n" +
                "        val logging = HttpLoggingInterceptor()\n" +
                "        if (conf.isDebugMode()) {\n" +
                "            logging.level = HttpLoggingInterceptor.Level.BODY\n" +
                "        }\n" +
                "        var okHttpClientBuilder = OkHttpClient.Builder()\n" +
                "            .addInterceptor(logging)\n" +
                "            .readTimeout(conf.getReadTimeout(), TimeUnit.SECONDS)\n" +
                "            .connectTimeout(conf.getConnectTimeout(), TimeUnit.SECONDS)\n" +
                "            .writeTimeout(conf.getWriteTimeout(), TimeUnit.SECONDS)\n" +
                "\n" +
                "        if (needToken) {\n" +
                "            val token = conf.getToken()\n" +
                "            if (token != null) {\n" +
                "                okHttpClientBuilder.addInterceptor { chain ->\n" +
                "                    val request = chain.request().newBuilder()\n" +
                "                    request.addHeader(\"Authorization\", token)\n" +
                "                    chain.proceed(request.build())\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        // Config Gson\n" +
                "        val gsonBuilder = GsonBuilder()\n" +
                "        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)\n" +
                "        gsonBuilder.registerTypeAdapter(conf.getBaseModel(), Deserializer<BaseResponse<Any>>())\n" +
                "\n" +
                "        // Init Retrofit\n" +
                "        networkApiService = Retrofit.Builder()\n" +
                "            .baseUrl(conf.getBaseUrl()" + " )\n" +
                "            .client(okHttpClientBuilder.build())\n" +
                "            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())\n" +
                "            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))\n" +
                "            .build()\n" +
                "            .create(" + className + "::class.java)\n" +
                "    }\n" +
                "\n" +
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

    String getImports(String pack) {
        return "import android.util.Log\n" +
                // "import MyDisposableObserver\n" +
                "import co.atrasvida.avidawebapi.BuildConfig\n" +
                "import com.google.gson.FieldNamingPolicy\n" +
                "import com.google.gson.GsonBuilder\n" +
                "import com.google.gson.JsonSyntaxException\n" +
                "import com.google.gson.Gson\n" +
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

    String getDeserializer(String pack) {
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


        return fileContent;

    }

    String getMCashClass(String pack) {
        String fileContent =
                "package " + pack + "\n" +
                        "\n" +
                        "import androidx.room.Entity\n" +
                        "import androidx.room.PrimaryKey\n" +
                        "\n" +
                        "/**\n" +
                        " * Table for store tableName\n" +
                        " *\n" +
                        " * @property id primary key of local table\n" +
                        " */\n" +
                        "@Entity(tableName = \"m_cash\")\n" +
                        "data class MCash(\n" +
                        "    @PrimaryKey(autoGenerate = true) var id: Int? = null,\n" +
                        "    var token: String,\n" +
                        "    var data_val: String?\n" +
                        "\n" +
                        ")";


        return fileContent;

    }

    String getMCashDaoClass(String pack) {
        String fileContent =
                "package " + pack + "\n" +
                        "import androidx.room.Dao\n" +
                        "import androidx.room.Insert\n" +
                        "import androidx.room.OnConflictStrategy\n" +
                        "import androidx.room.Query\n" +
                        "\n" +
                        "@Dao\n" +
                        "interface MCashDao {\n" +
                        "\n" +
                        "    @Insert(onConflict = OnConflictStrategy.REPLACE)\n" +
                        "    fun insert(users: MCash)\n" +
                        "\n" +
                        "    @Query(\"delete from m_cash\")\n" +
                        "    fun deleteAll(): Int\n" +
                        "\n" +
                        "    @Query(\"select * from m_cash where id=:id limit 1\")\n" +
                        "    fun getOneById(id: Int): MCash?\n" +
                        "\n" +
                        "    @Query(\"select * from m_cash \")\n" +
                        "    fun getAllObjects(): Array<MCash>?\n" +
                        "\n" +
                        "    @Query(\"select * from m_cash limit 1\")\n" +
                        "    fun getOneById(): MCash?\n" +
                        "\n" +
                        "    @Query(\"select * from m_cash where m_cash.token= :mToken limit 1\")\n" +
                        "    fun getObjByToken(mToken: String): MCash?\n" +
                        "\n" +
                        "    @Query(\"UPDATE m_cash SET  data_val = :vae where m_cash.token= :mToken \")\n" +
                        "    fun updateObj(mToken: String, vae: String): Int\n" +
                        "}";


        return fileContent;

    }

    String getDatabaseClass(String pack) {
        String fileContent = "\n" +
                "package " + pack + "\n" +
                "\n" +
                "import android.content.Context\n" +
                "import androidx.room.Database\n" +
                "import androidx.room.Room\n" +
                "import androidx.room.RoomDatabase\n" +
                "\n" +
                "\n" +
                "@Database(entities = [MCash::class], version = 1)\n" +
                "abstract class AvidaAppDatabases : RoomDatabase() {\n" +
                "\n" +
                "    companion object {\n" +
                "        private const val dataBaseName: String = \"api_casher.db\"\n" +
                "        var dbInstance: AvidaAppDatabases? = null\n" +
                "        @JvmStatic\n" +
                "        fun getInstance(): AvidaAppDatabases? {\n" +
                "            return dbInstance\n" +
                "        }\n" +
                "        @JvmStatic\n" +
                "        fun newInstance(context: Context): AvidaAppDatabases? {\n" +
                "            if (dbInstance == null) {\n" +
                "                synchronized(MCash::class.java) {\n" +
                "                    dbInstance =\n" +
                "                        Room.databaseBuilder(context, AvidaAppDatabases::class.java, dataBaseName)\n" +
                "                            .allowMainThreadQueries()\n" +
                "                            .fallbackToDestructiveMigration()\n" +
                "                            .build()\n" +
                "                }\n" +
                "            }\n" +
                "            return dbInstance\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public abstract fun mCashDao(): MCashDao\n" +
                "\n" +
                "\n" +
                "    fun destroyInstance() {\n" +
                "        dbInstance = null\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";

        return fileContent;
    }

    String getDOClass(String pack) {
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
                "\n" +
                "    internal lateinit var mCacheable: (T) -> Unit\n" +
                "    internal lateinit  var mCacheableToken: String\n" +
                "    fun fromCash(cacheableToken: String, cacheable: (T) -> Unit): MyDisposableObserver<T> {\n" +
                "        mCacheable = cacheable\n" +
                "        mCacheableToken = cacheableToken\n" +
                "        return this\n" +
                "    }\n" +
                "\n" +
                "    fun isCacheableInitialized(): Boolean {\n" +
                "        return ::mCacheable.isInitialized\n" +
                "    }\n\n" +
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

        return fileContent;
    }

    String getDatabaseImplClass(String pack) {
        String fileContent = "\n" +
                "package " + pack + ";\n" +
                "\n" +
                "import androidx.room.DatabaseConfiguration;\n" +
                "import androidx.room.InvalidationTracker;\n" +
                "import androidx.room.RoomOpenHelper;\n" +
                "import androidx.room.RoomOpenHelper.Delegate;\n" +
                "import androidx.room.RoomOpenHelper.ValidationResult;\n" +
                "import androidx.room.util.DBUtil;\n" +
                "import androidx.room.util.TableInfo;\n" +
                "import androidx.room.util.TableInfo.Column;\n" +
                "import androidx.room.util.TableInfo.ForeignKey;\n" +
                "import androidx.room.util.TableInfo.Index;\n" +
                "import androidx.sqlite.db.SupportSQLiteDatabase;\n" +
                "import androidx.sqlite.db.SupportSQLiteOpenHelper;\n" +
                "import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;\n" +
                "import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "import java.lang.SuppressWarnings;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "@SuppressWarnings({\"unchecked\", \"deprecation\"})\n" +
                "public final class AvidaAppDatabases_Impl extends AvidaAppDatabases {\n" +
                "  private volatile MCashDao _mCashDao;\n" +
                "\n" +
                "  @Override\n" +
                "  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {\n" +
                "    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {\n" +
                "      @Override\n" +
                "      public void createAllTables(SupportSQLiteDatabase _db) {\n" +
                "        _db.execSQL(\"CREATE TABLE IF NOT EXISTS `m_cash` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `token` TEXT NOT NULL, `data_val` TEXT)\");\n" +
                "        _db.execSQL(\"CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)\");\n" +
                "        _db.execSQL(\"INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c2a4f1a97940f1152c48b1fc7832c189')\");\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      public void dropAllTables(SupportSQLiteDatabase _db) {\n" +
                "        _db.execSQL(\"DROP TABLE IF EXISTS `m_cash`\");\n" +
                "        if (mCallbacks != null) {\n" +
                "          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {\n" +
                "            mCallbacks.get(_i).onDestructiveMigration(_db);\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      protected void onCreate(SupportSQLiteDatabase _db) {\n" +
                "        if (mCallbacks != null) {\n" +
                "          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {\n" +
                "            mCallbacks.get(_i).onCreate(_db);\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      public void onOpen(SupportSQLiteDatabase _db) {\n" +
                "        mDatabase = _db;\n" +
                "        internalInitInvalidationTracker(_db);\n" +
                "        if (mCallbacks != null) {\n" +
                "          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {\n" +
                "            mCallbacks.get(_i).onOpen(_db);\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      public void onPreMigrate(SupportSQLiteDatabase _db) {\n" +
                "        DBUtil.dropFtsSyncTriggers(_db);\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      public void onPostMigrate(SupportSQLiteDatabase _db) {\n" +
                "      }\n" +
                "\n" +
                "      @Override\n" +
                "      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {\n" +
                "        final HashMap<String, TableInfo.Column> _columnsMCash = new HashMap<String, TableInfo.Column>(3);\n" +
                "        _columnsMCash.put(\"id\", new TableInfo.Column(\"id\", \"INTEGER\", false, 1, null, TableInfo.CREATED_FROM_ENTITY));\n" +
                "        _columnsMCash.put(\"token\", new TableInfo.Column(\"token\", \"TEXT\", true, 0, null, TableInfo.CREATED_FROM_ENTITY));\n" +
                "        _columnsMCash.put(\"data_val\", new TableInfo.Column(\"data_val\", \"TEXT\", false, 0, null, TableInfo.CREATED_FROM_ENTITY));\n" +
                "        final HashSet<TableInfo.ForeignKey> _foreignKeysMCash = new HashSet<TableInfo.ForeignKey>(0);\n" +
                "        final HashSet<TableInfo.Index> _indicesMCash = new HashSet<TableInfo.Index>(0);\n" +
                "        final TableInfo _infoMCash = new TableInfo(\"m_cash\", _columnsMCash, _foreignKeysMCash, _indicesMCash);\n" +
                "        final TableInfo _existingMCash = TableInfo.read(_db, \"m_cash\");\n" +
                "        if (! _infoMCash.equals(_existingMCash)) {\n" +
                "          return new RoomOpenHelper.ValidationResult(false, \"m_cash(com.pintoads.moshaver24.webservice.MCash).\\n\"\n" +
                "                  + \" Expected:\\n\" + _infoMCash + \"\\n\"\n" +
                "                  + \" Found:\\n\" + _existingMCash);\n" +
                "        }\n" +
                "        return new RoomOpenHelper.ValidationResult(true, null);\n" +
                "      }\n" +
                "    }, \"c2a4f1a97940f1152c48b1fc7832c189\", \"2a7b0ea83266efb5d03652908ef830c9\");\n" +
                "    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)\n" +
                "        .name(configuration.name)\n" +
                "        .callback(_openCallback)\n" +
                "        .build();\n" +
                "    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);\n" +
                "    return _helper;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  protected InvalidationTracker createInvalidationTracker() {\n" +
                "    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);\n" +
                "    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);\n" +
                "    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, \"m_cash\");\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public void clearAllTables() {\n" +
                "    super.assertNotMainThread();\n" +
                "    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();\n" +
                "    try {\n" +
                "      super.beginTransaction();\n" +
                "      _db.execSQL(\"DELETE FROM `m_cash`\");\n" +
                "      super.setTransactionSuccessful();\n" +
                "    } finally {\n" +
                "      super.endTransaction();\n" +
                "      _db.query(\"PRAGMA wal_checkpoint(FULL)\").close();\n" +
                "      if (!_db.inTransaction()) {\n" +
                "        _db.execSQL(\"VACUUM\");\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public MCashDao mCashDao() {\n" +
                "    if (_mCashDao != null) {\n" +
                "      return _mCashDao;\n" +
                "    } else {\n" +
                "      synchronized(this) {\n" +
                "        if(_mCashDao == null) {\n" +
                "          _mCashDao = new MCashDao_Impl(this);\n" +
                "        }\n" +
                "        return _mCashDao;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";

        String fileName = "MyDisposableObserver";

        return fileContent;
    }


    private void FileWr(String pack, String fileName, String fileContent, RoundEnvironment roundEnvironment, Element it) {

        try {
            FileObject filerSourceFile = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                    pack, fileName + ".kt", it);

            OutputStream outputStream = filerSourceFile.openOutputStream();
            outputStream.write(fileContent.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FileWrJava(String pack, String fileName, String fileContent, RoundEnvironment roundEnvironment, Element it) {

        try {
            FileObject filerSourceFile = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                    pack, fileName + ".java", it);

            OutputStream outputStream = filerSourceFile.openOutputStream();
            outputStream.write(fileContent.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                WebApi.class.getCanonicalName(),
                BindView.class.getCanonicalName(),
                OnClick.class.getCanonicalName(),
                Keep.class.getCanonicalName()));
    }
}