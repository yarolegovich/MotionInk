package com.yarolegovich.motionink.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class Permissions {

    private static final int REQUEST_PERMISSION = 7;

    private Activity activity;

    private PermittedAction permittedAction;

    public Permissions(Activity activity) {
        this.activity = activity;
    }

    public void doIfPermitted(PermittedAction permittedAction, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            this.permittedAction = permittedAction;
            ActivityCompat.requestPermissions(
                    activity, new String[]{permission},
                    REQUEST_PERMISSION);
        } else {
            permittedAction.doAction();
        }
    }

    public void handleGrantResults(int[] grantResults) {
        int grantedResult = grantResults[0];
        if (grantedResult == PackageManager.PERMISSION_GRANTED) {
            if (permittedAction != null) {
                permittedAction.doAction();
            }
        }
    }

    public interface PermittedAction {
        void doAction();
    }

}
