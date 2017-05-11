package md.test.webmastertask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import md.test.webmastertask.adapters.MyOrderAdapter;
import md.test.webmastertask.models.Orders;

public class NotificationActivity extends AppCompatActivity {

    //Объявляем переменные
    Button btnMain;
    public static RelativeLayout tvMain;
    public static DB mDb;
    public static int dbSize;
    public  static ListView myLv;
    private MyOrderAdapter adapter;
    public static List<Orders> orderList = new ArrayList<>();
    public static int alla;
    public static String request;

    private static final String ACTIVITY_KEY_NOTIFICATION = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Объявляем БД
        mDb = new DB(this);

        // Определяем элементы activity_notification
        btnMain = (Button) findViewById(R.id.btn_orders);
        myLv = (ListView) findViewById(R.id.lvSimple);
        tvMain = (RelativeLayout) findViewById(R.id.tv_main);

        // устанавливаем ClickListener для главной кнопки
        btnMain.setOnClickListener(btnMainOnClickListener);

        // Получаем context текущей activity
        Context context = getApplicationContext();

        // Определяем адаптер для текущих заданий
        adapter         = new MyOrderAdapter(NotificationActivity.this, orderList, context, ACTIVITY_KEY_NOTIFICATION);

        // Перед наполнением очищаем ListView
        orderList.clear();
        // Достаем все ID из БД, где статус принятия занадия неопределен (0)
        Cursor myCursorScores = mDb.getIdFromAgree(0);
        while(myCursorScores.moveToNext()) {
            tvMain.setVisibility(View.GONE);
            Orders orderAA = new Orders();
            // Заполняем ListView соответстующим ID
            orderAA.setOrderId(Integer.toString(myCursorScores.getInt(myCursorScores.getColumnIndex("_id"))));
            orderList.add(orderAA);
        }

        // Задаем Adapter для соответствующего ListView
        myLv.setAdapter(adapter);

    }


    // Проверка, на которую из кнопок нажал пользователь
    public static void onItemClick(View view, int id, DB db, MyOrderAdapter myAdapter, Context context) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_yes:
                btnClickListener(id, 1, db,context,myAdapter);
                break;
            case R.id.btn_no:
                btnClickListener(id, -1, db,context,myAdapter);
                break;

            default:
                break;
        }
    }
    // Обработчик кнопок "Принять" и "Отказать"
    private static void btnClickListener (int id, int state, DB db, Context context, MyOrderAdapter myAdapter){
        // Заносим в БД соответсвующему ID статус принятия задания (зависит от того, какая кнопка была нажата)
        db.setAgree(id, state);
        Log.i("db Insert", Integer.toString(id));
        // Перед наполнением очищаем ListView
        orderList.clear();
        // Переменная, которая проверяет есть ли еще необработанные ID
        boolean ifArrayEmpty = false;
        // Проверяем каждый ID, где статус принятия неопределен (0)
        Cursor myCursorBtnYes = db.getIdFromAgree(0);
        while(myCursorBtnYes.moveToNext()) {
            tvMain.setVisibility(View.GONE);
            Orders orderAA = new Orders();
            // Заполняем ListView соответстующим ID
            orderAA.setOrderId(Integer.toString(myCursorBtnYes.getInt(myCursorBtnYes.getColumnIndex("_id"))));
            orderList.add(orderAA);
            ifArrayEmpty = true;
        }
        // Если ListView пуст - выключаем фонарик и медолдию
        if (!ifArrayEmpty){
            tvMain.setVisibility(View.VISIBLE);
            // Выключаем камеру
            Camera camera = Camera.open();
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();


            // Заносим в переменную dbSize количество ID в нашей БД
            Cursor myCursorDbSize = mDb.getDbSize();
            while(myCursorDbSize.moveToNext()) {
            // Заносим его в переменную временного счета
            dbSize = myCursorDbSize.getInt(myCursorDbSize.getColumnIndex("COUNT(*)"));
            Log.i("dbSize aa", Integer.toString(dbSize));
            }
            // Выключаем музыку
            for (int i = 0; i < dbSize; i++) {
                MainActivity.mPlayerStop();
            }
        }
        // в зависимости от того, какую кнопку нажали - задаем соответсвующий текст для Toast
        if (state == 1){request = " принят";}
        if (state == -1){request = " отклонен";}

        Toast.makeText(context, "Заказ " + id + request,
                Toast.LENGTH_SHORT).show();
        // Включаем Adapter
        myLv.setAdapter(myAdapter);
    }

    // Обработчик нажатия главной кнопки
    private View.OnClickListener btnMainOnClickListener = new View.OnClickListener() {

        public void onClick(View view) {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
