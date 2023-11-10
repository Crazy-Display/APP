package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LlistaActivity extends AppCompatActivity {
    private ListView myListview;
    static ArrayList<String> mis = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista2);


        myListview=findViewById(R.id.list);
        adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mis);
        myListview.setAdapter(adapter);


        final Button ran = findViewById(R.id.retorn);
        ran.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LlistaActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }
}