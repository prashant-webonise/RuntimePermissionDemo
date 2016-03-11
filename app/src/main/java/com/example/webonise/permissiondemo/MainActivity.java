package com.example.webonise.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.prashant.android.runtimepermissionhandler.PermissionHandler;

public class MainActivity extends AppCompatActivity {

    private PermissionHandler permissionHandler;

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
        permissionHandler = new PermissionHandler(this);
        permissionHandler
                .addPermission(Manifest.permission.CALL_PHONE)
                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .build(new PermissionHandler.PermissionCallback() {

                    @Override
                    public void permissionRejected() {
                        Log.d(getClass().getSimpleName(), "permission rejected");
                    }

                    @Override
                    public void permissionAccepted() {
                        Log.d(getClass().getSimpleName(), "permission accepted");
                    }

                    @Override
                    public void showRationale() {
                        Log.d(getClass().getSimpleName(), "asking permission again");
                    }
                });
    }

    private void askSinglePermission() {
        permissionHandler = new PermissionHandler(this);
        permissionHandler
                .addPermission(Manifest.permission.CAMERA)
                .build(new PermissionHandler.PermissionCallback() {

                    @Override
                    public void permissionRejected() {
                        Log.d(getClass().getSimpleName(), "permission rejected");
                    }

                    @Override
                    public void permissionAccepted() {
                        Log.d(getClass().getSimpleName(), "permission accepted");
                    }

                    @Override
                    public void showRationale() {
                        Log.d(getClass().getSimpleName(), "asking permission again");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.dispatchPermissionResult(grantResults);
    }
}
