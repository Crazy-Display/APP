package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;

public class ImageActivity extends AppCompatActivity {
    ArrayAdapter<Integer> adapter;
    WebSocketClient client = MyWebSocketClient.getInstance().getWebSocketClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        final Button im = findViewById(R.id.back);//image
        ArrayList<Integer> fotos = new ArrayList<Integer>();
        fotos.add(R.drawable.cascada);
        fotos.add(R.drawable.nieve);
        fotos.add(R.drawable.lago);
        fotos.add(R.drawable.paris);
        fotos.add(R.drawable.playa);
        ImageView i = new ImageView(this);
        adapter = new ArrayAdapter<Integer>( this, R.layout.list_item_view, fotos)
        {
            @Override
            public View getView(int pos, View convertView, ViewGroup container) {
                // getView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_item_view, container, false);
                }

                ImageView imageView = convertView.findViewById(R.id.imageView);
                int imageResource = fotos.get(pos);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResource);

                // Resize the bitmap to the desired size (e.g., 100x100)
                int targetWidth = 200;
                int targetHeight = 100;
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

                imageView.setImageBitmap(resizedBitmap);

                TextView textView = convertView.findViewById(R.id.nom);
                textView.setText(getResources().getResourceEntryName(imageResource).toUpperCase());
                // Afegim un click listener a la imatge
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                        // Convert the bitmap to Base64-encoded JPEG data
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();

                        String base64Image = Base64.getEncoder().encodeToString(bytes);

                        Intent intent = new Intent(ImageActivity.this, FullImageActivity.class);
                        intent.putExtra("image", base64Image);
                        intent.putExtra("name", getResources().getResourceEntryName(imageResource));
                        startActivity(intent);
                    }
                });

                return convertView;
            }
        };

        // busquem la ListView i li endollem el ArrayAdapter
        ListView lv = (ListView) findViewById(R.id.list_img);
        lv.setAdapter(adapter);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ImageActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}