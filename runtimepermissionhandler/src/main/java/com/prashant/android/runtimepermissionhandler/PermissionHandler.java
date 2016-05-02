package com.prashant.android.runtimepermissionhandler;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
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
    private Fragment fragment;
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

    public PermissionHandler(Fragment fragment) {
        this.fragment = fragment;
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
        if (null == permissionsList) {
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
        final List<String> permissionsList = new ArrayList<>(this.permissionsList);
        boolean status = true;
        for (String permission : permissionsList) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(null == activity ?
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
     * @param permissionInterface
     */
    public void build(PermissionCallback permissionInterface) {
        if (null == permissionsList) {
            return;
        }
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
            FragmentCompat.requestPermissions(fragment, getStringArray(permissionsList),
                    permissionsList.size());
        }
    }

    /**
     * method to check if the permission qualifies for showing rationale
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (null == fragment) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true;
                }
            } else {
                if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
                    return true;
                }
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
            if (PackageManager.PERMISSION_GRANTED != result) {
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
