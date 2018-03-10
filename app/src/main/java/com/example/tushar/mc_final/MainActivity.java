package com.example.tushar.mc_final;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static String mTAG = "MCDEBUG";
    private String mbssidString = null;
    private String mcurrentLocation = "Jaggi ki Service";
    private FirebaseAuth mAuth;
    private User mcurrentUser;
    private static String  TAG = "MCDEBUG";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference,usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usersRef = databaseReference.child("users");


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(MainActivity.this,Login.class));
                }
                else
                {
                    int userExistFlag = 0;
                    String name = firebaseAuth.getCurrentUser().getDisplayName() + firebaseAuth.getCurrentUser().getEmail() +firebaseAuth.getCurrentUser().getPhoneNumber()+checkLocation();
                    final TextView user_info = (TextView)findViewById(R.id.User_Information);
                    user_info.setText(name);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                if(postSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                                    mcurrentUser = postSnapshot.getValue(User.class);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            // ...
                        }
                    });

                    if(mcurrentUser==null){
                        mcurrentUser = new User(firebaseAuth.getCurrentUser().getEmail(), firebaseAuth.getCurrentUser().getDisplayName(), true, checkLocation(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                        usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                    }



                }
            }
        };



        Button logoutButton = (Button)findViewById(R.id.Logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });


        final EditText searchFriend = (EditText)findViewById(R.id.searchName);
        Button addFriend = (Button)findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendEmailID = String.valueOf(searchFriend.getText());

                ArrayList<String> sentRequests = (ArrayList<String>) mcurrentUser.getmSent();
                sentRequests.add(friendEmailID);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            if(postSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())){
                                mcurrentUser = postSnapshot.getValue(User.class);
                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // ...
                    }
                });

                if(mcurrentUser==null){
                    mcurrentUser = new User(mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName(), true, checkLocation(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                    usersRef.child(mAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                }


            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    public String checkLocation()
    {
        String next[]={};
        try
        {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.mac_address_list)));
            for(;;)
            {
                next = reader.readNext();

                if (next!=null)
                {
                    if(next[4].substring(0,16).equals(getMacId())){
                        return next[2];
                    }
                }
                else
                {
                    break;
                }
            }
        } catch (IOException e){
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
        return "-1";
    }

    public String getMacId() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return  wifiInfo.getBSSID().substring(0,16);
    }






//    public int addFriend(String argEmailID)
//    {
//
//        if(mFriends!=null && this.mFriends.contains(argEmailID))
//        {
//            return 0; // Friend already exists
//        }
//        else
//        {
//            // Query karna hai database ko for correct email id // return -1 = invalid email
//            // Agar correct hai to sent mein daalna hai and database commit [Dusre ke received mein add karna hai] = return 1
//        }
//    }



}