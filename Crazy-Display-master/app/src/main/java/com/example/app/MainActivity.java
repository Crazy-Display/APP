package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private boolean conectado;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int CONNECTION_TIMEOUT = 10000; // 5 segundos
    public static WebSocketClient webSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button ran = findViewById(R.id.mesages);//mensages
        final Button im = findViewById(R.id.img);//image
        EditText campoTexto = (EditText) findViewById(R.id.missatge);
        EditText ip = (EditText) findViewById(R.id.ip);
        if (!conectado){
        ran.setEnabled(false);//solo si esta conectado
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
                    if (!conectado){
                    try {
                        URI uri = new URI(ur);
                         webSocketClient = new WebSocketClient(uri) {
                            @Override
                            public void onOpen(ServerHandshake handshakedata) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        conn.setText("Disconect");
                                        conn.setBackgroundColor(Color.RED);
                                        conectado=true;
                                        ran.setEnabled(true);
                                    }
                                });
                                webSocketClient.send("app");
                            }
                            @Override
                            public void onMessage(String message) {
                                Log.i("INFO", "Received message: " + message);
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
                                ran.setEnabled(false);
                                    }
                                });
                            }
                            @Override
                            public void onError(Exception ex) {
                                Log.e("ERROR", "WebSocket connection error: ", ex);
                            }
                        };
                        webSocketClient.connect();
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
                                        message.put("username", username);
                                        message.put("password", password);
                                        // MainActivity.webSocketClient.send(message.toString());
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }


                                    // Realizar la acción correspondiente
                                    // ...
                                }
                            });
                            builder.setNegativeButton("Cancelar", null);

                            builder.show();
                        }

                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                        webSocketClient.close();
                        conn.setText("Connect");
                        conn.setBackgroundColor(Color.GREEN);
                    }
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
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        try {
                            JSONObject message = new JSONObject();
                                message.put("type", "app");
                                message.put("texto", mensaje);

                            //MainActivity.webSocketClient.send(message.toString());
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