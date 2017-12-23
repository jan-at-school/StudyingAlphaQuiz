package com.cykiq.studyingalphaquiz;

/**
 * Created by arifu on 7/17/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import me.anshulagarwal.simplifypermissions.MarshmallowSupportActivity;
import me.anshulagarwal.simplifypermissions.Permission;

public class SplashActivity extends MarshmallowSupportActivity {

    private static final int REQUEST = 16;
    static boolean location = false;
    static boolean camera = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity




        String[] GPS_PERMISSIONS = {};




        Permission.PermissionBuilder gpsPermissionBuilder =
                new Permission.PermissionBuilder(GPS_PERMISSIONS, REQUEST, new Permission.PermissionCallback() {
                    @Override
                    public void onPermissionGranted(int i) {
                        if(i==REQUEST){
                            location=true;
                        }
                        if(location){
                            goNext();
                            finish();
                        }

                    }

                    @Override
                    public void onPermissionDenied(int i) {

                    }

                    @Override
                    public void onPermissionAccessRemoved(int i) {
                        if(i == REQUEST){
                            location=false;
                        }
                    }
                })
                        .enableDefaultRationalDialog("Location!.", "Hey! App needs location camera access to work properly.")
                        .enableDefaultSettingDialog("Location/Camera access please!", "Please give cykiq access your location and camera to cykiq in settings.");





            requestAppPermissions(gpsPermissionBuilder.build());

    }



    void goNext(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(isInternetConnected()){

//            if(!prefs.getBoolean("login_success", false))//if user is not logged in
//            {
//                Log.d("Test", "Status");
//                Intent in = new Intent(this, WelcomeActivity.class);
//                startActivity(in);
//                Log.d("Test", "Status");
//            }
//            else //if user is logged in
//            {
//                Intent in = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(in);
//            }


            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(in);
        }
        else{
//            Log.d("Test", "Status");
//            Intent in = new Intent(this, LoginActivity.class);
//            startActivity(in);
//            Log.d("Test", "Status");
            Toast.makeText(getApplicationContext(),"No internet detected",Toast.LENGTH_LONG).show();

        };
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private boolean checkPermissions()
    {
        return true;
    }


}