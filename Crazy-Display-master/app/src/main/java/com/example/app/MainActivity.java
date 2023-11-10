package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button ran = findViewById(R.id.mesages);//mensages
        EditText campoTexto = (EditText) findViewById(R.id.missatge);
        EditText ip = (EditText) findViewById(R.id.ip);
        try {
            URI uri = new URI("ws://192.168.18.45:8081");

            WebSocketClient webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i("INFO","Dentro");
                }

                @Override
                public void onMessage(String message) {
                    Log.i("INFO","mensage");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.i("INFO","Cerrado");
                }

                @Override
                public void onError(Exception ex) {

                    Log.i("INFO",ex.toString());
                }
            };


            final Button conn = findViewById(R.id.conectar);//conectar
            conn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    webSocketClient.connect();
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }




        ran.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (LlistaActivity.mis.contains(campoTexto.getText().toString())){
                    Log.i("INFO",campoTexto.getText().toString());
                }
                else {LlistaActivity.mis.add(campoTexto.getText().toString());

                }

                Intent intent = new Intent(MainActivity.this,LlistaActivity.class);
                startActivity(intent);

            }
        });



    }
}