package com.example.webonise.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.prashant.android.runtimepermissionhandler.PermissionHandler;

public class MainActivity extends AppCompatActivity implements PermissionHandler
        .PermissionCallback {

    private static final int REQUEST_MULTIPLE_PERMISSION = 22;
    private static final int REQUEST_SINGLE_PERMISSION = 42;
    private PermissionHandler multiplePermissionHandler;
    private PermissionHandler singlePermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnSingle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askSinglePermission();
            }
        });


        findViewById(R.id.btnMultiple).setOnClickListener(new View.OnClickListener() {
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

                    }

                    @Override
                    public void showRationale(int requestCode) {

                    }

                    @Override
                    public void permissionRejected(int requestCode) {

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
