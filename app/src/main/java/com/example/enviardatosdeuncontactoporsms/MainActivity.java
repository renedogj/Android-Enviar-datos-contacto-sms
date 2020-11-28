package com.example.enviardatosdeuncontactoporsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final int CONTS_MOSTRAR_CONTACTO = 1;
    static final int CONTS_ENVIAR_CONTACTO = 2;
    static final int CONTS_LLAMAR_CONTACTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void selecionarMostrarContacto(View view){
        seleccionarContacto(1);
    }

    public void selecionarEnviarContacto(View view){
        seleccionarContacto(2);
    }

    public void selecionarLlamarContacto(View view){
        seleccionarContacto(3);
    }

    public void irIntentenviarMensaje(View view){
        Intent intentActivityEnviarMensajeTexto = new Intent(this,EnviarMensajeTexto.class);
        startActivity(intentActivityEnviarMensajeTexto);
    }

    public void seleccionarContacto(int accion) {
        Intent intentSeleccionarContacto = new Intent(Intent.ACTION_PICK);
        if(accion==1){
            intentSeleccionarContacto.setType(ContactsContract.Contacts.CONTENT_TYPE);
        }else {
            intentSeleccionarContacto.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        }
        startActivityForResult(intentSeleccionarContacto, accion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTS_MOSTRAR_CONTACTO && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            verContacto(contactUri);
        }
        if (requestCode == CONTS_ENVIAR_CONTACTO && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null,null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String nombre = cursor.getString(columnaNombre);
                String numero = cursor.getString(columnaNumero);
                Toast.makeText(this,nombre,Toast.LENGTH_SHORT).show();
                Toast.makeText(this,numero,Toast.LENGTH_SHORT).show();
                String DatosDeContacto = "Nombre: " + nombre + "\nNúmero de telefono: "+ numero;
                enviarTexto(DatosDeContacto);
            }
        }
        if (requestCode == CONTS_LLAMAR_CONTACTO && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null,null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(columnaNumero);
                llarmarnumero(numero);
            }
        }
    }

    public void enviarTexto(String textMessage){
        Intent IntentEnviarTexto = new Intent(Intent.ACTION_SEND);
        IntentEnviarTexto.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        IntentEnviarTexto.putExtra("sms_body", textMessage);
        IntentEnviarTexto.setType("text/plain");
        if (IntentEnviarTexto.resolveActivity(getPackageManager()) != null) {
            startActivity(IntentEnviarTexto);
        }
    }

    public void verContacto(Uri contactUri) {
        Intent intentVerContacto = new Intent(Intent.ACTION_VIEW, contactUri);
        startActivity(intentVerContacto);
    }

    public void llarmarnumero(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}