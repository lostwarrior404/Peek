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
        mCurrentUserUID = intent.getExtras().getString("current_user");
        Log.d(mTAG,mCurrentUserUID);
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
//                Log.d(mTAG,mCurrentUserUID);
                String loc=checkLocation();
                Log.d(mTAG,loc);
                usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                usersRef.child(mCurrentUserUID).child("mUserLocation").setValue(loc);
                Log.d(mTAG,"success");

//                databaseReference = FirebaseDatabase.getInstance().getReference();
//
//                Log.d(mTAG,usersRef.getKey());
//                mAuth = FirebaseAuth.getInstance();
//                muser=databaseReference.child("users").child(mCurrentUserUID);
//                Log.d(mTAG,muser.getKey());


                //mAuthListener = new FirebaseAuth.AuthStateListener()
//                {
//                    @Override
//                    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
//
//                            int userExistFlag = 0;
//                            Log.d(mTAG,"hey2");
//                            String name = firebaseAuth.getCurrentUser().getDisplayName() + firebaseAuth.getCurrentUser().getEmail() +firebaseAuth.getCurrentUser().getPhoneNumber()+checkLocation();
//                            //final TextView user_info = (TextView)findViewById(R.id.User_Information);
//                            //user_info.setText(name);
//
//                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        //Log.d(mTAG,postSnapshot.getKey());
//                                        if (postSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
//                                            mcurrentUser = postSnapshot.getValue(User.class);
//                                        }
//                                        if(mcurrentUser.getmUserLocation().equals(loc))
//                                        {
//                                        }
//                                        else
//                                        {
//
//                                            usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
//                                        }
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                    // ...
//                                }
//                            });
//
//
//
//
//
//
//                    }
//                };
                Log.d(mTAG,"fgfg");
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
                        return next[1].substring(0,2)+","+next[2];
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