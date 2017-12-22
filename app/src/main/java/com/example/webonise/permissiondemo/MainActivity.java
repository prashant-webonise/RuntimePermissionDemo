package com.example.webonise.permissiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity:
                startActivity(new Intent(this, SimpleActivity.class));
                break;
            case R.id.fragment:
                startActivity(new Intent(this, SimpleFragmentActivity.class));
                break;
            case R.id.supportfragment:
                startActivity(new Intent(this, SupportFragmentActivity.class));
                break;
        }
    }
}
