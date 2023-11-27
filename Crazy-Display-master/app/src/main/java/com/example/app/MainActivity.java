package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private boolean conectado;
    private Bundle outState;

    private ArrayAdapter<String> adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int CONNECTION_TIMEOUT = 10000;
    private WebSocketClient client;
    MyWebSocketClient webSocket = MyWebSocketClient.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayList<String> users = new ArrayList<>();


        final Button clients = findViewById(R.id.cli);//clients
        final Button ran = findViewById(R.id.mesages);//mensages
        final Button im = findViewById(R.id.img);//image


        EditText campoTexto = (EditText) findViewById(R.id.missatge);
        EditText ip = (EditText) findViewById(R.id.ip);

        if (!conectado){
            ran.setEnabled(false);//solo si esta conectado
            toolbar.setVisibility(View.INVISIBLE);
            clients.setEnabled(false);
            clients.setVisibility(View.INVISIBLE);
            im.setEnabled(false);
        }
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
            final Button en = findViewById(R.id.enviar);
            final Button conn = findViewById(R.id.conectar);//conectar
            conn.setBackgroundColor(Color.GREEN);
            conn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ur="ws://"+ ip.getText().toString()+":8888";
                    Log.i("INFO", ur);

                    if (!webSocket.getWebSocketClient().isOpen()){
                    try {
                        URI uri = new URI(ur);
                         client = new WebSocketClient(uri, (Draft) new Draft_6455()) {
                            @Override
                            public void onOpen(ServerHandshake handshakedata) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                ran.setEnabled(true);//solo si esta conectado
                                toolbar.setVisibility(View.VISIBLE);
                                clients.setEnabled(true);
                                clients.setVisibility(View.VISIBLE);
                                im.setEnabled(true);
                                    }
                                });
                            }
                            @Override
                            public void onMessage(String message) {

                                if (message.equals("Connected")){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            conn.setText("Disconect");
                                            conn.setBackgroundColor(Color.RED);
                                            conectado=true;
                                            ran.setEnabled(true);
                                        }
                                    });
                                }

                                if (message.equals("Connected")){
                                    Toast.makeText(MainActivity.this, "Connexion", Toast.LENGTH_SHORT).show();
                                };
                                if (message.equals("Mensaje")){
                                    Toast.makeText(MainActivity.this, "Connexion", Toast.LENGTH_SHORT).show();

                                };

                                try {
                                    JSONObject obj_json = new JSONObject(message);
                                    if (obj_json.getString("type").equals("users")){

                                        JSONArray arrayListflu = obj_json.getJSONArray("usersFlutter");
                                        JSONArray arrayListapp = obj_json.getJSONArray("usersApp");
                                        for (int i = 0; i < arrayListflu.length(); i++) {
                                            users.add(arrayListflu.get(i).toString());
                                        }
                                        for (int i = 0; i < arrayListapp.length(); i++) {
                                            users.add(arrayListapp.get(i).toString());
                                        }
                                    }


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                            @Override
                            public void onClose(int code, String reason, boolean remote) {
                                Log.i("INFO", "Connection closed: " + code + " - " + reason);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                conn.setText("Connect");
                                conn.setBackgroundColor(Color.GREEN);
                                conectado=false;

                                ran.setEnabled(false);//solo si esta conectado
                                        toolbar.setVisibility(View.INVISIBLE);
                                        clients.setEnabled(false);
                                        clients.setVisibility(View.INVISIBLE);
                                        im.setEnabled(false);
                                    }
                                });
                            }
                            @Override
                            public void onError(Exception ex) {
                                Log.e("ERROR", "WebSocket connection error: ", ex);
                            }
                        };
                         MyWebSocketClient.getInstance().setWebSocketClient(client);
                        client.connect();

                        if (true){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Iniciar sesión");
                            builder.setMessage("Introduzca su nombre de usuario y contraseña");

                            // Añadir los campos de usuario y contraseña
                            LinearLayout layout = new LinearLayout(MainActivity.this);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            final EditText usernameEditText = new EditText(MainActivity.this);
                            usernameEditText.setHint("Nombre de usuario");
                            layout.addView(usernameEditText);

                            final EditText passwordEditText = new EditText(MainActivity.this);
                            passwordEditText.setHint("Contraseña");
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            layout.addView(passwordEditText);
                            builder.setView(layout);

                            // Añadir los botones de aceptar y cancelar
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Obtener los valores de los campos de usuario y contraseña
                                    String username = usernameEditText.getText().toString();
                                    String password = passwordEditText.getText().toString();

                                    JSONObject message = new JSONObject();
                                    try {
                                        message.put("type", "verify");
                                        message.put("username", username);
                                        message.put("password", password);
                                        message.put("from", "App");

                                        if (client != null && client.isOpen()) {
                                            Log.i("INFO", "Mensaje: " +String.valueOf(message));
                                            client.send(String.valueOf(message));
                                        }

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            });
                            builder.setNegativeButton("Cancelar", null);

                            builder.show();
                        }

                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }else{

                        conn.setText("Connect");
                        conn.setBackgroundColor(Color.GREEN);
                    }
                }
            });

        String[] array = new String[users.size()];
        users.toArray(array);
        clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject requestedUsers = new JSONObject();

                try {
                    requestedUsers.put("type", "users");
                    client.send(String.valueOf(requestedUsers));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("CLIENTES");

                builder.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
           }
        });

            en.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // Leer los mensajes existentes del archivo
                        FileInputStream fileInputStream = openFileInput("mensages.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

                        // Vaciar el array 'mis' antes de agregar nuevos mensajes
                        LlistaActivity.mis.clear();

                        while (reader.ready()) {
                            String line = reader.readLine();
                            Log.i("INFO", line);
                            if (!LlistaActivity.mis.contains(line) && !line.equals(" ")) {
                                LlistaActivity.mis.add(line);
                            }
                        }
                        reader.close();
                    } catch (FileNotFoundException e) {
                        // Crear el archivo si no existe
                        try {
                            FileOutputStream fileOutputStream = openFileOutput("mensages.txt", Context.MODE_PRIVATE);
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //enviar el mensage al servidor
                    String mensaje = campoTexto.getText().toString();
                    if (client != null && client.isOpen()) {
                        try {
                            JSONObject message = new JSONObject();
                                message.put("type", "texto");
                                message.put("texto", mensaje);

                            client.send(message.toString());
                            Log.i("INFO", "Mensaje enviado: " + mensaje);
                        } catch (Exception e) {
                            Log.e("ERROR", "Error al enviar el mensaje: ", e);
                        }
                        }
                    // Agregar el nuevo mensaje al array
                    String newMessage = campoTexto.getText().toString();
                    newMessage = newMessage.replaceAll("\\s+", "");
                    if (!LlistaActivity.mis.contains(newMessage)) {
                        LlistaActivity.mis.add(newMessage);
                        Log.i("INFO", LlistaActivity.mis.toString());
                    }
                    // Guardar el array actualizado en el archivo

                    Context context = getApplicationContext();
                    FileOutputStream fos = null;

                    try {
                        fos = context.openFileOutput("mensages.txt", Context.MODE_APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (String message : LlistaActivity.mis) {
                        try {

                            fos.write((message + "\n").getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        ran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Leer los mensajes existentes del archivo
                    FileInputStream fileInputStream = openFileInput("mensages.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

                    // Vaciar el array 'mis' antes de agregar nuevos mensajes
                    LlistaActivity.mis.clear();

                    while (reader.ready()) {
                        String line = reader.readLine();
                        Log.i("INFO", line);
                        if (!LlistaActivity.mis.contains(line) && !line.equals(" ")) {
                            LlistaActivity.mis.add(line);
                        }
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    // Crear el archivo si no existe
                    try {
                        FileOutputStream fileOutputStream = openFileOutput("mensages.txt", Context.MODE_PRIVATE);
                        fileOutputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this, LlistaActivity.class);

                startActivity(intent);

            }
        });

    }


}