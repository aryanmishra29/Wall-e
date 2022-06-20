package com.example.wall_e;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserInterface extends AppCompatActivity {
    TextView name;
    double bal=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        Intent i=getIntent();
        name=findViewById(R.id.tv_welcome);
        String s=i.getExtras().getString("name");
        name.setText("Welcome "+s+"!");
    }
}