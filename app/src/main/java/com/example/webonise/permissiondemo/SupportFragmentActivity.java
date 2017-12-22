package com.example.webonise.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prashant.android.runtimepermissionhandler.PermissionHandler;

public class SupportFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_fragment);
    }

    /**
     * A simple {@link Fragment} subclass.
     */
    public static class SupportFragmentClass extends Fragment implements PermissionHandler
            .PermissionCallback {


        private static final int REQUEST_MULTIPLE_PERMISSION = 22;
        private static final int REQUEST_SINGLE_PERMISSION = 42;
        private static final String TAG = SupportFragmentClass.class.getSimpleName();
        private PermissionHandler multiplePermissionHandler;
        private PermissionHandler singlePermissionHandler;

        public SupportFragmentClass() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_layout, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.findViewById(R.id.btnSingle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askSinglePermission();
                }
            });


            view.findViewById(R.id.btnMultiple).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askMultiplePermission();
                }
            });
        }

        private void askMultiplePermission() {
            multiplePermissionHandler = new PermissionHandler(this);
            multiplePermissionHandler
                    .addPermission(Manifest.permission.CALL_PHONE)
                    .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .ask(REQUEST_MULTIPLE_PERMISSION, new PermissionHandler.PermissionCallback() {
                        @Override
                        public void permissionAccepted(int requestCode) {
                            Log.d(TAG, "permissionAccepted: ");
                        }

                        @Override
                        public void showRationale(int requestCode) {
                            Log.d(TAG, "showRationale: ");
                        }

                        @Override
                        public void permissionRejected(int requestCode) {
                            Log.d(TAG, "permissionRejected: ");
                        }
                    });
        }

        private void askSinglePermission() {
            singlePermissionHandler = new PermissionHandler(this);
            singlePermissionHandler
                    .addPermission(Manifest.permission.CAMERA)
                    .ask(REQUEST_SINGLE_PERMISSION, this);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_SINGLE_PERMISSION) {
                singlePermissionHandler.dispatchPermissionResult(grantResults);
            } else if (requestCode == REQUEST_MULTIPLE_PERMISSION) {
                multiplePermissionHandler.dispatchPermissionResult(grantResults);
            }
        }

        @Override
        public void permissionAccepted(int requestCode) {
            Log.d(getClass().getSimpleName(), "permission accepted");
        }

        @Override
        public void showRationale(int requestCode) {
            Log.d(getClass().getSimpleName(), "asking permission again");
        }

        @Override
        public void permissionRejected(int requestCode) {
            Log.d(getClass().getSimpleName(), "permission rejected");
        }
    }
}
