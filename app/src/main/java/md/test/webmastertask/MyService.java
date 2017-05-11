package md.test.webmastertask;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyService extends Service {

    //Объявляем переменные
    private DB mDb;
    int checkForIteration, adminAgree;
    public static int dbSize;
    int[] idArray;
    Boolean hasOrders;
    static boolean needNotification;
    public static int arrayLength;

    private static final String PUT_EXTRA_KEY_CLEAR_DB = "clearDB";
    private static final int NO_MATCH_ID = -1;
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 60;
    private static final int NOTIFY_ID = 101;
    private CountDownTimer timer;
    private String strURL, strPass;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Адресс URL
        strURL = "https://cadourionline.md/ws/checker.php?";
        // Пароль URL
        strPass = "pass=CdRR3jb3gFaVLVtBIS5bAn8Ci097vq55p6Z9Ktcp";
        // Переменная, проверяющая есть ли заказы
        hasOrders = false;
        // Масив целых чисел, куда заносятся все ID до поступления в БД
        idArray = new int[10];
        // Определяем БД
        mDb = new DB(this);
//        mDb.deleteTable();

        new ParseTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Подлючаемся к URL
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // Получаем данные с внешнего ресурса
            try {
                URL url = new URL(strURL+strPass);
//                URL url = new URL("http://shatova.pro/test.html");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d("URL Строка", strJson);

            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);

                // Заносим в hasOrders информацию "Есть ли заказы"
                hasOrders = dataJsonObj.getBoolean("hasOrders");

                JSONArray ordersIds = dataJsonObj.getJSONArray("ordersIds");
                // Заносим в arrayLength количество поступивших ID заказов
                arrayLength = ordersIds.length();
                Log.i("arrayLength", Integer.toString(arrayLength));
                for (int i = 0; i < arrayLength; i++) {
                    // Заносим в массив idArray[i] соотвествующую ID
                    idArray[i] = ordersIds.getInt(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Запускаем метод fillDB
            fillDB();
        }
    }

    // В методе fillDB все данные заносятся во внутреннюю БД
    private void fillDB() {

        // Если есть заказ
        if (hasOrders) {
            // Если список ID не нулевой
            if (arrayLength > 0) {

                // Узнаем количество элементов в БД
                Cursor myCursorDbSize = mDb.getDbSize();
                while(myCursorDbSize.moveToNext()) {
                    dbSize = myCursorDbSize.getInt(myCursorDbSize.getColumnIndex("COUNT(*)"));
                }
                Log.i("dbSize 1", Integer.toString(dbSize));

                // Запускаем цикл по количеству поступивших ID
                for (int i = 0; i < arrayLength; i++) {

                    // Переменная для проверки на наличаи текущего ID в БД
                    checkForIteration = NO_MATCH_ID;

                    // Если БД не пустая
                    if (dbSize > 0) {
                        // Переменная boolean, которая показывает есть ли текущий ID в BD
                        boolean hasID = false;
                        // Запускаем второй цикл, который проверяет каждый поступившый ID на совпадение в БД
                        for (int ii = 0; ii < dbSize; ii++) {
                            Log.i("hasID 1", Boolean.toString(hasID));
                            // запускаем курсор, который достает ID из БД
                            Cursor myCursorScore = mDb.getAll(idArray[i]);
                            while (myCursorScore.moveToNext()) {
                                // Заносим его в переменную временного счета

                                checkForIteration = myCursorScore.getInt(myCursorScore.getColumnIndex("_id"));
                                adminAgree = myCursorScore.getInt(myCursorScore.getColumnIndex("agree"));

                                // Если есть совпадение по ID
                                if (idArray[i] == checkForIteration) {
                                    // Задаем переменной hasID значение true
                                    hasID = true;
                                }
                            }
                        }
                        // если текущего ID нету в БД
                        if (!hasID) {
                            // Заносим соотвествующий ID с параметром agree = 0 (означающим что этот ID еще в обработке)
                            mDb.insertId(idArray[i], 0);
                            // метод, удаляющий этот ID через сутки
                            clearDB(idArray[i]);
                            Log.i("all ok 2", Integer.toString(idArray[i]));
                        }
                    }
                    // Если БД пустая
                    else if (dbSize == 0) {
                        Log.i("i", Integer.toString(i));
                        // Заносим соотвествующий ID
                        mDb.insertId(idArray[i], 0);
                        // метод, удаляющий этот ID через сутки
                        clearDB(idArray[i]);
                        Log.i("all ok", Integer.toString(idArray[i]));
                    }
                }
                // После того как все ID были занесены в БД можно запускать метод checkNotification()
                checkNotification();
            }
        }
    }

    private void createNotification(){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon_test)
                .setContentTitle("Внимание")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)

//                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentText("У вас новый заказ на сайте");
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_INSISTENT;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    // Метод, удаляющий заданный ID спустя сутки после поступления
    private void clearDB(int orderID) {
        // Создаем AlarmManager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Создаем Intent со ссылкой на удаляющий Receiver
        Intent intent = new Intent(MyService.this, md.test.webmastertask.ClearReceiver.class);
        // заполняем pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent.putExtra(PUT_EXTRA_KEY_CLEAR_DB, orderID), PendingIntent.FLAG_CANCEL_CURRENT );
        am.cancel(pendingIntent);

        Log.i("clearDB", Integer.toString(orderID));
        // Запускаем AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
    }



    private void checkNotification() {
        // Создаем AlarmManager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Создаем Intent со ссылкой на NotificationReceiver
        Intent intent = new Intent(MyService.this, md.test.webmastertask.NotificationReceiver.class);
        // заполняем pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        am.cancel(pendingIntent);

        // Запускаем AlarmManager с задержкой в минуту
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + SECONDS_TO_COUNTDOWN * MILLIS_PER_SECOND, pendingIntent);
    }

    // метод для мгновенного показа Notification (вызывает много багов/на доработку)
    private void checkNotificationNow() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MyService.this, md.test.webmastertask.NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        am.cancel(pendingIntent);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }
}
