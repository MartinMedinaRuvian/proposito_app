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

public class VerDiarioRealizado extends AppCompatActivity {

    ListView lv;
    ArrayList<String> listaDescripciones;
    ArrayList<PropositoDiarioRealizado> propositos;
    helpBdd h;
    ImageView imgmenu;
    CheckBox chkhoy;

    int year;
    int month;
    int dayofmonth;
    private String hoy = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_diario_realizado);

        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);

        lv = findViewById(R.id.lv);
        imgmenu = findViewById(R.id.imgmenu);
        chkhoy = findViewById(R.id.chkhoy);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);


        hoy  = dayofmonth + "/" + (month+1) + "/" + year;

        chkhoy.setChecked(true);

        consultarNotas(chkhoy.isChecked());

        chkhoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarNotas(chkhoy.isChecked());
            }
        });

        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuPropositos.class);
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

    private void consultarNotas(boolean fechaActual) {

        String sql = "";
        String SELECT = "select id, descripcion,fecha,hora from realizado";
        String WHERE = "";
        SQLiteDatabase db = h.getReadableDatabase();

        PropositoDiarioRealizado proposito = null;
        propositos = new ArrayList<PropositoDiarioRealizado>();

        if (!fechaActual){
            WHERE = "WHERE 1 ORDER BY fecha";
        }else if(fechaActual){
            WHERE = "WHERE fecha='" + hoy + "' ORDER BY fecha";
        }

        sql = SELECT + " "  + WHERE;

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            proposito = new PropositoDiarioRealizado();
            proposito.setId(c.getInt(0));
            proposito.setDescripcion(c.getString(1));
            proposito.setFecha(c.getString(2));
            proposito.setHora(c.getString(3));

            propositos.add(proposito);

        }

        AdaptadorListaDiarioRealizado adaptador = new AdaptadorListaDiarioRealizado(this, propositos);
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
        db.delete(sql.tabla2,sql.noId+"=?",parametros);
        Toast.makeText(getApplicationContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
        db.close();
        consultarNotas(chkhoy.isChecked());
    }

}
