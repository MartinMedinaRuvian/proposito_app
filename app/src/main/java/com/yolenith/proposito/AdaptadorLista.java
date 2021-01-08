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

public class AdaptadorLista extends BaseAdapter {
    Activity activity;
    ArrayList<Proposito> propositos;

    public AdaptadorLista(Activity activity, ArrayList<Proposito> propositos) {
        this.activity = activity;
        this.propositos = propositos;
    }

    @Override
    public int getCount() {
        return propositos.size();
    }

    @Override
    public Object getItem(int i) {
        return propositos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView lbltitulo, lbldescripcion, lblfecha, lblhora;
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
            v=inflater.inflate(R.layout.item_lista, null);
        }

        lbltitulo = v.findViewById(R.id.lbltitulo);
        lbldescripcion = v.findViewById(R.id.lbldescripcion);
        lblfecha = v.findViewById(R.id.lblfecha);
        lblhora = v.findViewById(R.id.lblhora);
        imgestado = v.findViewById(R.id.imgestado);

        Proposito proposito = propositos.get(i);

        String titulo = proposito.getTitulo();
        String descripcion = proposito.getDescripcion();

        lbltitulo.setText(titulo);
        lbldescripcion.setText(descripcion);

        lblhora.setText("Hora: " + proposito.getHora());

        int img = -1;

        if(proposito.getEstado() == 0 && proposito.getRepetir() ==0){
            img = R.drawable.estado_2;
        }else if (proposito.getEstado() == 0 && proposito.getRepetir() ==1){
            img = R.drawable.repetir;
        }else if (proposito.getEstado() == 1){
            img = R.drawable.estado_1;
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

        if (propositos.get(i).getFecha().equals(hoy) && propositos.get(i).getRepetir() == 0){
            fecha = "HOY";
        }else if (propositos.get(i).getRepetir() == 1){
            fecha = "Realizado: " + realizado  + veces;
        }else{
            fecha = "Para el dia " + propositos.get(i).getFecha();
        }



        lblfecha.setText(fecha);

        return v;
    }

    private int vecesRealizado(Context context, String decripcion){
        helpBdd h = new helpBdd(context, "bdd_proposito", null, 1);
        int total = 0;
        String sql = "select id from realizado WHERE descripcion='" + decripcion + "'";
        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            total ++;
        }
        db.close();
        return total;
    }

    private String ultimaFecha(Context context, String decripcion){
        helpBdd h = new helpBdd(context, "bdd_proposito", null, 1);
        String fecha = "";
        String sql = "select fecha from realizado WHERE descripcion='" + decripcion + "' ORDER DESC fecha";
        SQLiteDatabase db = h.getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
           return  c.getString(0);
        }
        db.close();
        return "";
    }


}
