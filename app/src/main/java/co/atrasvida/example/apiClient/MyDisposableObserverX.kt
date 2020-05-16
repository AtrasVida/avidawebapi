
package co.atrasvida.example.apiClient
import android.util.Log
import com.google.gson.GsonBuilder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import retrofit2.HttpException

/**
 * Created by AvidA
 */
abstract class MyDisposableObserverX<T>(var onSuccess: (T) -> Unit) : DisposableObserver<T>() {

    override fun onNext(t: T) {
        onSuccess(t)
    }


    override fun onError(throwable: Throwable) {
        if (::mThrowable.isInitialized)
            mThrowable(throwable)


        Log.e(
            TAG,
            "onError() called with: throwable = [$throwable] " 
        )
        Log.e("", GsonBuilder().setPrettyPrinting().create().toJson(throwable))
        val throwableClass: Class<*> = throwable.javaClass
        if (HttpException::class.java.isAssignableFrom(throwableClass)) {
            Log.e(
                TAG,
                "There is an error in network call"
            )
        }
    }

    override fun onComplete() {}


    lateinit private var mThrowable: (Throwable) -> Unit
    fun onError(throwable: (Throwable) -> Unit): MyDisposableObserverX<T> {
        mThrowable = throwable
        return this
    }


    fun addToDisposable(function: CompositeDisposable) {
        function.add(this)
    }

    companion object {
        private const val TAG = "MyDisposableObserver"
    }}
