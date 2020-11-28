package com.example.enviardatosdeuncontactoporsms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EnviarMensajeTexto extends AppCompatActivity {

    private TextView tvMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensaje_texto);

        tvMensaje = findViewById(R.id.etm_texto_enviar);
    }

    public void enviarMensaje(View view){
        String mensaje = tvMensaje.getText().toString();
        enviarTexto(mensaje);
    }

    public void enviarTexto(String textMessage){
        Intent IntentEnviarTexto = new Intent(Intent.ACTION_SEND);
        IntentEnviarTexto.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        IntentEnviarTexto.putExtra("sms_body", textMessage);
        IntentEnviarTexto.setType("text/plain");
        if (IntentEnviarTexto.resolveActivity(getPackageManager()) != null) {
            startActivity(IntentEnviarTexto);
        }else{
            Toast.makeText(this,"Se ha producido un error",Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"si sigue fallando revise los permisos e intento de nuevo",Toast.LENGTH_LONG).show();
        }
        finish();
    }
}