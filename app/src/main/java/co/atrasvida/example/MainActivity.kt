package co.atrasvida.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.atrasvida.avidawebapi.example.R
import co.atrasvida.example.apiClient.ApiServiceX

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApiServiceX(). getCarBrands(1){
                print(it.toString())
        }
    }
}