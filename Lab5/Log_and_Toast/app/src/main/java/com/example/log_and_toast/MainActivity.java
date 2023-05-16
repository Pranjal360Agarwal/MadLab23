package com.example.log_and_toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
//import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.toasting_Button);
        btn.setOnClickListener(v -> {
            int duration=Toast.LENGTH_LONG;
            Log.i("Calling Log","Called It");
            Toast toast=Toast.makeText(getApplicationContext(),"You clicked The Button",duration);
            toast.show();
        });
    }
}