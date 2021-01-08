package com.yolenith.proposito;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class helpBdd extends SQLiteOpenHelper {
    sql s = new sql();

    public helpBdd(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(s.crearTablaNotas);
        db.execSQL(s.crearTablaNotas2);
        db.execSQL(s.crearTablaNotas3);
        db.execSQL(s.crearTablaNotas4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(s.deleteTablaNotas);
        db.execSQL(s.crearTablaNotas);

        db.execSQL(s.deleteTablaNotas2);
        db.execSQL(s.crearTablaNotas2);

        db.execSQL(s.deleteTablaNotas3);
        db.execSQL(s.crearTablaNotas3);

        db.execSQL(s.deleteTablaNotas4);
        db.execSQL(s.crearTablaNotas4);


    }
}
