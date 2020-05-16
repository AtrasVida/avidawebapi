package co.atrasvida.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.atrasvida.avidawebapi.example.R
import co.atrasvida.example.apiClient.ApiService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApiService(). getCarBrands(){
                print(it.toString())
        }.fromCash("as"){
            print(it.toString())

        }.onError {
            print(it.toString())

        }
    }
}