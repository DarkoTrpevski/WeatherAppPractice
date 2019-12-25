package com.darko.weatherapppractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeCityActivity extends AppCompatActivity {

    EditText editTextCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);

        editTextCity = findViewById(R.id.et_change_city);
        ImageButton backButton = findViewById(R.id.backButton);

        //*When back image button is pressed just finish this activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //*When clicked go back to the MainActivity and finish this activity
        editTextCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String newCity = editTextCity.getText().toString();
                Intent newCityIntent = new Intent(ChangeCityActivity.this, MainActivity.class);
                newCityIntent.putExtra("city", newCity);
                startActivity(newCityIntent);
                finish();
                return false;
            }
        });

    }
}