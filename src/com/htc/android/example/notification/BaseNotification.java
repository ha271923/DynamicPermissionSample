package com.htc.android.example.notification;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

abstract public class BaseNotification {

    private final static String TAG = "BaseNotification";
    
    public interface NotificationType {
        public static final int REQUEST_PERMISSION_TYPE = 1;
    }
    
    protected int mType;
    protected int mAccountId;
    protected Uri mNotifyUri;
    protected long  mRowId;
    protected String mTitle;
    protected String mDesc;
    protected String mTicker;
    protected Intent mTarget;
    protected long mCurrTime;
    protected Context mNotifyContext;
    
    protected int mIconResource;
    
    public void setContext(Context context) {
        mNotifyContext = context;
    }
    
    public static  Notification.Builder getBuilder(Context context) {
        return new Notification.Builder(context);
    }
}
