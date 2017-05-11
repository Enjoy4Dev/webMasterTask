package md.test.webmastertask;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import md.test.webmastertask.adapters.MyOrderAdapter;
import md.test.webmastertask.models.Orders;

public class MainActivity extends AppCompatActivity {

    //Объявляем переменные
    Button btnOrders;
    RelativeLayout tvMain;
    private DB mDb;
    public static int dbSize;
    private  ListView orderLV;
    private MyOrderAdapter adapter;
    public static List<Orders> orderList = new ArrayList<>();
    private static final String ACTIVITY_KEY_MAIN = "MainActivity";
    public static MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toast.makeText(this, "Приложение работает\nв фоновом режиме",
//                Toast.LENGTH_SHORT).show();

        // Влючаем сервис MyService
        startService(
                new Intent(MainActivity.this, MyService.class));

        // Просим разрешение на использование камеры (для включения фонарика)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);

        // Подключаем базу данных
        mDb = new DB(this);

        // Подключаем MediaPlayer
        mPlayer = MediaPlayer.create(this, R.raw.tone);
        mPlayer.setLooping(true);

        // Определяем элементы activity_main
        btnOrders = (Button) findViewById(R.id.btn_main);
        orderLV = (ListView) findViewById(R.id.lvSimple);
        tvMain = (RelativeLayout) findViewById(R.id.tv_main);

        // Получаем context текущей activity
        Context context = getApplicationContext();

        // Определяем адаптер для принятых заданий
        adapter         = new MyOrderAdapter(MainActivity.this, orderList, context, ACTIVITY_KEY_MAIN);

        // Даем кнопке ClickListener
        btnOrders.setOnClickListener(btnOrdersOnClickListener);



//        finish();
    }
    // Метод для наполнения ListView данными
    private void updateListView() {
        // Узнаем количество элементов в БД
        Cursor myCursorDbSize = mDb.getDbSize();
        while(myCursorDbSize.moveToNext()) {
            dbSize = myCursorDbSize.getInt(myCursorDbSize.getColumnIndex("COUNT(*)"));
            Log.i("dbSize", Integer.toString(dbSize));
        }

        // Если БД не пуста то наполняем ListView данными из БД
        if (dbSize != 0) {
            orderList.clear();
            Cursor myCursorScores = mDb.getIdFromAgree(1);
            while (myCursorScores.moveToNext()) {
                tvMain.setVisibility(View.GONE);
                Orders orderAA = new Orders();
                orderAA.setOrderId(Integer.toString(myCursorScores.getInt(myCursorScores.getColumnIndex("_id"))));
                orderList.add(orderAA);
            }

            orderLV.setAdapter(adapter);
        }
        else
            tvMain.setVisibility(View.VISIBLE);
    }

    // Создаем ClickListener для главной кнопки
    private View.OnClickListener btnOrdersOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
//            finish();
        }
    };


    // Публичный метод включения звукового оповещения
    public static void mPlayerStart(){
        mPlayer.start();
        Log.i("mPlayerStart", "mPlayerStart");
    }
    // Публичный метод выключения звукового оповещения
    public static void mPlayerStop(){
        mPlayer.stop();
        Log.i("mPlayerStop", "mPlayerStop");
    }


    // Метод для мигания фонарика (вызывает много багов, на доработку)

//    public static void flashOnOff(final Context context, Boolean onOff) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            if (onOff) {
//                //ask for authorisation
//                {
//                    Log.i("flashOn", "flashOn");
//                    Camera camera = Camera.open();
//                    Camera.Parameters p = camera.getParameters();
//                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                    camera.setParameters(p);
//                    camera.startPreview();
//                }
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        Log.i("flashOn", "flashOn");
//                        Camera camera = Camera.open();
//                        Camera.Parameters p = camera.getParameters();
//                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                        camera.setParameters(p);
//                        camera.startPreview();
//                    }
//                }, 1000);
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        flashOnOff(context, true);
//                    }
//                }, 1000);
//            }
//            if (!onOff){
//                Log.i("flashOff", "flashOff");
//                Camera camera = Camera.open();
//                Camera.Parameters p = camera.getParameters();
//                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                camera.setParameters(p);
//                camera.startPreview();
//                return;
//            }
//        }
//
//    }

    // Публичный метод для включения фонарика
    public static void flashOn(Context context) {
        // Проверка на получение доступа к камере
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //ask for authorisation
                Log.i("flashOn", "flashOn");
                Camera camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
        }
}

    @Override
    protected void onStart() {
        updateListView();
        super.onStart();
    }

    @Override
    protected void onResume() {
        updateListView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

}
