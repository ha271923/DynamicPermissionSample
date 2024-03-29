package com.htc.android.example.activity;

import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;
import android.util.ArrayMap;
import android.widget.Toast;

import com.htc.android.example.activity.permission.RequestPermissionsActivity;
import com.htc.android.example.util.RequestPermissionUtil;
import com.htc.android.example.util.RequestPermissionUtil.PendingPermissionTask;
import com.htc.android.example.util.RequestPermissionUtil.RuntimePermissionHelper;

public class BaseActivity extends Activity implements RuntimePermissionHelper {
    
    private static final String TAG = "BaseActivity";
    
    private boolean[] mIsRequestingPermission = new boolean[RequestPermissionUtil.PERMISSION_REQUEST_COUNT]; // default all false
    
    private ArrayMap<Integer, LinkedList<PendingPermissionTask>> mPendingPermissionMap = new ArrayMap<Integer, LinkedList<PendingPermissionTask>>();
    
    /**
     * @return list of permissions that are needed in order for {@link #PREVIOUS_ACTIVITY_INTENT} to
     * operate. You only need to return a single permission per permission group you care about.
     */
    protected String[] getRequiredPermissions() {
        return null;
    };
    
    /**
     * @return list of permissions that would be useful for {@link #PREVIOUS_ACTIVITY_INTENT} to
     * operate. You only need to return a single permission per permission group you care about.
     */
    protected String[] getDesiredPermissions() {
        return null;
    }
    
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        if (RequestPermissionsActivity.startPermissionActivity(this, getRequiredPermissions(), getDesiredPermissions())) {
           return;
        }
    }
    
    /*
     * RuntimPermission model implements starts
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
       Log.d(TAG, "onRequestPermissionsResult, requestCode: " + requestCode + ", permissions: " + Arrays.toString(permissions) + ", grantResults: " + Arrays.toString(grantResults));
        
        LinkedList<PendingPermissionTask> list = mPendingPermissionMap.get(requestCode);
        if (list == null || list.isEmpty()) {
           Log.d(TAG, "There's no pending task of requestCode: " + requestCode);
        } else {
            PendingPermissionTask task = list.removeFirst();
            if (isAllGranted(permissions, grantResults) && task != null) {
                task.run();
            }
        }
        
        setIsRequestingPermission(requestCode, false);
    }
    
    private boolean isAllGranted(String permissions[], int[] grantResult) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResult[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void addPendingPermissionTask(int requestCode, PendingPermissionTask task) {
        LinkedList<PendingPermissionTask> list = mPendingPermissionMap.get(requestCode);
        if (list == null) {
            list = new LinkedList<PendingPermissionTask>();
            mPendingPermissionMap.put(requestCode, list);
        }
        list.add(task);
    }
    
    @Override
    public boolean isRequestingPermission(int requestCode) {
        if (checkBoundary(requestCode)) {
            return mIsRequestingPermission[requestCode];
        }
        return false;
    }
    
    @Override
    public void setIsRequestingPermission(int requestCode, boolean isRequesting) {
        if (checkBoundary(requestCode)) {
            mIsRequestingPermission[requestCode] = isRequesting;
        }
    }
    
    private boolean checkBoundary(int index) {
        if (index >= 0 && index < mIsRequestingPermission.length) {
            return true;
        }
        
       Log.e(TAG, "checkBoundary failed, index: " + index);
        return false;
    }
    
    
    public boolean handleRequestPermission(WeakReference<Activity> weakActivity, String desiredPermission, int requestCode, PendingPermissionTask task, String description) {
        Activity target = weakActivity.get();
        if (target == null || target.isFinishing() || target.isDestroyed()) {
           Log.d(TAG, "target is null");
            return false;
        }
        
        if (target.checkSelfPermission(desiredPermission) != PackageManager.PERMISSION_GRANTED) {
            if (target instanceof RuntimePermissionHelper) {
                if (((RuntimePermissionHelper) target).isRequestingPermission(requestCode)) {
                   Log.d(TAG, "Already request permission:" + desiredPermission);
                } else {
                    ((RuntimePermissionHelper) target).addPendingPermissionTask(requestCode, task);
                    
                    // The toast will show up if user want to dynamic request permission when click some button
                    // App needs to make decision to add it or not.  
                    if (target.shouldShowRequestPermissionRationale(desiredPermission)) {
                        Toast.makeText(target, "Needs " + desiredPermission + " to " + description +" directly.", Toast.LENGTH_SHORT).show();
                    }
                    
                    target.requestPermissions(new String[] { desiredPermission }, requestCode);
                    ((RuntimePermissionHelper) target).setIsRequestingPermission(requestCode, true);
                }
            } else {
               Log.e(TAG, "Need implements RuntimePermissionHelper to handle runtime permission: " + desiredPermission);
            }
            return false;
        }
        return true;
    }
    
    /*
     * RuntimPermission model implements ends
     */
}
