package com.yolenith.proposito;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorListaDiarioRealizado extends BaseAdapter {

    Activity activity;
    ArrayList<PropositoDiarioRealizado> propositos;


    public AdaptadorListaDiarioRealizado(Activity activity, ArrayList<PropositoDiarioRealizado> propositos) {
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
        TextView lbldescripcion, lblfecha, lblhora;
        ImageView imgestado;

        if (v == null){
            LayoutInflater inflater = (LayoutInflater)activity.getLayoutInflater();
            v=inflater.inflate(R.layout.item_lista_diario_realizado, null);
        }

        lbldescripcion = v.findViewById(R.id.lbldescripcion);
        lblfecha = v.findViewById(R.id.lblfecha);
        lblhora = v.findViewById(R.id.lblhora);
        imgestado = v.findViewById(R.id.imgestado);

        PropositoDiarioRealizado proposito = propositos.get(i);

        lbldescripcion.setText(proposito.getDescripcion());
        lblfecha.setText("Fecha: " +proposito.getFecha());
        lblhora.setText("Hora: " + proposito.getHora());

        int img = R.drawable.diario_realiado;

        imgestado.setImageResource(img);


        return v;
    }
}

