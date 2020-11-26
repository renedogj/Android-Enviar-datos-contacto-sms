package com.example.enviardatosdeuncontactoporsms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intentSeleccionarContacto = new Intent(Intent.ACTION_PICK);
        intentSeleccionarContacto.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        //if (intent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(intentSeleccionarContacto, REQUEST_SELECT_PHONE_NUMBER);
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
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
                enviarTexto(numero);
            }
        }
    }

    public void enviarTexto(String textMessage){
        Intent IntentEnviarTexto = new Intent(Intent.ACTION_SEND);
        IntentEnviarTexto.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        IntentEnviarTexto.putExtra("sms_body", textMessage);
        IntentEnviarTexto.setType("text/plain");
        //IntentEnviarTexto.putExtra(Intent.EXTRA_TEXT, textMessage);
        if (IntentEnviarTexto.resolveActivity(getPackageManager()) != null) {
            startActivity(IntentEnviarTexto);
        }
    }
}