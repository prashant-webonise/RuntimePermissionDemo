package com.prashant.android.runtimepermissionhandler;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Prashant Singh
 */
public class PermissionHandler {
    /**
     * list for storing single/multi permissions
     */
    private final Set<String> permissionsList = new HashSet<>();
    private Activity activity;
    private Fragment fragment;
    /**
     * instance of callback interface for delegating the permission states
     */
    private PermissionCallback permissionInterface;

    /**
     * constructor supplied with activity reference
     *
     * @param activity valid instance of {@link Activity}
     */
    public PermissionHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * constructor supplied with fragment reference
     *
     * @param fragment valid instance of {@link Fragment}
     */
    public PermissionHandler(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    private static String[] getStringArray(Set<String> stringList) {
        return stringList.toArray(new String[stringList.size()]);
    }

    /**
     * method to add permission. The same method can be called again for adding more permissions
     *
     * @param permission The string permission which needs to be asked. Must be from the
     *                   {@link android.Manifest.permission}
     */
    public PermissionHandler addPermission(String permission) {
        permissionsList.add(permission);
        return this;
    }

    /**
     * method checks if the permission in the permissionsList are not already granted, if
     * granted, it removes the permission from the permissionsList
     *
     */
    private boolean checkPermission() {
        final List<String> permissionsList = new ArrayList<>(this.permissionsList);
        boolean status = true;
        for (String permission : permissionsList) {
            if (PackageManager.PERMISSION_GRANTED !=
                    PermissionChecker.checkSelfPermission(null == activity ?
                    fragment.getActivity() : activity, permission)) {
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
     * @param permissionInterface instance of {@link PermissionCallback} to listen to events
     */
    public void build(PermissionCallback permissionInterface) {
        this.permissionInterface = permissionInterface;
        if (checkPermission()) {
            if (null != this.permissionInterface) {
                this.permissionInterface.permissionAccepted();
            }
        } else if (shouldShowRequestPermissionRationale(permissionsList)) {
            requestForPermission();
            if (null != this.permissionInterface) {
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
        if (null == fragment) {
            ActivityCompat.requestPermissions(activity, getStringArray(permissionsList),
                    permissionsList.size());
        } else {
            fragment.requestPermissions(getStringArray(permissionsList), permissionsList.size());
        }
    }

    /**
     * method to check if the permission qualifies for showing rationale
     */
    private boolean shouldShowRequestPermissionRationale(Set<String> permissions) {
        for (String permission : permissions) {
            if (null == fragment) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true;
                }
            } else {
                if (fragment.shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * method to check if the supplied array of permissions is granted by the system
     */
    private boolean checkAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false;
            }
        }
        return true;
    }

    /**
     * method to call from {@link Activity#onRequestPermissionsResult(int, String[], int[])} of
     * {@link Fragment#onRequestPermissionsResult(int, String[], int[])}
     *
     * @param grantResults the grantResults onRequestPermissionsResult
     */
    public void dispatchPermissionResult(int[] grantResults) {
        if (checkAllPermissionsGranted(grantResults)) {
            if (null != permissionInterface) {
                permissionInterface.permissionAccepted();
            }
        } else {
            if (null != permissionInterface) {
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
