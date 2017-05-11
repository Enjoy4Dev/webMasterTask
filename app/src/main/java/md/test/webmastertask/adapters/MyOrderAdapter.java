package md.test.webmastertask.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import md.test.webmastertask.DB;
import md.test.webmastertask.MainActivity;
import md.test.webmastertask.NotificationActivity;
import md.test.webmastertask.models.Orders;
//import simpals.androidtest.Loader.BitmapCache;
//import simpals.androidtest.Loader.LoadImage;
import md.test.webmastertask.R;

import static md.test.webmastertask.NotificationActivity.alla;
//import simpals.androidtest.models.Tracks;


public class MyOrderAdapter extends BaseAdapter {

    public static String LOG_TAG = "my_log";

    private Activity activity;
    private Context context;
    private LayoutInflater inflater;
    private List<Orders> orderItems;
    private String activityKey;
    private static final String ACTIVITY_KEY_MAIN = "MainActivity";
    private static final String ACTIVITY_KEY_NOTIFICATION = "NotificationActivity";


    public String orderId = "Order Id";

    public MyOrderAdapter(Activity activity, List<Orders> orderItems, Context myContext, String myActivityKey) {
        this.activity = activity;
        this.orderItems = orderItems;
        this.context = myContext;
        this.activityKey = myActivityKey;
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int location) {
        return orderItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        // Определяе View
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.order_view, null);

        // Подключаем список песен
        Orders m = orderItems.get(position);


//        NotificationActivity.ololo();


//        Context context = MainActivity.getApplicationContext();


        // Задаем значения переменным
        orderId         = String.valueOf(m.getOrderId());


        final DB myDb = new DB(context);

        // Определяем и наплняем View
        TextView tvNewOrder = (TextView) convertView.findViewById(R.id.tv_new_order);
        TextView tvId = (TextView) convertView.findViewById(R.id.order_id);
        tvId.setText(orderId);
        Button btnYes = (Button) convertView.findViewById(R.id.btn_yes);
        Button btnNo = (Button) convertView.findViewById(R.id.btn_no);
        if (activityKey == ACTIVITY_KEY_MAIN){
            tvNewOrder.setText("У Вас заказ под номером");
            btnNo.setVisibility(View.GONE);
            btnYes.setVisibility(View.GONE);
        }
//        btnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                alla = Integer.parseInt(orderId);
//                Log.i("dbSize1", Integer.toString(alla));
//            }
//        });
        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Orders my = orderItems.get(position);
                NotificationActivity.onItemClick(v, (Integer.parseInt(String.valueOf(my.getOrderId()))), myDb, MyOrderAdapter.this, context);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Orders my = orderItems.get(position);
                NotificationActivity.onItemClick(v, (Integer.parseInt(String.valueOf(my.getOrderId()))), myDb, MyOrderAdapter.this, context);
            }
        });

//        convertView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((ListView) parent).performItemClick(v, position, 0);
//            }
//        });
//        TextView tvPrice = (TextView) convertView.findViewById(R.id.view_track_price);
//        tvPrice.setText(price);

//        progressBar = (ProgressBar) convertView.findViewById(R.id.view_track_progressbar);

        // Загружаем изображение из URL
//        trackImage = (ImageView) convertView.findViewById(R.id.view_track_image);
//        BitmapCache mBitmapCache = new BitmapCache(Context.CONTEXT_IGNORE_SECURITY);
//        new LoadImage(mBitmapCache, trackImage, progressBar, ImageUrl).execute();

        return convertView;
    }

}