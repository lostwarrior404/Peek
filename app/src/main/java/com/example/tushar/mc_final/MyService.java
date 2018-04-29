package com.example.tushar.mc_final;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private static String mTAG = "MCDEBUG";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference,usersRef;
    private FirebaseAuth mAuth;
    private User mcurrentUser;
    private DatabaseReference muser;
    private String mCurrentUserUID;


    public MyService() {

    }
    public int onStartCommand (Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override

    public void onCreate() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String loc=checkLocation();
                mAuth=mAuth.getInstance();
                if(mAuth.getCurrentUser().getUid()!=null){
                    usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("mUserLocation").setValue(loc);
                }
            }
        }, 0, 1000);

    }



    public String checkLocation() {
        String next[] = {};
        try {
            Log.d(mTAG,"Mac:id"+getMacId());
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.mac_address_list)));
            for (; ; ) {
                next = reader.readNext();
                if (next != null) {

                    if (next[4].substring(0, 16).equals(getMacId())) {
                        Log.d(mTAG,"Jaggi"+next[2]);
                        char floor = '0';
                        for (int i = 0; i < next[1].length(); i++) {
                            if(Character.isDigit(next[1].charAt(i))){
                                floor = next[1].charAt(i);
                                break;
                            }

                        }
                        return next[1].substring(0,2)+","+next[2]+","+floor;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            Log.d(mTAG, "Jaggi"+e.getMessage());
            e.printStackTrace();
        }
        return "-1";
    }


    public String getMacId() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID().substring(0, 16);
    }
}