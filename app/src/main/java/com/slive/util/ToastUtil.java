package com.slive.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slive.fileextractor.R;

/**
 * Created by Administrator on 2017/5/6.
 */
public class ToastUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());
    public static void show(Context context, String message) {
        showMessage(context,message);
    }

    public static void show(Context context, int resId){
        String message=context.getResources().getString(resId);
        showMessage(context,message);
    }

    private static void showMessage(final Context context, final String message){

        handler.post(new Runnable() {
            @Override
            public void run() {
                View toastRoot = LayoutInflater.from(context).inflate(R.layout.item_toast, null);
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastRoot);
                TextView tv = toastRoot.findViewById(R.id.item_toast_tv_toast);
                tv.setText(message);
                toast.show();
            }
        });

    }
}
