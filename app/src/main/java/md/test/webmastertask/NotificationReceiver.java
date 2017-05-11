package md.test.webmastertask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class NotificationReceiver extends BroadcastReceiver {


    private static final int NOTIFY_ID = 101;
    private DB mDb;
    boolean ifStateNull;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Подключаем БД
        mDb = new DB(context);

        // Проверяем есть ли в БД ID, с параметром agree = 0
        Cursor myCursorScore = mDb.getIdFromAgree(0);
        while(myCursorScore.moveToNext()) {
            // Если таковой имеется - задаем переменной ifStateNull состояние true
            ifStateNull = true;
        }

        // Если у нас нашлись ID с неопределенным состоянием - то выводим Notification
        if (ifStateNull) {

            // Создаем Intent с адрессом NotificationActivity
            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            // Создаем PendingIntent
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            // Создаем builder
            Notification.Builder builder = new Notification.Builder(context);

            // Заносим соответствующий PendingIntent
            builder.setContentIntent(contentIntent);
            // Задаем иконку
            builder.setSmallIcon(R.drawable.icon_test);
            // Задаем главный текст
            builder.setContentTitle("Внимание");
            // Задаем милодию оповещения
//            builder.setDefaults(Notification.DEFAULT_SOUND);
            // Задаем мигание лампочки индентификатора (не работает)
//            builder.setDefaults(Notification.DEFAULT_LIGHTS);

            // Задаем  вторичный текст
            builder.setContentText("У вас новый заказ на сайте");
            // Задаем параметр, который отвечает за то чтоб Notification закрывался при нажатии
            // (почемуто если делать через BroadcastReceiver не работает)
            builder.setAutoCancel(true);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_INSISTENT;
            // параметы свеченияй лампочки индентификатора (не работает)
//            notification.ledARGB = Color.RED;
//            notification.ledOnMS = 1000;
//            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            // Включаем фонарик
            MainActivity.flashOn(context);
            // Включаем милодию
            MainActivity.mPlayerStart();

            // запускаем NotificationManager
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, notification);
        }

        // Для цикличности работы Service и Receiver перезапускаем Service
        context.stopService(new Intent(context.getApplicationContext(), MyService.class));
        context.startService(new Intent(context.getApplicationContext(), MyService.class));
    }


}