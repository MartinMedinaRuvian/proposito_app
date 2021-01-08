package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

public class agregar extends AppCompatActivity {

    Button btnguardar,btncambiarFecha, btncambiarHora;
    TextView lblfecha, lblhora;
    EditText txttitulo;
    MultiAutoCompleteTextView txtdescripcion;
    DatePickerDialog date;
    ImageView imgmenu;


    Random aleatorio = new Random(System.currentTimeMillis());
    int idNotificacion = aleatorio.nextInt(100);



    Spinner cmb, cmbrepetir;

    Calendar calendar;

    int year;
    int month;
    int dayofmonth;
    int minuto;
    int hora;





    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);



        btnguardar=findViewById(R.id.btnguardar);
        btncambiarFecha=findViewById(R.id.btncambiarFecha);
        btncambiarHora=findViewById(R.id.btncambiarHora);
        btnguardar=findViewById(R.id.btnguardar);
        txttitulo=findViewById(R.id.txtdescripcion);
        txtdescripcion=findViewById(R.id.txtdescripcion);
        lblfecha=findViewById(R.id.lblfecha);
        lblhora=findViewById(R.id.lblhora);
        cmb=findViewById(R.id.cmbtitulo);
        cmbrepetir=findViewById(R.id.cmbrepetir);
        imgmenu=findViewById(R.id.imgmenu);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);

        ArrayAdapter<CharSequence> a=ArrayAdapter.createFromResource(this, R.array.opcionesSpinner, R.layout.texto_spinner);
        ArrayAdapter<CharSequence> b=ArrayAdapter.createFromResource(this, R.array.opcionesRepetir, R.layout.texto_spinner);
        cmb.setAdapter(a);
        cmbrepetir.setAdapter(b);



        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        lblfecha.setText(dayofmonth + "/" + (month+1) + "/" + year);

        //Formateo el hora obtenido: antepone el 0 si son menores de 10
        String horaFormateada =  (hora < 10)? String.valueOf(CERO + hora) : String.valueOf(hora);
        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
        String minutoFormateado = (minuto < 10)? String.valueOf(CERO + minuto):String.valueOf(minuto);


        lblhora.setText(horaFormateada + ":" + minutoFormateado);

        btncambiarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cambiarFecha();

            }
        });

        lblfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFecha();
            }
        });

        btncambiarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerHora();
            }
        });

        lblhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerHora();
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuPropositos.class);
                startActivity(intent);
            }
        });

//fin de constructor principal
    }

    private void cambiarFecha(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        date = new DatePickerDialog(agregar.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                lblfecha.setText(day + "/" + (month+1) + "/" + year);

            }
        }, year, month, dayofmonth);
        date.show();
    }

    private void guardar(){
        helpBdd h = new helpBdd(this, "bdd_proposito", null, 1);
        SQLiteDatabase db = h.getWritableDatabase();

        if (txtdescripcion.getText().length() == 0){
            Toast.makeText(agregar.this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
        }else{


            int sele = cmb.getSelectedItemPosition();
            int seleRepetir = cmbrepetir.getSelectedItemPosition();


            String date = lblfecha.getText().toString();
            String horaGuardar = lblhora.getText().toString();

            String hoy = dayofmonth + "/" + (month+1) + "/" + year;
            String horaActual = hora + ":" + minuto;


            String titulo = cmb.getItemAtPosition(sele).toString();
            String descripcion = txtdescripcion.getText().toString();

            ContentValues c = new ContentValues();
            c.put(sql.noTitulo, titulo);
            c.put(sql.noDescripcion, descripcion);
            c.put(sql.noFecha, date);
            c.put(sql.noHora, horaGuardar);
            c.put(sql.noEstado, 0);
            c.put(sql.noRepetir, seleRepetir);

            db.insert(sql.tabla,null,c);
            db.close();

//setAlarma();

            Toast.makeText(agregar.this, "Guardado correctamente.", Toast.LENGTH_LONG).show();

            abrirMenu();

        }

    }



    private void abrirMenu(){
        Intent i = new Intent(agregar.this, MenuPropositos.class);
        startActivity(i);
    }



    private void obtenerHora(){

        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);

                //Muestro la hora con el formato deseado
                lblhora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }


    private void alarma(String hora, String titulo, String descripcion, int idNotificacion){

        Calendar calendar = Calendar.getInstance();
        String h [] = hora.split(":");

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(h[0]),
                Integer.parseInt(h[1]),
                0
        );

        // setAlarma2(calendar.getTimeInMillis(), titulo, descripcion, idNotificacion);
    }

    private void setAlarma2(long timeInMillis, String titulo, String descripcion, int idNotificacion) {

        String colorNotificacion = String.valueOf(Color.BLACK);

        if (cmb.getSelectedItemPosition() == 1){
            colorNotificacion = String.valueOf(Color.BLUE);
        }else if (cmb.getSelectedItemPosition() ==2){
            colorNotificacion = String.valueOf(Color.GRAY);
        }else if (cmb.getSelectedItemPosition() ==3){
            colorNotificacion = String.valueOf(Color.RED);
        }

        Intent intent = new Intent(this, Alarma.class);
        intent.putExtra("titulo", titulo + "\n" + "\n");
        intent.putExtra("descripcion", descripcion);
        intent.putExtra("color", colorNotificacion);



        PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(), idNotificacion, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }


    private void setAlarma(){
        Intent intent = new Intent(getApplicationContext(), Alarma.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, Alarma.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        int intervalMillis = 1 * 1 * 1000; //1 segundo
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
    }


}
