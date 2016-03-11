package com.prashant.android.runtimepermissionhandler;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * @author Prashant Singh
 */
public class PermissionHandler {
    /**
     * list for storing single/multi permissions
     */
    private List<String> permissionsList;
    private Activity activity;
    /**
     * instance of callback interface for delegating the permission states
     */
    private PermissionCallback permissionInterface;

    /**
     * constructor supplied with activity reference
     *
     * @param activity
     */
    public PermissionHandler(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    private static String[] getStringArray(List<String> stringList) {
        return stringList.toArray(new String[stringList.size()]);
    }

    /**
     * method to add permission. The same method can be called again for adding more permissions
     *
     * @param permission
     * @return
     */
    public PermissionHandler addPermission(String permission) {
        if (permissionsList == null) {
            permissionsList = new ArrayList<>();
        }
        if (!permissionsList.contains(permission)) {
            permissionsList.add(permission);
        }
        return this;
    }

    /**
     * method checks if the permission in the permissionsList are not already granted, if
     * granted, it removes the permission from the permissionsList
     *
     * @return
     */
    private boolean checkPermission() {
        List<String> permissionsList = new ArrayList<>(this.permissionsList);
        boolean status = true;
        for (String permission : permissionsList) {
            if (checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                status = false;
            } else {
                this.permissionsList.remove(permission);
            }
        }
        return status;
    }

    /**
     * method to start requesting the permissions
     *
     * @param permissionInterface
     */
    public void build(PermissionCallback permissionInterface) {
        if (permissionsList == null) {
            return;
        }
        this.permissionInterface = permissionInterface;
        if (checkPermission()) {
            if (this.permissionInterface != null) {
                this.permissionInterface.permissionAccepted();
            }
        } else if (shouldShowRequestPermissionRationale(activity, permissionsList)) {
            requestForPermission();
            if (this.permissionInterface != null) {
                this.permissionInterface.showRationale();
            }
        } else {
            requestForPermission();
        }
    }

    /**
     * method to display the System default rationale
     */
    private void requestForPermission() {
        ActivityCompat.requestPermissions(activity, getStringArray(permissionsList),
                permissionsList.size());
    }

    /**
     * method to check if the permission qualifies for showing rationale
     *
     * @param activity
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(Activity activity, List<String>
            permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to check if the supplied array of permissions is granted by the system
     *
     * @param grantResults
     * @return
     */
    private boolean checkAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * method to call from Activity's onRequestPermissionsResult
     *
     * @param grantResults
     */
    public void dispatchPermissionResult(int[] grantResults) {
        if (checkAllPermissionsGranted(grantResults)) {
            if (permissionInterface != null) {
                permissionInterface.permissionAccepted();
            }
        } else {
            if (permissionInterface != null) {
                permissionInterface.permissionRejected();
            }
        }
    }

    /**
     * callback interface for delegating the permission states
     */
    public interface PermissionCallback {
        void permissionAccepted();

        void showRationale();

        void permissionRejected();
    }
}
