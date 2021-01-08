package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class MenuDinero extends AppCompatActivity {

    Button btnagregar, btnver, btnverRealizado;
    ImageView imgmenu, imgingreso, imgegreso;
    TextView lblhoyTotalIngreso, lblfijoTotalIngreso, lbltotalIngresos,
            lblhoyTotalEgreso, lblfijoTotalEgreso, lbltotalEgreso, lblutilidadHoy, lblutilidadFijo, lblutilidadTotal;
    private String fechaActual ="";

    int year;
    int month;
    int dayofmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dinero);

        imgmenu = findViewById(R.id.imgmenu);
        imgingreso = findViewById(R.id.imgingreso);
        imgegreso = findViewById(R.id.imgegreso);
        btnagregar = findViewById(R.id.btndineroAgregar);
        btnver = findViewById(R.id.btndineroVer);
        btnverRealizado = findViewById(R.id.btndineroVerRealizado);

        lbltotalEgreso = findViewById(R.id.lbltotalEgreso);
        lbltotalIngresos = findViewById(R.id.lbltotalIngresos);
        lblhoyTotalIngreso = findViewById(R.id.lblhoyTotalIngreso);
        lblhoyTotalEgreso = findViewById(R.id.lblhoyTotalEgreso);
        lblfijoTotalIngreso = findViewById(R.id.lblfijoTotalIngreso);
        lblfijoTotalEgreso = findViewById(R.id.lblfijoTotalEgreso);
        lblutilidadHoy = findViewById(R.id.lblhoyUtilidad);
        lblutilidadFijo = findViewById(R.id.lblfijoUtilidad);
        lblutilidadTotal = findViewById(R.id.lbltotalUtilidad);

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarDinero();
            }
        });

        btnver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verDinero(true, true);
            }
        });

        btnverRealizado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verDineroRealizado();
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        imgingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             verDinero(true,true);
            }
        });

        imgegreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDinero(true,false);
            }
        });

        lblhoyTotalIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDinero(true,true);
            }
        });

        lbltotalIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDinero(false,true);
            }
        });

        lblhoyTotalEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDinero(true,false);
            }
        });

        lbltotalEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verDinero(false,false);
            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        fechaActual  = dayofmonth + "/" + (month+1) + "/" + year;

        verTotales();

    }


    private void verTotales(){
        Util util = new Util();
        lblhoyTotalIngreso.setText("Hoy $"+ util.formatoMoneda(totalNormal(true,  true)));
        lblfijoTotalIngreso.setText("Fijo $" + util.formatoMoneda(totalFijo(true)));
        lbltotalIngresos.setText("Total $" + util.formatoMoneda(total(true)));

        lblhoyTotalEgreso.setText("Hoy $"+ util.formatoMoneda(totalNormal(false,  true)));
        lblfijoTotalEgreso.setText("Fijo $" + util.formatoMoneda(totalFijo(false)));
        lbltotalEgreso.setText("Total $" + util.formatoMoneda(total(false)));

        lblutilidadHoy.setText("Hoy $" + util.formatoMoneda(utilidadNormalHoy()));
        lblutilidadFijo.setText("Fijo $" + util.formatoMoneda(utilidadFijo()));
        lblutilidadTotal.setText("Total $" + util.formatoMoneda(utilidadTotal()));
    }


    private void agregarDinero() {
        Intent i = new Intent(getApplicationContext(), AgregarDinero.class);
        startActivity(i);
    }


    private void verDinero(boolean hoy, boolean ingreso) {
        Intent i = new Intent(getApplicationContext(), VerDinero.class);
        i.putExtra("hoy", hoy);
        i.putExtra("ingreso", ingreso);
        startActivity(i);
    }

    private void verDineroRealizado() {
        Intent i = new Intent(getApplicationContext(), VerDineroRealizado.class);
        startActivity(i);
    }

    private float totalNormal(boolean ingreso, boolean hoy){
        helpBdd h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);
        float total = 0;
        String sql = "";
        if (hoy && ingreso){
            sql = "select sum(valor) as valor, descripcion, repetir from dinero WHERE fecha='" + fechaActual + "' AND tipo='0' AND repetir='0'";
        }else if(!hoy && !ingreso){
            sql = "select sum(valor) as valor, descripcion, repetir from dinero WHERE tipo='1' AND repetir='0'";
        }else if (!hoy && ingreso){
            sql = "select sum(valor) as valor, descripcion, repetir from dinero WHERE tipo='0' AND repetir='0'";
        }else if (hoy && !ingreso){
            sql = "select sum(valor) as valor, descripcion, repetir from dinero WHERE fecha='" + fechaActual + "' AND tipo='1' AND repetir='0'";
        }

        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
   total = c.getFloat(0);
        }
        db.close();
        return total;
    }


    private float totalFijo(boolean ingreso){
        helpBdd h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);
        float total = 0;
        String sql = "";
        if (ingreso){
            sql = "select sum(valor) as valor from realizado_dinero WHERE tipo='0'";
        }else if(!ingreso){
            sql = "select sum(valor) as valor from realizado_dinero WHERE tipo='1'";
        }

        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            total = c.getFloat(0);
        }
        db.close();
        return total;
    }

   private float total(boolean ingreso){
        return totalNormal(ingreso, false) + totalFijo(ingreso);
   }

   private float utilidadNormalHoy(){
        return totalNormal(true, true) - totalNormal(false, true);
   }
    private float utilidadFijo(){
        return totalFijo(true) - totalFijo(false);
    }

    private float utilidadTotal(){
        return total(true) - total(false);
    }

}
