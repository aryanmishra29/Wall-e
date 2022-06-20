package com.example.wall_e;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    EditText name;
    Button cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.et_name);
        cont=findViewById(R.id.b_cont);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i=new Intent(MainActivity.this,UserInterface.class);
                i.putExtra("name",name.getText().toString());
                startActivity(i);
            }
        });
    }
}