package com.practica2.practica3widgetaudio;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.widget.RemoteViews;

import java.io.IOException;

public class widget extends AppWidgetProvider {
    //variables de las acciones a realizar con los botones
    final static String action_play = "action_play";
    final static String action_right = "cambiar";
    final static String action_stop = "action_stop";

    private static int count = 0;
    private static int update = 0;
    private static MediaPlayer mediaPlayer = new MediaPlayer();;


    String pathSong ="";

    @Override
    public void onUpdate(Context context, AppWidgetManager widgetManager,int[] appWidgetIds){
        final int arraySize = appWidgetIds.length;

        for(int i =0;i<arraySize;i++){

            int appwidgetid = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.mi_widget);

            //para la accion del primer boton de play
            Intent intent_play = new Intent(context,widget.class);
            intent_play.setAction(action_play);
            PendingIntent pendingIntent_play = PendingIntent.getBroadcast(context,0,intent_play,0);
            views.setOnClickPendingIntent(R.id.btn_play,pendingIntent_play);

            //para el boton de stop
            Intent intent_stop = new Intent(context,widget.class);
            intent_stop.setAction(action_stop);
            PendingIntent pendingIntent_stop = PendingIntent.getBroadcast(context,0,intent_stop,0);
            views.setOnClickPendingIntent(R.id.btn_stop,pendingIntent_stop);

            if(update == 1){
                SharedPreferences img_preference = context.getSharedPreferences("img",Context.MODE_PRIVATE);
                String get = img_preference.getString("img"+String.valueOf(count),"");
                Bitmap bitmap = dimencion(get);
                views.setImageViewBitmap(R.id.img_widget,bitmap);
                views.setTextViewText(R.id.txt_widget,"Count: "+String.valueOf(count));
                update = 0;
            }

            widgetManager.updateAppWidget(appwidgetid,views);
        }
    }

    //nuestros metodos de cada accion de nuestros botones
    public void met_btn_play(Context context){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(),getClass().getName());

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("music",Context.MODE_PRIVATE);
            pathSong = sharedPreferences.getString("song"+count,"");

            mediaPlayer.reset();
            update = 1;
            mediaPlayer.setDataSource(pathSong);
            mediaPlayer.prepare();
            mediaPlayer.start();
            onUpdate(context,appWidgetManager,appWidgetIds);


        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public void met_btn_stop(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        mediaPlayer.stop();
        mediaPlayer.reset();


        onUpdate(context,appWidgetManager,appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context,intent);
        if(action_play.equals(intent.getAction())){
            met_btn_play(context);
        }

        if(action_stop.equals(intent.getAction())){
            met_btn_stop(context);
        }

    }
    public Bitmap dimencion(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        int img_width=options.outWidth;
        int img_height = options.outHeight;
        int ratio;
        options.inJustDecodeBounds = false;
        if(img_width>img_height){
            ratio = img_width/250;
        }else{
            ratio = img_height/250;
        }
        options.inSampleSize = ratio;
        return BitmapFactory.decodeFile(path,options);
    }

}
