package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

public class FullImageActivity extends AppCompatActivity {
    WebSocketClient client = MyWebSocketClient.getInstance().getWebSocketClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        final Button im = findViewById(R.id.button);
        final Button se = findViewById(R.id.send);
        // Obtener los datos de la imagen de la intención
        String base64Image = getIntent().getStringExtra("image");
        String nom = getIntent().getStringExtra("name");
        // Decodificar la imagen codificada en Base64 a un Bitmap
        byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // Establecer el Bitmap en la ImageView
        ImageView imageView = findViewById(R.id.full_image_view);
        imageView.setImageBitmap(bitmap);
        TextView textView = findViewById(R.id.name);
        textView.setText(nom.toUpperCase());

        se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject message = new JSONObject();
                try {
                    message.put("type", "image");
                    message.put("image", base64Image);

                    if (client != null && client.isOpen()) {

                        client.send(message.toString()); //envia al servidor el mensaje seleccionado

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullImageActivity.this, ImageActivity.class);
                startActivity(intent);

            }
        });


    }
}