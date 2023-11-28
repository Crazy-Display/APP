package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LlistaActivity extends AppCompatActivity {
    private ListView myListview;
    static ArrayList<String> mis = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    WebSocketClient client = MyWebSocketClient.getInstance().getWebSocketClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista2);



        myListview=findViewById(R.id.list_img);
        adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mis);
        myListview.setAdapter(adapter);


        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = mis.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(LlistaActivity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Desea enviar el mensaje?");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject message = new JSONObject();
                        try {
                            message.put("type", "texto");
                            message.put("texto", value);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        //envia al servidor el mensaje seleccionado
                        if (client != null && client.getConnection().isOpen()) {
                            Log.i("INFO", "Mensaje enviado: " + client.isOpen());
                            client.send(message.toString());
                            }
                        Toast.makeText(LlistaActivity.this, value, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada
                    }
                });

                builder.show();
            }
        });

        final Button ran = findViewById(R.id.retorn);
        ran.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                client.close();
                Intent intent = new Intent(LlistaActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }

}