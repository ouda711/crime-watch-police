package com.example.crimewatchpolice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class BottomActivity extends AppCompatActivity {
    TextView title, location, description;
    Button accept, reject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
//        title = (TextView)findViewById(R.id.textCrimeTitle);
//        location = (TextView)findViewById(R.id.textCrimeLocation);
//        description = (TextView)findViewById(R.id.textCrimeDescription);
//        Intent callingIntent = getIntent();
//        String crimeTitle = callingIntent.getExtras().getString("crimeTitle");
//        String crimeLocation = callingIntent.getExtras().getString("crimeDescription");
//        String crimeDescription = callingIntent.getExtras().getString("crimeDescription");
//
//        title.append(callingIntent.getExtras().getString("crimeTitle"));
//        location.append(callingIntent.getExtras().getString("crimeLocation"));
//        description.append(callingIntent.getExtras().getString("crimeDescription"));
    }
}