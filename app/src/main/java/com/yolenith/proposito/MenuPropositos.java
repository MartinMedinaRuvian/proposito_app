package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class MenuPropositos extends AppCompatActivity {
Button btnagregar, btnver, btnverDiario;
TextView lblhoyHecho, lblhoyEnEspera, lblhecho, lblenEspera;
ImageView imgmenu, imgcumplido, imgenEspera;
    private String fechaActual ="";


    int year;
    int month;
    int dayofmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_propositos);

        lblhoyHecho = findViewById(R.id.lblhoyHecho);
        lblhoyEnEspera = findViewById(R.id.lblhoyEnEspera);
        lblhecho = findViewById(R.id.lblhecho);
        lblenEspera = findViewById(R.id.lblenEspera);

        imgmenu = findViewById(R.id.imgmenu);
        imgcumplido = findViewById(R.id.imgenCumplido);
        imgenEspera = findViewById(R.id.imgenEspera);
        btnagregar = findViewById(R.id.btnadd);
        btnver = findViewById(R.id.btnver);
        btnverDiario = findViewById(R.id.btnverDiarioRealizado);



        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });

        btnver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(true, true);
            }
        });

        btnverDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verDiarioRealizado();
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        imgcumplido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(true, false);
            }
        });

        imgenEspera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(true, true);
            }
        });

        lblhoyEnEspera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(true, true);
            }
        });

        lblenEspera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(false, true);
            }
        });


        lblhoyHecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(true, false);
            }
        });

        lblhecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver(false, false);
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
        lblhoyHecho.setText("Hoy " + total(false, true));
        lblhoyEnEspera.setText("Hoy " + total(true, true));

        lblhecho.setText("Total " + total(false, false));
        lblenEspera.setText("Total " + total(true, false));
    }

    private String total(boolean enEspera, boolean hoy){
        int total = 0;
        helpBdd h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);
        String sql = "";
        if (hoy && enEspera){
            sql = "select id from proposito WHERE fecha='" + fechaActual + "' AND estado='0' AND repetir='0'";
        }else if(!hoy && !enEspera){
            sql = "select id from proposito WHERE estado='1'";
        }else if (!hoy && enEspera){
            sql = "select id from proposito WHERE estado='0' AND repetir='0'";
        }else if (hoy && !enEspera){
            sql = "select id from proposito WHERE fecha='" + fechaActual + "' AND estado='1'";
        }

        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            total++;
        }

        db.close();
        return String.valueOf(total);
    }

    private void agregar() {
        Intent i = new Intent(getApplicationContext(), agregar.class);
        startActivity(i);
    }

    private void ver(boolean hoy, boolean enEspera) {
        Intent i = new Intent(getApplicationContext(), ver.class);
        i.putExtra("hoy", hoy);
        i.putExtra("enEspera", enEspera);
        i.putExtra("proposito_unico", false);
        startActivity(i);
    }

    private void verDiarioRealizado() {
        Intent i = new Intent(getApplicationContext(), VerDiarioRealizado.class);
        startActivity(i);
    }


}
