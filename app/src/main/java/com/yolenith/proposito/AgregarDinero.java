package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

public class AgregarDinero extends AppCompatActivity {

    Button btnguardar,btncambiarFecha, btncambiarHora;
    TextView lblfecha, lblhora;
    EditText txttitulo, txtvalor;
    MultiAutoCompleteTextView txtdescripcion;
    DatePickerDialog date;
    ImageView imgmenu;
    CheckBox chkfijo;


    Spinner cmb, cmbtipo;

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
        setContentView(R.layout.activity_agregar_dinero);



        btnguardar=findViewById(R.id.btnguardar);
        btncambiarFecha=findViewById(R.id.btncambiarFecha);
        btncambiarHora=findViewById(R.id.btncambiarHora);
        btnguardar=findViewById(R.id.btnguardar);
        txttitulo=findViewById(R.id.txtdescripcion);
        txtvalor=findViewById(R.id.txtvalor);
        txtdescripcion=findViewById(R.id.txtdescripcion);
        lblfecha=findViewById(R.id.lblfecha);
        lblhora=findViewById(R.id.lblhora);
        cmb=findViewById(R.id.cmbtitulo);
        cmbtipo=findViewById(R.id.cmbtipo);
        imgmenu=findViewById(R.id.imgmenu);
        chkfijo=findViewById(R.id.chkfijo);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);

        ArrayAdapter<CharSequence> a=ArrayAdapter.createFromResource(this, R.array.opcionesSpinner, R.layout.texto_spinner);
        ArrayAdapter<CharSequence> b=ArrayAdapter.createFromResource(this, R.array.opcionesDinero, R.layout.texto_spinner);
        cmb.setAdapter(a);
        cmbtipo.setAdapter(b);



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
                Intent intent = new Intent(getApplicationContext(), MenuDinero.class);
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

        date = new DatePickerDialog(AgregarDinero.this, new DatePickerDialog.OnDateSetListener() {
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
            Toast.makeText(getApplicationContext(), "Ingrese todos los datos", Toast.LENGTH_LONG).show();
        }else{


            int sele = cmb.getSelectedItemPosition();
            int tipo = cmbtipo.getSelectedItemPosition();
            int fijo = 0;

            if (chkfijo.isChecked()){
                fijo = 1;
            }


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
            c.put(sql.noTipo, tipo);
            c.put(sql.noRepetir, fijo);
            c.put(sql.noValor, Float.parseFloat(txtvalor.getText().toString()));

            db.insert(sql.tabla3,null,c);
            db.close();

//setAlarma();

            Toast.makeText(getApplicationContext(), "Guardado correctamente.", Toast.LENGTH_LONG).show();

            abrirMenu();

        }

    }



    private void abrirMenu(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
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

}
