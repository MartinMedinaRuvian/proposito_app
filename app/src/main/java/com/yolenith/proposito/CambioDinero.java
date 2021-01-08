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

public class CambioDinero extends AppCompatActivity {
    ImageView imgestado, imgmenu;
    TextView lblfecha, lblhora, lbldescripcion;
    Button btnupdate, btnupdateFechaHora, btncambiarFecha, btncambiarHora;

    private int id;
    private String descripcion = "";
    private String fechaDinero = "";
    private String horaDinero = "";
    private float valor = 0;
    private int tipo;

    private static final String PROPOSITO_EN_ESPERA = "0";
    private static final String PROPOSITO_CUMPLIDO = "1";

    Calendar calendar;
    DatePickerDialog date;

    int year;
    int month;
    int dayofmonth;
    int minuto;
    int hora;


    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    helpBdd h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_dinero);



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


        recibirInfoDinero();
        textoBtnUpdate();


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarGuardarRealizado();
            }
        });

        btnupdateFechaHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        lblfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarFecha();
            }
        });

        btncambiarFecha.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(getApplicationContext(), MenuDinero.class);
                startActivity(intent);
            }
        });


    }

    private void textoBtnUpdate(){
        if (tipo == 0){
            btnupdate.setText("Recibir dinero");
        }else if (tipo ==1){
            btnupdate.setText("Pagar dinero");
        }
    }


    private void update(){
            SQLiteDatabase db = h.getWritableDatabase();
            String consulta =  "UPDATE " + sql.tabla3 + " SET " + sql.noFecha + "='" + lblfecha.getText().toString() + "'," + sql.noHora + "='" + lblhora.getText() + "'" + " WHERE id='" + id + "'";;
            actualizar(db, consulta);

        verListaDinero();
    }

    private void actualizar(SQLiteDatabase db, String consulta){
        db.execSQL(consulta);
        Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_LONG).show();
        db.close();
    }




    private void recibirInfoDinero(){
        Bundle extra = getIntent().getExtras();

        if (extra!=null){

            Dinero dinero = null;
            dinero = (Dinero) extra.getSerializable("dinero");

            id = dinero.getId();
            descripcion = dinero.getDescripcion();
            valor = dinero.getValor();
            fechaDinero = dinero.getFecha();
            tipo = dinero.getTipo();

            String h []= dinero.getHora().split(":");

            hora = Integer.parseInt(h[0]);
            minuto = Integer.parseInt(h[1]);

            horaDinero = hora + ":" + minuto;

            int img = -1;

            if (tipo == 0){
                img = R.drawable.repetir;
            }else if (tipo == 1){
                img = R.drawable.repetir_egreso;
            }

            imgestado.setImageResource(img);
            lbldescripcion.setText(descripcion);
            lblfecha.setText(fechaDinero);
            lblhora.setText(horaDinero);

        }

    }


    private void verListaDinero(){
        Intent i = new Intent(getApplicationContext(),VerDinero.class);
        i.putExtra("hoy", true);
        i.putExtra("ingreso", true);
        startActivity(i);
    }


    private void cambiarFecha(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        date = new DatePickerDialog(CambioDinero.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                lblfecha.setText(day + "/" + (month+1) + "/" + year);
                update();

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

    private void confirmarGuardarRealizado(){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guardarRealizado();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Confirmar")
                .setMessage("¿Desea guardar?")
                .create();

        dialogo.show();

    }

    private void guardarRealizado(){

        Calendar calendar = Calendar.getInstance();

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
            values.put(sql.noValor, valor);
        values.put(sql.noTipo, tipo);

            db.insert(sql.tabla4,null, values);
            db.close();

            Toast.makeText(getApplicationContext(), "Guardado correctamente.", Toast.LENGTH_LONG).show();

            confirmarCambiarFecha();

    }


    private void confirmarCambiarFecha(){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cambiarFecha();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Confirmar")
                .setMessage("¿Cambiar la fecha para notificación?")
                .create();

        dialogo.show();

    }

}
