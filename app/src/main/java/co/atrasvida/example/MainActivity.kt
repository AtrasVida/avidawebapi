package co.atrasvida.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.atrasvida.avidawebapi.example.R
import co.atrasvida.example.apiClient.ApiService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    var myDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ApiService().getCarBrands() {
            print(it.toString())
        }.fromCash("as") {
            print(it.toString())

        }.onError {
            print(it.toString())

        }.addToDisposable(myDisposable)

    }
}