package com.example.enviardatosdeuncontactoporsms;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * Variables int que determinan cada una de las acciones que se pueden realizar en el
     * metodo onActivityResult
     */
    static final int CONTS_MOSTRAR_CONTACTO = 1;
    static final int CONTS_ENVIAR_CONTACTO = 2;
    static final int CONTS_LLAMAR_CONTACTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Cuatro metodos para los cuatro RelativeLayout
     * Los tres primeros metodos que instancian el metodo seleccionarContacto
     * pasandole cada uno de ellos un número que luego se pasara al metodo startActivityForResult
     * que iniciará la actividad indicada por el numero pasado en el metodo seleccionarcontacto
     * en cada uno de los metodos
     *
     * El cuarto metodo se inicia al pulsar el layout de enviar un mensaje
     * este metodo inicia otro activity, el activity EnniarMensajeTexto.java
     * @param view
     */
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

    /**
     * Metodo que recibe como parametro un int que indicara que acción se desea realizar
     * @param accion
     */
    public void seleccionarContacto(int accion) {
        Intent intentSeleccionarContacto = new Intent(Intent.ACTION_PICK);
        if(accion==1){
            intentSeleccionarContacto.setType(ContactsContract.Contacts.CONTENT_TYPE);
        }else {
            intentSeleccionarContacto.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        }
        startActivityForResult(intentSeleccionarContacto, accion);
    }

    /**
     * Metodo sobreescrito onActivityResult
     * En el metodo lo primero se comprueba que el resultCode sea igual a la variable RESULT_OK
     * Después se instancia un Uri que recibe los datos del intent y cursor
     * A continuación con un bucle switch se seleciona cual es la accion mediante el requestCode
     *
     * En el primer caso activa el metodo verContacto al que se le pasa el  uri con los datos del contacto
     *
     * En el segundo caso se le pasa al cursor los datos del contacto mediante el metodo getContentResolver
     * yel metodo query al que se le pasa el Uri contacUri instanciado con los datos del contacto
     * Se selecionan las columnas que quieres y en caso de que el cursor tenga más de un valor y con un
     * getString se asignan se asignan los valores a un string para construir el mensaje a enviar en otro String
     * que luego será pasado al metodo enviartexto para enviarlo como mensaje
     *
     * En el tercar caso se vuelve pasar al cursor el conctact Uri para sacar el numero de la misma forma que
     * en el caso anterior solo que al final el String con el número se le posa al metodo llamarnumero
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri contactUri = data.getData();
            Cursor cursor;
            switch (requestCode){
                case CONTS_MOSTRAR_CONTACTO:
                    verContacto(contactUri);
                break;
                case CONTS_ENVIAR_CONTACTO:
                    cursor = getContentResolver().query(contactUri, null,null, null, null);
                    // If the cursor returned is valid, get the phone number
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        String nombre = cursor.getString(columnaNombre);
                        String numero = cursor.getString(columnaNumero);
                        String DatosDeContacto = "Nombre: " + nombre + "\nNúmero de telefono: "+ numero;
                        enviarTexto(DatosDeContacto);
                    }
                break;
                case CONTS_LLAMAR_CONTACTO:
                    cursor = getContentResolver().query(contactUri, null,null, null, null);
                    if (cursor != null && cursor.moveToFirst()){
                        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String numero = cursor.getString(columnaNumero);
                        llamarnumero(numero);
                    }
                break;
                default:
                    Toast.makeText(this,"Se aproducido un error", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Se aproducido un error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para abrir una apliacion de mensajes
     * El mensaje que se envia es el String introducido en el parametro
     * @param textMessage
     */
    public void enviarTexto(String textMessage){
        Intent IntentEnviarTexto = new Intent(Intent.ACTION_SEND);
        IntentEnviarTexto.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        IntentEnviarTexto.putExtra("sms_body", textMessage);
        IntentEnviarTexto.setType("text/plain");
        if (IntentEnviarTexto.resolveActivity(getPackageManager()) != null) {
            startActivity(IntentEnviarTexto);
        }
    }

    /**
     * Metodo que recibe un uri con los datos de contacto
     * Este uri se lo pasa a un intent que al pasarlo como parametro al metodo startActivity
     * abre la applicación de contactos y te muestra el contacto pasado por el uri
     * @param contactUri
     */
    public void verContacto(Uri contactUri) {
        Intent intentVerContacto = new Intent(Intent.ACTION_VIEW, contactUri);
        startActivity(intentVerContacto);
    }

    /**
     * Metodo que recibe como parametro un String que debe ser un número de telefono
     * Después se instancia un intent que va abrir la aplicacion de llamada
     * Se pasa el String numero de telefono al intent y se inicia el mismo
     * @param numeroTelefono
     */
    public void llamarnumero(String numeroTelefono) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + numeroTelefono));
        startActivity(intent);
    }
}