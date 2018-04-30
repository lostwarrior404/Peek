package com.example.tushar.mc_final;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private User mcurrentUser;
    private static String  TAG = "MCDEBUG";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isReceiverRegistered = false;
    private DatabaseReference databaseReference,usersRef;
    FragmentManager mFragment_manager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        mFragment_manager.beginTransaction().replace(R.id.mainframe,new FeedFragment()).commit();

          bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //fragment1
                        selectedFragment = new FriendFragment();
                        mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                        break;
                    case R.id.navigation_dashboard:
                        //fragment2
                        //setTitle("feed");
                        selectedFragment = new FeedFragment();
                        mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                        break;
                    case R.id.navigation_notifications:
                        //fragment3
                        //setTitle("chat");
                        selectedFragment = new ChatFragment();
                        mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                        break;


                }
                return true;
            }
        });



        StrictMode.setThreadPolicy(policy);
        if(checkwifistate()==false)
        {
            Toast.makeText(HomeActivity.this,"Not connected",Toast.LENGTH_LONG).show();
        }
         else{
            databaseReference = FirebaseDatabase.getInstance().getReference();
            usersRef = databaseReference.child("users");
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null || !firebaseAuth.getCurrentUser().getEmail().contains("iiitd.ac.in")) {
                        startActivity(new Intent(HomeActivity.this, Login.class));
                    } else {
                        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                        mcurrentUser = postSnapshot.getValue(User.class);
                                    }
                                }
                                if (mcurrentUser == null) {
                                    Uri temp_uri = firebaseAuth.getCurrentUser().getPhotoUrl();

                                    mcurrentUser = new User(firebaseAuth.getCurrentUser().getEmail(), firebaseAuth.getCurrentUser().getDisplayName(), true, "Unknown,Unknown,Unknown", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),temp_uri.toString());
                                    usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                                }
                                Intent intent1 = new Intent(HomeActivity.this, MyService.class);
                                startService(intent1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                // ...
                            }
                        });
                    }
                }
            };
            mAuth=FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(mAuthListener);
            }
    }

    public boolean checkwifistate(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
            return false;
        }
        return true;
    }
}
