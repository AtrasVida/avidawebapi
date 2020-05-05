package co.atrasvida.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.atrasvida.avidawebapi.example.R
import co.atrasvida.avidawebapi_annotations.GreetingGenerator
//import co.atrasvida.avidawebapi_example.ApiClient.Generated_NetworkApiService
import co.atrasvida.example.ApiClient.NetworkApiService

@GreetingGenerator
class MainActivity : AppCompatActivity() {

   //@BindView(R.id.tv_content)
   //var tvContent: TextView? = null

    @GreetingGenerator
    class Santa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // Binding.bind(this)
        ///Generated_NetworkApiService().getAd(0,0){}

        var clazz= NetworkApiService::class



    }

  // @OnClick(R.id.tv_content)
  // fun bt1Click(v: View?) {
  //     tvContent!!.text = "Button 1 Clicked"
  // }
}
