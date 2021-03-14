package com.practica2.practica3widgetaudio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //variables de los objetos
    Button btn_pathsong,btn_createwidget,btn_back,btn_next,btn_save;
    TextView txt_count,txt_pathsong,txt_pathimg;
    ImageView img_path;
    EditText inp_pathsong;

    //varibales para la respuesta
    final static int REQUEST_IMG = 100;
    final static int REQUEST_SONG = 101;

    String pathsong = "";
    String pathimg = "";
    int count = -1;

    int count_verify = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //asignando id de cada objeto correspondiente
        btn_back = findViewById(R.id.buttonBack);
        btn_next = findViewById(R.id.buttonNext);
        btn_createwidget = findViewById(R.id.buttonCreate);
        btn_pathsong = findViewById(R.id.buttonSong);
        btn_save = findViewById(R.id.buttonSave);

        txt_count = findViewById(R.id.textViewcount);
        txt_pathimg = findViewById(R.id.textViewpathimg);
        txt_pathsong = findViewById(R.id.textViewpathsong);

        img_path = findViewById(R.id.imageViewactivty);


        inp_pathsong = findViewById(R.id.inputsong);

        txt_pathsong.setText("pathsong = "+ pathsong);
        txt_pathimg.setText("pathimg = "+ pathimg);
        txt_count.setText("count = "+count_verify);


        btn_next.setOnClickListener(v -> {
            if(count_verify<=4){
                count_verify++;
            }else
                count_verify = 0;


            SharedPreferences imgs = getSharedPreferences("img",Context.MODE_PRIVATE);
            String getimg = imgs.getString("img"+count_verify,"none");

            SharedPreferences song = getSharedPreferences("music",Context.MODE_PRIVATE);
            String getSong = song.getString("song"+count_verify,"none");
            if(!getimg.equals("none")){
                Bitmap bitmap = dimencion(pathimg);
                img_path.setImageBitmap(bitmap);

                txt_count.setText("count: " +count_verify);
                txt_pathimg.setText("pathimg: "+getimg);
                txt_pathsong.setText("pathsong: "+getSong);
            }

        });


        btn_back.setOnClickListener(v -> {
            if(count_verify>=0){
                count_verify--;
            }else
                count_verify = 4;


            SharedPreferences imgs = getSharedPreferences("img",Context.MODE_PRIVATE);
            String getimg = imgs.getString("img"+count_verify,"none");

            SharedPreferences song = getSharedPreferences("music",Context.MODE_PRIVATE);
            String getSong = song.getString("song"+count_verify,"none");
            if(!getimg.equals("none")){
                Bitmap bitmap = dimencion(pathimg);
                img_path.setImageBitmap(bitmap);

                txt_count.setText("count: " +count_verify);
                txt_pathimg.setText("pathimg: "+getimg);
                txt_pathsong.setText("pathsong: "+getSong);
            }

        });






        btn_pathsong.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,REQUEST_SONG);
        });

        img_path.setOnClickListener(v -> {
            Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,REQUEST_IMG);
        });

        btn_save.setOnClickListener(v -> {
            if((!pathsong.equals(""))&&(!pathimg.equals(""))){
                if(count<=4){
                    count++;
                }else{
                    count = 0;
                }
                //guardando la imagen
                SharedPreferences sharedPreferences = getSharedPreferences("img",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("img"+count,pathimg);
                editor.commit();

                //guardando la rolonga
                SharedPreferences sharedPreferences2 = getSharedPreferences("music",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString("song"+count,pathsong);
                editor2.commit();

                pathsong = "";
                pathimg = "";
            }else
                Toast.makeText(this,"eliga todas las rutas plox cabeza de huevo",Toast.LENGTH_SHORT).show();
        });

        btn_createwidget.setOnClickListener(v -> {
            AppWidgetManager widgetManager = getApplicationContext().getSystemService(AppWidgetManager.class);
            ComponentName componentName = new ComponentName(getApplicationContext(),widget.class);
            if(widgetManager.isRequestPinAppWidgetSupported()){
                widgetManager.requestPinAppWidget(componentName,null,null);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();

        if(resultCode == RESULT_OK && requestCode == REQUEST_IMG){
            pathimg = getRealPathFromURI(uri);
            Bitmap bitmap = dimencion(pathimg);
            img_path.setImageBitmap(bitmap);
            Toast.makeText(this,"imagen: " + pathimg,Toast.LENGTH_SHORT).show();

        }else{
            if(resultCode == RESULT_OK && requestCode == REQUEST_SONG){
                pathsong = getRealPathFromURI(uri);
                Toast.makeText(this,"cancion: " + pathsong,Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this,"no seleciono ni madres",Toast.LENGTH_SHORT).show();
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {

            result = contentURI.getPath();
        } else
        {
            cursor.moveToFirst(); int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); result = cursor.getString(idx); cursor.close();
        }
        return result;
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