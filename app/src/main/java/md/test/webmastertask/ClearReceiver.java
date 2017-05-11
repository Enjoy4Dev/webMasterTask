package md.test.webmastertask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ClearReceiver extends BroadcastReceiver {


    private DB mDb;
    private static final String PUT_EXTRA_KEY_CLEAR_DB = "clearDB";
        @Override
        public void onReceive(Context context, Intent intent) {

            // Определяем бд
            mDb = new DB(context);
            // Получаем ID
            int id = intent.getIntExtra(PUT_EXTRA_KEY_CLEAR_DB, -1);

            // Выводим тост
            Toast.makeText(context, Integer.toString(id) + "был удален", Toast.LENGTH_SHORT).show();

            // Удаляем соответвующий ID
            mDb.deleteId(id);
        }



    }