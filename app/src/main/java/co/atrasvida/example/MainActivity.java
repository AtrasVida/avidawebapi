package co.atrasvida.example;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import co.atrasvida.avidawebapi.example.R;


class MainActivity extends AppCompatActivity {

    //@BindView(R.id.tv_content)
    //var tvContent: TextView? = null


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Binding.bind(this)
        ///Generated_NetworkApiService().getAd(0,0){}


    }

    // @OnClick(R.id.tv_content)
    // fun bt1Click(v: View?) {
    //     tvContent!!.text = "Button 1 Clicked"
    // }
}
