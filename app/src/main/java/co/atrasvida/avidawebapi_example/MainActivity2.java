package co.atrasvida.avidawebapi_example;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import co.atrasvida.avidawebapi_annotations.BindView;

import co.atrasvida.avidawebapi_annotations.BindView;
import co.atrasvida.avidawebapi_annotations.OnClick;
import co.atrasvida.avidawebapi_annotations.WebApi;

public class MainActivity2 extends AppCompatActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Binding.bind(this);

    }

    @OnClick(R.id.tv_content)
    void bt1Click(View v) {
        tvContent.setText("Button 1 Clicked");
    }

    //@OnClick(R.id.bt_2)
    //void bt2Click(View v) {
    //    tvContent.setText("Button 2 Clicked");
   // }
}
