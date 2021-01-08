package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class cambios extends AppCompatActivity {

    ImageView imgestado, imgmenu;
    TextView lblfecha, lblhora, lbldescripcion;
    Button btnupdate, btnupdateFechaHora, btncambiarFecha, btncambiarHora;

    private int id;
    private int estado;
    private String fechaProposito;
    private String horaProposito;


    DatePickerDialog date;
    Calendar calendar;

    int year;
    int month;
    int dayofmonth;
    int minuto;
    int hora;




    private int repetir;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    helpBdd h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambios);



        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);

        imgestado=findViewById(R.id.imgestado);
        imgmenu=findViewById(R.id.imgmenu);
        lblfecha=findViewById(R.id.lblfecha);
        lblhora=findViewById(R.id.lblhora);
        lblhora=findViewById(R.id.lblhora);
        lbldescripcion=findViewById(R.id.lbldescripcion);
        btncambiarFecha=findViewById(R.id.btncambiarFecha);
        btncambiarHora=findViewById(R.id.btncambiarHora);

        btnupdate=findViewById(R.id.btnupdate);
        btnupdateFechaHora=findViewById(R.id.btnupdateFechaHora);


        recibirProposito();

        propiedadesBotonUpdate();

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarUpdate();
            }
        });

        btnupdateFechaHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(true);
            }
        });

        btncambiarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFecha();
            }
        });

        lblfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFecha();
            }
        });


        lblhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerHora();
            }
        });

        btncambiarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerHora();
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(cambios.this, MenuPropositos.class);
                startActivity(intent);
            }
        });


    }

    private void colorBotonUpdate(){
        if (estado == 0 && repetir == 0){
            btnupdate.setBackgroundColor(Color.CYAN);
            btnupdate.setTextColor(Color.BLACK);
        }else if (estado==1){
            btnupdate.setBackgroundColor(Color.RED);
            btnupdate.setTextColor(Color.WHITE);
        }else if (estado == 0 && repetir == 1){
            btnupdate.setBackgroundColor(Color.WHITE);
            btnupdate.setTextColor(Color.BLACK);
        }
    }

    private void textoBotonUpdate(){
        if (repetir == 1){
            btnupdate.setText("Proposito realizado");
            btncambiarFecha.setEnabled(false);
            lblfecha.setEnabled(false);
        }
    }

    private void propiedadesBotonUpdate(){
        textoBotonUpdate();
        colorBotonUpdate();
    }

    private void confirmarUpdate(){
        String btnaceptar = "";
        String mensaje = "";
        if (repetir == 1){
            btnaceptar = "Realizar y guardar";
            mensaje =  "¿Desea realizar el proposito?";
        }else{
            btnaceptar = "Cambiar";
            mensaje =  "¿Desea cambiar el estado?";
        }
        confirmarCambiarEstado(btnaceptar, mensaje);
    }

    private void update(boolean cambiarFechaHora){

        SQLiteDatabase db = h.getWritableDatabase();

        int estadoGuardar = -1;
        String consulta = "";


        if (!cambiarFechaHora && repetir == 0){

            if (estado == 1){
                estadoGuardar = 0;
            }else if (estado == 0){
                estadoGuardar = 1;
            }
            consulta =  "UPDATE " + sql.tabla + " SET " + sql.noEstado +  "='" + estadoGuardar + "'" + " WHERE id='" + id + "'";
            actualizar(db, consulta);
        }


        if (cambiarFechaHora){
            consulta =  "UPDATE " + sql.tabla + " SET " + sql.noFecha + "='" + lblfecha.getText().toString() + "'," + sql.noHora + "='" + lblhora.getText() + "'" + " WHERE id='" + id + "'";
            actualizar(db, consulta);
        }

        verPropositos();
    }

    private void actualizar(SQLiteDatabase db, String consulta){
        db.execSQL(consulta);
        Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_LONG).show();
        db.close();
    }




    private void recibirProposito(){
        Bundle extra = getIntent().getExtras();

        if (extra!=null){

            Proposito proposito = null;

            proposito = (Proposito) extra.getSerializable("proposito");

            id = proposito.getId();
            estado = proposito.getEstado();
            String descripcion = proposito.getDescripcion();
            fechaProposito = proposito.getFecha();
            repetir = proposito.getRepetir();

            String h []= proposito.getHora().split(":");

            hora = Integer.parseInt(h[0]);
            minuto = Integer.parseInt(h[1]);

            horaProposito = hora + ":" + minuto;

            int img = -1;

            if (estado == 1){
                img = R.drawable.estado_1;
            }else if (estado == 0 && repetir == 0){
                img = R.drawable.estado_2;
            }else if (estado ==0 && repetir == 1){
                img = R.drawable.repetir;
            }


            imgestado.setImageResource(img);
            lbldescripcion.setText(descripcion);
            lblfecha.setText(fechaProposito);
            lblhora.setText(horaProposito);

        }

    }


    private void verPropositos(){
        Intent i = new Intent(cambios.this,ver.class);
        i.putExtra("hoy", true);
        i.putExtra("enEspera", true);
        i.putExtra("proposito_unico", false);
        startActivity(i);
    }


    private void cambiarFecha(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        date = new DatePickerDialog(cambios.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                lblfecha.setText(day + "/" + (month+1) + "/" + year);

            }
        }, year, month, dayofmonth);
        date.show();
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


    private void confirmarCambiarEstado(String btnaceptar, String mensaje){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton(btnaceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cambiarEstado();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Confirmar")
                .setMessage(mensaje)
                .create();

        dialogo.show();

    }

    private void cambiarEstado(){
        update(false);
        guardarRealizado();
    }

    private void guardarRealizado(){

        Calendar calendar = Calendar.getInstance();

        if (repetir == 1){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            int minuto = calendar.get(Calendar.MINUTE);

            String fechaActual  = dayofmonth + "/" + (month+1) + "/" + year;

            String horaFormateada =  (hora < 10)? String.valueOf(CERO + hora) : String.valueOf(hora);

            String minutoFormateado = (minuto < 10)? String.valueOf(CERO + minuto):String.valueOf(minuto);

            String horaActual = horaFormateada + DOS_PUNTOS + minutoFormateado;

            helpBdd h = new helpBdd(this, "bdd_proposito", null, 1);
            SQLiteDatabase db = h.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(sql.noDescripcion, lbldescripcion.getText().toString());
            values.put(sql.noFecha, fechaActual);
            values.put(sql.noHora, horaActual);

            db.insert(sql.tabla2,null, values);
            db.close();

            Toast.makeText(getApplicationContext(), "Guardado correctamente.", Toast.LENGTH_LONG).show();
        }

    }

}
