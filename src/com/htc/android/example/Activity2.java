package com.htc.android.example;

import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.htc.android.example.activity.BaseActivity;

public class Activity2 extends BaseActivity {
    
    private static final String TAG = "Activity2";
    
    @Override
    protected String[] getRequiredPermissions() {
        return new String[] {
                // Contacts group
                android.Manifest.permission.READ_CONTACTS, // Contacts display name, EmailAddressAdapter
                
                // Calendar
                android.Manifest.permission.READ_CALENDAR, // Propose meeting
                
                // Storage group
                android.Manifest.permission.READ_EXTERNAL_STORAGE, // Show Attachment 
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // Save Attachment
                
        };
    }
    
    @Override
    protected String[] getDesiredPermissions() {
        return new String[] {
                // Contacts group
                android.Manifest.permission.READ_CONTACTS, 
                
                // Calendar
                android.Manifest.permission.READ_CALENDAR,
                
                // Storage group
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
    }
    
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.main_activity_2);
    }
}
