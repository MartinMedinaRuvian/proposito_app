package com.yolenith.proposito;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdaptadorListaDinero extends BaseAdapter {
    Activity activity;
    ArrayList<Dinero> listaDinero;

    public AdaptadorListaDinero(Activity activity, ArrayList<Dinero> listaDinero) {
        this.activity = activity;
        this.listaDinero = listaDinero;
    }

    @Override
    public int getCount() {
        return listaDinero.size();
    }

    @Override
    public Object getItem(int i) {
        return listaDinero.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView lbltitulo, lbldescripcion, lblfecha, lblvalor;
        ImageView imgestado;

        int year;
        int month;
        int dayofmonth;

        String hoy = "";

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        hoy  = dayofmonth + "/" + (month+1) + "/" + year;

        if (v == null){
            LayoutInflater inflater = (LayoutInflater)activity.getLayoutInflater();
            v=inflater.inflate(R.layout.item_lista_dinero, null);
        }

        lbltitulo = v.findViewById(R.id.lbltitulo);
        lbldescripcion = v.findViewById(R.id.lbldescripcion);
        lblfecha = v.findViewById(R.id.lblfecha);
        imgestado = v.findViewById(R.id.imgestado);
        lblvalor = v.findViewById(R.id.lblvalor);

        Dinero dinero = listaDinero.get(i);

        String titulo = dinero.getTitulo();
        String descripcion = dinero.getDescripcion();


        lbltitulo.setText(titulo);
        lbldescripcion.setText(descripcion);
        lblvalor.setText("$ " + new Util().formatoMoneda(dinero.getValor()));

        int img = -1;

        if(dinero.getTipo() == 0 && dinero.getRepetir() ==0){
            img = R.drawable.ingreso;
        }else if (dinero.getTipo() == 0 && dinero.getRepetir() ==1){
            img = R.drawable.repetir;
        }else if (dinero.getTipo() == 1 && dinero.getRepetir() ==1){
            img = R.drawable.repetir_egreso;
        }else if (dinero.getTipo() == 1){
            img = R.drawable.egreso;
        }

        lbltitulo.setBackgroundColor(Color.GRAY);
        lbltitulo.setTextColor(Color.WHITE);


        imgestado.setImageResource(img);

        int realizado = vecesRealizado(v.getContext(), descripcion);
        String veces = "";

        if (realizado == 1){
            veces = " Vez";
        }else{
            veces = " Veces";
        }

        if (realizado == 1){

        }

        String fecha = "";

        if (dinero.getFecha().equals(hoy) && dinero.getRepetir() == 0){
            fecha = "HOY a las " + dinero.getHora();
        }else if (dinero.getRepetir() == 1){
            fecha = "Realizado: " + realizado  + veces + "\n"
            + "Ultima vez: " + ultimaFechaFijo(v.getContext(), descripcion);
        }else{
            fecha = "Para el dia " + dinero.getFecha() + " a las " + dinero.getHora();
        }



        lblfecha.setText(fecha);

        return v;
    }

    private int vecesRealizado(Context context, String decripcion){
        helpBdd h = new helpBdd(context, "bdd_proposito", null, 1);
        int total = 0;
        String sql = "select id from realizado_dinero WHERE descripcion='" + decripcion + "'";
        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            total ++;
        }
        db.close();
        return total;
    }

    private String ultimaFechaFijo(Context context, String decripcion){
        helpBdd h = new helpBdd(context, "bdd_proposito", null, 1);
        String fecha = "";
        String sql = "select fecha from realizado_dinero WHERE descripcion='" + decripcion + "' ORDER BY fecha DESC LIMIT 1";
        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
          fecha = c.getString(0);
        }
        db.close();
        return fecha;
    }


}
