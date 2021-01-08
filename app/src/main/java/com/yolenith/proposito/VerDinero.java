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

public class VerDinero extends AppCompatActivity {
    ListView lv;
    ArrayList<String> listaDescripciones;
    ArrayList<Dinero> listaDinero;
    CheckBox chkhoy, chktipo, chkfijo;
    helpBdd h;
    ImageView imgmenu;

    int year;
    int month;
    int dayofmonth;

    private String hoy = "";

    private int idEliminar = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_dinero);

        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);

        lv = findViewById(R.id.lv);
        chkhoy = findViewById(R.id.chkhoy);
        chktipo = findViewById(R.id.chkestado);
        chkfijo = findViewById(R.id.chkfijo);
        imgmenu = findViewById(R.id.imgmenu);

     inicio();


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        hoy  = dayofmonth + "/" + (month+1) + "/" + year;

        comprobarSeleccion();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {

                Dinero dinero = listaDinero.get(pos);

              if (dinero.getRepetir() == 1){

                  Intent intent  = new Intent(getApplicationContext(), CambioDinero.class);

                  Bundle bundle = new Bundle();
                  bundle.putSerializable("dinero", dinero);
                  intent.putExtras(bundle);
                  startActivity(intent);

              }else{
                  Toast.makeText(getApplicationContext(), "Este registro no se puede modificar", Toast.LENGTH_LONG).show();
              }

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idEliminar = listaDinero.get(i).getId();
                delete();
                return true;
            }
        });

        chkhoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarSeleccion();
            }
        });

        chktipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarSeleccion();
            }
        });

        chkfijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarSeleccion();
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

    private void inicio() {
        Bundle extra = getIntent().getExtras();
        boolean hoy = extra.getBoolean("hoy");
        boolean ingreso = extra.getBoolean("ingreso");

        chkhoy.setChecked(hoy);
        chktipo.setChecked(ingreso);
        chkfijo.setChecked(false);
    }

    private void comprobarSeleccion() {
        datosIniciales(chkhoy.isChecked(), chktipo.isChecked(), chkfijo.isChecked());
    }

    private void datosIniciales(boolean hoy, boolean ingreso, boolean fijo){

        consultarDinero(hoy, ingreso, fijo);

        AdaptadorListaDinero adaptador = new AdaptadorListaDinero(this, listaDinero);
        lv.setAdapter(adaptador);

        //adaptador = new ArrayAdapter<>(getApplicationContext(), R.layout.lvpersonalizada, listaDescripciones);



        idEliminar = -1;
    }



    private void consultarDinero(boolean fechaActual, boolean ingreso, boolean fijo) {

        String sql = "";
        String SELECT = "select id,titulo,descripcion,fecha,hora,tipo,repetir,valor from dinero";
        String WHERE = "";
        SQLiteDatabase db = h.getReadableDatabase();

        Dinero dinero = null;
        listaDinero = new ArrayList<Dinero>();

        if (!fechaActual && ingreso && !fijo){
            WHERE = "WHERE tipo='0' AND repetir='0' ORDER BY fecha";
        }else if(!fechaActual && !ingreso && !fijo){
            WHERE = "WHERE tipo='1' AND repetir='0' ORDER BY fecha";
        }else if(fechaActual && ingreso && !fijo){
            WHERE = "WHERE fecha='"  + hoy + "'  AND tipo='0' AND repetir='0' ORDER BY fecha";
        }else if(fechaActual && !ingreso && !fijo){
            WHERE = "WHERE fecha='" + hoy + "'  AND tipo='1' AND repetir='0' ORDER BY fecha";
        }

        else if (!fechaActual && ingreso && fijo){
            WHERE = "WHERE tipo='0' AND repetir='1' ORDER BY fecha";
        }else if(!fechaActual && !ingreso && fijo){
            WHERE = "WHERE tipo='1' AND repetir='1' ORDER BY fecha";
        }else if(fechaActual && ingreso && fijo){
            WHERE = "WHERE fecha='"  + hoy + "'  AND tipo='0' AND repetir='1' ORDER BY fecha";
        }else if(fechaActual && !ingreso && fijo){
            WHERE = "WHERE fecha='" + hoy + "'  AND tipo='1' AND repetir='1' ORDER BY fecha";
        }

        sql = SELECT + " "  + WHERE;

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            dinero = new Dinero();
            dinero.setId(c.getInt(0));
            dinero.setTitulo(c.getString(1));
            dinero.setDescripcion(c.getString(2));
            dinero.setFecha(c.getString(3));
            dinero.setHora(c.getString(4));
            dinero.setTipo(c.getInt(5));
            dinero.setRepetir(c.getInt(6));
            dinero.setValor(c.getFloat(7));

            listaDinero.add(dinero);

        }
    }


    private void delete(){

        AlertDialog dialogo = new AlertDialog
                .Builder(this)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRegistro();
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

    private void deleteRegistro(){
        SQLiteDatabase db = h.getWritableDatabase();
        String[]parametros ={String.valueOf(idEliminar)};
        db.delete(sql.tabla3,sql.noId+"=?",parametros);
        Toast.makeText(getApplicationContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
        db.close();
        comprobarSeleccion();
    }
}
