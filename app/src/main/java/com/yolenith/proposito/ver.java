package com.yolenith.proposito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationManager;
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

public class ver extends AppCompatActivity {
    ListView lv;
    ArrayList<String> listaDescripciones;
    ArrayList<Proposito> propositos;
    CheckBox chkhoy, chkestado, chkdiario;
    helpBdd h;
    ImageView imgmenu;

    int year;
    int month;
    int dayofmonth;

    private String hoy = "";

    private int estadoProposito = -1;
    private int idEliminar = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);

        cerrarNotificacion();

        h = new helpBdd(getApplicationContext(), "bdd_proposito", null, 1);

        lv = findViewById(R.id.lv);
        chkhoy = findViewById(R.id.chkhoy);
        chkestado = findViewById(R.id.chkestado);
        chkdiario = findViewById(R.id.chkdiario);
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

               Proposito proposito = propositos.get(pos);

                Intent i = new Intent(ver.this, cambios.class);
      Bundle bundle =new Bundle();
      bundle.putSerializable("proposito", proposito);

      i.putExtras(bundle);
                startActivity(i);

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idEliminar = propositos.get(i).getId();
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

        chkestado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarSeleccion();
            }
        });

        chkdiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarSeleccion();
            }
        });


        imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ver.this, MenuPropositos.class);
                startActivity(intent);
            }
        });


    }

    private void inicio(){
        Bundle extra = getIntent().getExtras();
        boolean hoy = extra.getBoolean("hoy");
        boolean enEspera = extra.getBoolean("enEspera");

        chkhoy.setChecked(hoy);
        chkestado.setChecked(enEspera);
        chkdiario.setChecked(false);

    }



    private void cerrarNotificacion(){
        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            int idNotificacion = extra.getInt("id_notificacion");
            NotificationManager notificationManager = ((NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE));
            notificationManager.cancel(idNotificacion);
            System.out.println(idNotificacion);
        }

    }

    private boolean propositoUnioo(){
        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            return extra.getBoolean("proposito_unico");
        }
        return false;
    }

    private int idPropositoNotificacion(){
        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            return extra.getInt("id_notificacion");
        }
        return -1;
    }

    private void comprobarSeleccion() {
        datosIniciales(chkhoy.isChecked(), chkestado.isChecked(), chkdiario.isChecked(), propositoUnioo());
    }

    private void datosIniciales(boolean hoy, boolean enEspera, boolean diario, boolean soloUnProposito){

        if (!soloUnProposito){
            consultarNotas(hoy, enEspera, diario);
        }else{
            consultarNota();
        }

        AdaptadorLista adaptador = new AdaptadorLista(this, propositos);
        lv.setAdapter(adaptador);

    idEliminar = -1;
    }


    private void consultarNota(){
        int id = idPropositoNotificacion();
        SQLiteDatabase db = h.getReadableDatabase();
        String sql = "SELECT id,titulo,descripcion,fecha,hora,estado,repetir FROM proposito WHERE id='" + id + "'";

        Cursor c = db.rawQuery(sql, null);

        Proposito  proposito = null;
        propositos = new ArrayList<Proposito>();


while(c.moveToNext()){
    proposito = new Proposito();
    proposito.setId(c.getInt(0));
    proposito.setTitulo(c.getString(1));
    proposito.setDescripcion(c.getString(2));
    proposito.setFecha(c.getString(3));
    proposito.setHora(c.getString(4));
    proposito.setEstado(c.getInt(5));
    proposito.setRepetir(c.getInt(6));
    propositos.add(proposito);
}
    }

    private void consultarNotas(boolean fechaActual, boolean enEspera, boolean diario) {

        String sql = "";
        String SELECT = "select id,titulo,descripcion,fecha,hora,estado,repetir from proposito";
        String WHERE = "";
        SQLiteDatabase db = h.getReadableDatabase();

        Proposito proposito = null;
        propositos = new ArrayList<Proposito>();

        if (!fechaActual && enEspera && !diario){
            WHERE = "WHERE estado='0' AND repetir='0' ORDER BY fecha";
        }else if (!fechaActual && enEspera && diario){
            WHERE = "WHERE estado='0' AND repetir='1' ORDER BY fecha";
        }else if(!fechaActual && !enEspera && !diario){
            WHERE = "WHERE estado='1' AND repetir='0' ORDER BY fecha";
        }else if(!fechaActual && !enEspera && diario){
            WHERE = "WHERE estado='1' AND repetir='1' ORDER BY fecha";
        }else if(fechaActual && enEspera && !diario){
            WHERE = "WHERE (fecha='" + hoy + "' OR repetir='1') AND estado='0' AND repetir='0' ORDER BY fecha";
        }else if(fechaActual && enEspera && diario){
            WHERE = "WHERE (fecha='" + hoy + "' OR repetir='1') AND estado='0' AND repetir='1' ORDER BY fecha";
        }else if(fechaActual && !enEspera && !diario){
            WHERE = "WHERE fecha='" + hoy + "' AND estado='1' AND repetir='0' ORDER BY fecha";
        }else if(fechaActual && !enEspera && diario){
            WHERE = "WHERE fecha='" + hoy + "' AND estado='1' AND repetir='1' ORDER BY fecha";
        }

        sql = SELECT + " "  + WHERE;

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            proposito = new Proposito();
            proposito.setId(c.getInt(0));
            proposito.setTitulo(c.getString(1));
            proposito.setDescripcion(c.getString(2));
            proposito.setFecha(c.getString(3));
            proposito.setHora(c.getString(4));
            proposito.setEstado(c.getInt(5));
            proposito.setRepetir(c.getInt(6));

            propositos.add(proposito);

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
        db.delete(sql.tabla,sql.noId+"=?",parametros);
        Toast.makeText(getApplicationContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
        db.close();
        comprobarSeleccion();
    }
}
