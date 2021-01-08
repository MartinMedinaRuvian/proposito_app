package com.yolenith.proposito;

public class sql {

    public static final String tabla = "proposito";
    public static final String noId= "id";
    public static final String noTitulo = "titulo";
    public static final String noDescripcion = "descripcion";
    public static final String noFecha = "fecha";
    public static final String noHora = "hora";
    public static final String noEstado = "estado";
    public static final String noRepetir = "repetir";
    public static final String noTipo = "tipo";
    public static final String noValor = "valor";

    public static final String crearTablaNotas = "create table " + tabla + " ("
            + noId + " integer primary key, " + noTitulo + " text, " + noDescripcion + " text, " + noFecha + " text, "
            + noHora + " text, " + noEstado + " integer, " + noRepetir + " integer)";


    public static final String deleteTablaNotas = "drop table if exists " + tabla;



    public static final String tabla2 = "realizado";

    public static final String crearTablaNotas2 = "create table " + tabla2 + " ("
            + noId + " integer primary key, " + noDescripcion + " text, " + noFecha + " text, "
            + noHora + " text)";


    public static final String deleteTablaNotas2 = "drop table if exists " + tabla2;



    public static final String tabla3 = "dinero";
    public static final String crearTablaNotas3 = "create table " + tabla3 + " ("
            + noId + " integer primary key, " + noTitulo + " text, " + noDescripcion + " text, " + noFecha + " text, "
            + noHora + " text, " + noTipo + " integer, " + noRepetir + " integer, " + noValor + " float)";


    public static final String deleteTablaNotas3 = "drop table if exists " + tabla3;




    public static final String tabla4 = "realizado_dinero";

    public static final String crearTablaNotas4 = "create table " + tabla4 + " ("
            + noId + " integer primary key, " + noDescripcion + " text, " + noFecha + " text, "
            + noHora + " text, " + noValor + " float, " + noTipo + " integer)";


    public static final String deleteTablaNotas4 = "drop table if exists " + tabla4;


}



