package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listaDescripciones;
    ArrayList<Proposito> propositos;
    helpBdd h;
    Button btnpropositos, btndinero, btnbackup, btnrestaurar;

    Calendar calendar;

    int year;
    int month;
    int dayofmonth;
    int minuto;
    int hora;


    private int VALOR_RETORNO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAlarma();
        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);


        btnpropositos = findViewById(R.id.btnpropositos);
        btndinero = findViewById(R.id.btndinero);
        btnbackup= findViewById(R.id.btnbackup);
        btnrestaurar= findViewById(R.id.btnrestaurar);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);

        btnpropositos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            propositos();
            }
        });


        btndinero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dinero();
            }
        });


        btnbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });

        btnrestaurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
confirmarRestauracion();
            }
        });



    }

    private String horaActual(){
        //Formateo el hora obtenido: antepone el 0 si son menores de 10
        String horaFormateada =  (hora < 10)? String.valueOf("0" + hora) : String.valueOf(hora);
        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
        String minutoFormateado = (minuto < 10)? String.valueOf("0" + minuto):String.valueOf(minuto);
        return "hora_" + horaFormateada + "_" + minutoFormateado;

    }

    private String hoy(){
        return  dayofmonth + "_" + (month+1) + "_" + year;
    }

    private void backup(){

        String nombre_db = "bdd_proposito";
        String nombre_guardar = nombre_db + "_fecha_" + hoy() + ".sqlite";
        //Obtiene ruta de base de datos origen.
        String pathDB = getDatabasePath(nombre_db).toString();
        //Copia base de datos a destino definido.
        String destino = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + nombre_guardar;
        if(copiaBD(pathDB, destino)){
            Toast.makeText(getApplicationContext(), "Copia de seguridad guardada correctamente", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Error al realizar el backup", Toast.LENGTH_LONG).show();
        }
    }

    private void restaurar(String archivo){
        //Obtiene ruta de base de datos origen.
        String pathDB = getDatabasePath("bdd_proposito").toString();
        //Copia base de datos a destino definido.
        String destino = Environment.getExternalStorageDirectory().getPath() + "/" + archivo;
        String destino2 = Environment.getRootDirectory().getPath() + "/Android/data/" + "bdd_proposito.sqlite";
        Toast.makeText(getApplicationContext(), "Origen: " + destino, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Destino: " + destino2, Toast.LENGTH_LONG).show();
        if(copiaBD(destino, pathDB)){
            Toast.makeText(getApplicationContext(), "Restaurado correctamente", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void seleccionarArchivo(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione bdd"), VALOR_RETORNO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            //Cancelado por el usuario
        }
        if ((resultCode == RESULT_OK) && (requestCode == VALOR_RETORNO)) {
            //Procesar el resultado
            Uri uri = data.getData(); //obtener el uri content
            String ruta [] = uri.getPath().split(":");
            String archivo = ruta[1];
            restaurar(archivo);
        }
    }

    private void setAlarma(){
        Intent intent = new Intent(getApplicationContext(), Alarma.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, Alarma.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        int intervalMillis = 1 * 1 * 1000; //1 segundo
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
    }

    private void propositos() {
        Intent i = new Intent(MainActivity.this, MenuPropositos.class);
        startActivity(i);
    }


    private void dinero() {
        Intent i = new Intent(MainActivity.this, MenuDinero.class);
        startActivity(i);
    }


    private void checkExternalStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
           Toast.makeText(getApplicationContext(), "No tiene permisos", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmarRestauracion(){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton("Restaurar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccionarArchivo();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Confirmar")
                .setMessage("¿Desea restaurar la copia de seguridad?")
                .create();

        dialogo.show();

    }

    public boolean copiaBD(String from, String to) {
        boolean result = false;

        //Verifica permisos para Android 6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkExternalStoragePermission();
        }

        try{
            File dir = new File(to.substring(0, to.lastIndexOf('/')));
            dir.mkdirs();
            File tof = new File(dir, to.substring(to.lastIndexOf('/') + 1));
            int byteread;
            File oldfile = new File(from);
            if(oldfile.exists()){
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(tof);
                byte[] buffer = new byte[1024];
                while((byteread = inStream.read(buffer)) != -1){
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            result = true;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("copyFile", "Error copiando archivo: " + e.getMessage());
        }
        return result;
    }

}
