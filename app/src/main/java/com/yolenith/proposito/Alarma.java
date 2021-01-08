package com.yolenith.proposito;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class Alarma extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;


    private static final String CERO = "0";


    helpBdd h;
    private Cursor fila, filaRepetir, filaDinero;
   // public static int idNotificacion;
    private String descripcion,titulo;

    public static String CHANNEL_ID = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        crearServicio(context);
        verificarAlarma(context);
    }


    private void crearServicio(Context context){
        Intent intentService = new Intent(context, MyTestService.class);
        context.startService(intentService);
    }

    private void verificarAlarma(Context context){
        h = new helpBdd(context, "bdd_proposito", null, 1);
        SQLiteDatabase db = h.getReadableDatabase();

        Calendar calendario = Calendar.getInstance();
        int hora, min,dia,mes,ano;

        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        ano = calendario.get(Calendar.YEAR);
        hora = calendario.get(Calendar.HOUR_OF_DAY);
        min = calendario.get(Calendar.MINUTE);

        //Formateo el hora obtenido: antepone el 0 si son menores de 10
        String horaFormateada =  (hora < 10)? String.valueOf(CERO + hora) : String.valueOf(hora);
        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
        String minutoFormateado = (min < 10)? String.valueOf(CERO + min):String.valueOf(min);

        String hoy = dia + "/" + (mes+1) + "/" + ano;
        String horaActual = horaFormateada + ":" + minutoFormateado;



        if(db!=null) {

            String sql_repetir = "SELECT id, titulo, descripcion FROM proposito WHERE estado='0' AND repetir='1' AND hora='"+horaActual+"'";


            filaRepetir = db.rawQuery(sql_repetir, null);

            //COMPRUEBO SI ES UN PROPOSITO DIARIO

            if(filaRepetir.moveToFirst()){

              int idNotificacion =filaRepetir.getInt(0);
               titulo=filaRepetir.getString(1) + "\n";
                descripcion =filaRepetir.getString(2);
                notificar(context, titulo, descripcion, idNotificacion);
            }

            else{

                String  sql = "SELECT id, titulo, descripcion FROM proposito WHERE  fecha='"+hoy+"' AND estado='0' AND hora='"+horaActual+"'";

                fila = db.rawQuery(sql, null);

                if(fila.moveToFirst()){
                   int idNotificacion=fila.getInt(0);
                    titulo=fila.getString(1) + "\n";
                    descripcion =fila.getString(2);
                    notificar(context, titulo, descripcion,idNotificacion);
                }

            }

            /*
            VERIFICAR DINERO FIJO PENDIENTE
             */
            String  sql_dinero = "SELECT id, titulo, descripcion FROM dinero WHERE  fecha='"+hoy+"' AND hora='"+horaActual+"' AND repetir='1'";
            filaDinero = db.rawQuery(sql_dinero, null);
            if (filaDinero.moveToFirst()){
               int idNotificacion=filaDinero.getInt(0);
                titulo=fila.getString(1) + "\n";
                descripcion =filaDinero.getString(2);
                notificar(context, titulo, descripcion, idNotificacion);
            }
        }


        db.close();

    }



    private void notificar(Context context, String titulo, String descripcion, int idNotificacion){
        createNotificationChannel(context, idNotificacion);
        crearNotificacion(context, titulo, descripcion, idNotificacion);
    }


    private Intent ver(Context context, int idNotificacion, boolean propositoUnico){
        Intent intent = new Intent(context, ver.class);
        intent.putExtra("id_notificacion", idNotificacion);
        intent.putExtra("hoy", true);
        intent.putExtra("enEspera", true);
        intent.putExtra("proposito_unico", propositoUnico);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private PendingIntent verPropositoUnico(Context context, int idNotificacion){
        return PendingIntent.getActivity(context, 100, ver(context, idNotificacion, true), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent verPropositos(Context context, int idNotificacion){
        return PendingIntent.getActivity(context, 100, ver(context, idNotificacion, false), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void crearNotificacion(Context context, String titulo, String descripcion, int idNotificacion){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        String descripcionMostrar = descripcion.toUpperCase();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);

        builder.setSmallIcon(R.drawable.icono_yolenith);
        //  builder.setContentTitle(titulo);
        builder.setContentText(descripcionMostrar);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(descripcionMostrar));
        builder.setColor(Color.BLACK);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(verPropositos(context, idNotificacion));
        builder.addAction(R.drawable.estado_0, "Ver todos", verPropositos(context, idNotificacion));
        builder.addAction(R.drawable.estado_2, "Ver proposito", verPropositoUnico(context, idNotificacion));
        builder.setAutoCancel(true);
        notificationManager.notify(idNotificacion, builder.build());
    }



    private void crearNotificacionChannel(Context context, int idNotificacion){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = String.valueOf(idNotificacion);
            CHANNEL_ID = String.valueOf(idNotificacion);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =  (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotificationChannel(Context context, int idNotificacion) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = String.valueOf(idNotificacion);
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
