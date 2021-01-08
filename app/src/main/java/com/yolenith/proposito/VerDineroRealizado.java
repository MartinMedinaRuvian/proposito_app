package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class VerDineroRealizado extends AppCompatActivity {
    ListView lv;
    ArrayList<String> listaDescripciones;
    ArrayList<DineroRealizado> propositos;
    helpBdd h;
    ImageView imgmenu;
    CheckBox chkhoy, chktipo;

    int year;
    int month;
    int dayofmonth;
    private String hoy = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_dinero_realizado);

        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);

        lv = findViewById(R.id.lv);
        imgmenu = findViewById(R.id.imgmenu);
        chkhoy = findViewById(R.id.chkhoy);
        chktipo = findViewById(R.id.chktipo);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);


        hoy  = dayofmonth + "/" + (month+1) + "/" + year;

        chkhoy.setChecked(true);
        chktipo.setChecked(false);

        comprobarConsultar();

        chkhoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarConsultar();
            }
        });

        chktipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarConsultar();
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuDinero.class);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int idEliminar = propositos.get(i).getId();
                delete(idEliminar);
                return true;
            }
        });
    }

    private void comprobarConsultar(){
        consultarNotas(chkhoy.isChecked(), chktipo.isChecked());
    }

    private void consultarNotas(boolean fechaActual, boolean ingreso) {

        String sql = "";
        String SELECT = "select id, descripcion,fecha,hora,valor,tipo from realizado_dinero";
        String WHERE = "";
        SQLiteDatabase db = h.getReadableDatabase();

        DineroRealizado proposito = null;
        propositos = new ArrayList<DineroRealizado>();

        if (!fechaActual && ingreso){
            WHERE = "WHERE tipo='0' ORDER BY fecha";
        }else if (!fechaActual && !ingreso){
            WHERE = "WHERE tipo='1' ORDER BY fecha";
        }else if(fechaActual && ingreso){
            WHERE = "WHERE fecha='" + hoy + "' AND tipo='0' ORDER BY fecha";
        }else if(fechaActual && !ingreso){
            WHERE = "WHERE fecha='" + hoy + "' AND tipo='1' ORDER BY fecha";
        }

        sql = SELECT + " "  + WHERE;

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            proposito = new DineroRealizado();
            proposito.setId(c.getInt(0));
            proposito.setDescripcion(c.getString(1));
            proposito.setFecha(c.getString(2));
            proposito.setHora(c.getString(3));
            proposito.setValor(c.getFloat(4));
            proposito.setTipo(c.getInt(5));

            propositos.add(proposito);

        }

        AdaptadorListaDineroRealizado adaptador = new AdaptadorListaDineroRealizado(this, propositos);
        lv.setAdapter(adaptador);
    }

    private void delete(final int idEliminar){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRegistro(idEliminar);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Confirmar")
                .setMessage("¿Desea eliminar el proposito #" + idEliminar + "?")
                .create();

        dialogo.show();

    }

    private void deleteRegistro(int idEliminar){
        SQLiteDatabase db = h.getWritableDatabase();
        String[]parametros ={String.valueOf(idEliminar)};
        db.delete(sql.tabla4,sql.noId+"=?",parametros);
        Toast.makeText(getApplicationContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
        db.close();
       comprobarConsultar();
    }

}
