package com.prashant.android.runtimepermissionhandler;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
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
    private Fragment supportV4Fragment;
    private android.app.Fragment fragment;
    private int requestCode;
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

    public PermissionHandler(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * constructor supplied with supportV4Fragment reference
     *
     * @param supportV4Fragment valid instance of {@link Fragment}
     */
    public PermissionHandler(Fragment supportV4Fragment) {
        this.supportV4Fragment = supportV4Fragment;
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
            if (isPermissionGranted(permission)) {
                status = false;
            } else {
                this.permissionsList.remove(permission);
            }
        }
        return status;
    }

    private boolean isPermissionGranted(String permission) {
        if (activity != null) {
            return PackageManager.PERMISSION_GRANTED !=
                    PermissionChecker.checkSelfPermission(activity, permission);
        }
        if (supportV4Fragment != null) {
            return PackageManager.PERMISSION_GRANTED !=
                    PermissionChecker.checkSelfPermission(supportV4Fragment.getActivity(),
                            permission);
        }
        return PackageManager.PERMISSION_GRANTED !=
                PermissionChecker.checkSelfPermission(fragment.getActivity(), permission);
    }

    /**
     * method to start requesting the permissions
     *
     * @param permissionInterface instance of {@link PermissionCallback} to listen to events
     */
    public void ask(final @IntRange(from = 0) int requestCode,
                    PermissionCallback permissionInterface) {
        this.requestCode = requestCode;
        this.permissionInterface = permissionInterface;
        if (checkPermission()) {
            if (permissionInterface != null) {
                this.permissionInterface.permissionAccepted(requestCode);
            }
        } else if (shouldShowRequestPermissionRationale(permissionsList)) {
            requestForPermission();
            if (permissionInterface != null) {
                this.permissionInterface.showRationale(requestCode);
            }
        } else {
            requestForPermission();
        }
    }

    /**
     * method to display the System default rationale
     */
    private void requestForPermission() {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, getStringArray(permissionsList),
                    requestCode);
        } else if (supportV4Fragment != null) {
            supportV4Fragment.requestPermissions(getStringArray(permissionsList), requestCode);
        } else {
            FragmentCompat.requestPermissions(fragment, getStringArray(permissionsList),
                    requestCode);
        }
    }

    /**
     * method to check if the permission qualifies for showing rationale
     */
    private boolean shouldShowRequestPermissionRationale(Set<String> permissions) {
        for (String permission : permissions) {
            if (activity != null) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true;
                }
            } else if (supportV4Fragment != null) {
                if (supportV4Fragment.shouldShowRequestPermissionRationale(permission)) {
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
            if (permissionInterface != null) {
                permissionInterface.permissionAccepted(requestCode);
            }
        } else {
            if (permissionInterface != null) {
                permissionInterface.permissionRejected(requestCode);
            }
        }
    }

    /**
     * callback interface for delegating the permission states
     */
    public interface PermissionCallback {
        void permissionAccepted(final int requestCode);

        void showRationale(final int requestCode);

        void permissionRejected(final int requestCode);
    }
}
