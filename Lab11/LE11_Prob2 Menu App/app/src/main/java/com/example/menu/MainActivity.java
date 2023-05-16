package com.example.menu;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    SharedPreferences sharedPreferences;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.eng:
                textView.setText("English");
                sharedPreferences.edit().putString("tittle","English").apply();
                return true;
            case R.id.hin:
                textView.setText("Hindi");
                sharedPreferences.edit().putString("tittle","Hindi").apply();
                return true;
            case R.id.san:
                textView.setText("Sanskrit");
                sharedPreferences.edit().putString("tittle","Sanskrit").apply();
                return true;
            default:
                return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        sharedPreferences=this.getSharedPreferences("com.example.myapplication",0);
        String pref=sharedPreferences.getString("tittle","default");
        textView.setText(pref);
    }
}
