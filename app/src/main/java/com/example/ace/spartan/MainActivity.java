package com.example.ace.spartan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public Activity_Identifiers activity_identifiers=new Activity_Identifiers(MainActivity.this);
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
sharedPreferences=getSharedPreferences("MyPref",0);
      String[] creds=new String[2];
session_details sessions=new session_details(this);
      if(sharedPreferences.contains("userId")) {

int a=sessions.getCheck();

if(a==1) {
    Intent intent = new Intent(this, ReceptionistTabbedActivity.class);
    startActivity(intent);
    finish();
}
if(a==2){
    Intent intent = new Intent(this, WorkShop.class);
    startActivity(intent);
    finish();
}
      }
        Button workShop=(Button)findViewById(R.id.workshop);
        Button Cust=(Button)findViewById(R.id.customer);
        final Intent intent=new Intent(MainActivity.this,customerLoginActivity.class);
        workShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                if(activity_identifiers.inserts(1)==true) {
                    startActivity(intent);
                   // finish();
                }}else {
                    Toast.makeText(getApplicationContext(),"Please Connect to your Mobile Data/wifi!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
        Cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                if(activity_identifiers.inserts(2)==true){
                    startActivity(intent);
                }}
                else {
                    Toast.makeText(getApplicationContext(),"Please Connect to your Mobile Data/wifi!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}




















