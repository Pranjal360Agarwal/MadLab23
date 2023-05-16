package com.example.imageviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    boolean isBoy=true;
    public void change(View view){
        ImageView imageView=findViewById(R.id.Boy);
        if(isBoy){
            imageView.setImageResource(R.drawable.girl);
            isBoy=false;
        }
        else{
            imageView.setImageResource(R.drawable.boy);
            isBoy=true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}