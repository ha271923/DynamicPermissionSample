package com.htc.android.example;

import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

import android.Manifest;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.util.ArrayMap;

import com.htc.android.example.activity.BaseActivity;
import com.htc.android.example.notification.RequestPemissionNotification;
import com.htc.android.example.util.RequestPermissionUtil;
import com.htc.android.example.util.RequestPermissionUtil.PendingPermissionTask;

public class Activity1 extends BaseActivity {
    
    private static final String TAG = "Activity1";
    
    private Context mContext;
    
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.main_activity_1);
        
        mContext = this;
        
        // Activity
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(); 
                intent.setClass(mContext, Activity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        
        // Background Service
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] desiredPermission = new String[] {
                    // Contacts group
                    android.Manifest.permission.READ_CONTACTS, 
                    
                    // Calendar
                    android.Manifest.permission.READ_CALENDAR,
                };
                RequestPemissionNotification.showNotificationInLine(mContext, desiredPermission);
            }
        });
        
        // dynamic request permission btn Service
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String desiredPermission = Manifest.permission.CALL_PHONE;
                int requestCode = RequestPermissionUtil.RequestCode.PERMISSION_REQUEST_CALL_PHONE;
                PendingPermissionTask task = new ActionCallRunnable(Activity1.this);
                String description = "call phone";
                
                boolean hasPermission = handleRequestPermission(new WeakReference<Activity>(Activity1.this), desiredPermission, requestCode, task, description);
                if (!hasPermission) {
                    Log.d(TAG, "Return, doesn't have permision:" + desiredPermission);
                    return;
                 }
                showActionCallToast(Activity1.this);
            }
        });
    }
    
    private static class ActionCallRunnable extends PendingPermissionTask {
        
        private WeakReference<Activity> mWeakActivity;
        
        public ActionCallRunnable(Activity act) {
            mWeakActivity = new WeakReference<Activity>(act);
        }

        @Override
        public void run() {
            Log.d(TAG, "ActionCallRunnable starts");
            
            if (mWeakActivity == null) {
                Log.d(TAG, "mWeakActivity is null");
                return;
            }
            
            Activity target = mWeakActivity.get();
            if (target == null || target.isFinishing() || target.isDestroyed()) {
                Log.d(TAG, "target is null");
                return;
            }
            
            Toast.makeText(target, "request call_phone_permission ok", Toast.LENGTH_SHORT).show();
        }
    }
    
    private static void showActionCallToast (Context context) {
        Toast.makeText(context, "Have CALL_PHONE Permission", Toast.LENGTH_SHORT).show();
    } 
}
