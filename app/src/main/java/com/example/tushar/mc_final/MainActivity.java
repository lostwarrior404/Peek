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
    private User mFriendUser;
    private String mFriendUID;
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
                    //Log.d(mTAG, "Old");
                }
                else
                {


                    String name = firebaseAuth.getCurrentUser().getDisplayName() + firebaseAuth.getCurrentUser().getEmail() +firebaseAuth.getCurrentUser().getPhoneNumber()+checkLocation();
                    final TextView user_info = (TextView)findViewById(R.id.User_Information);
                    user_info.setText(name);


                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //Log.d(mTAG,postSnapshot.getKey());
                                if (postSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                    mcurrentUser = postSnapshot.getValue(User.class);
                                }
                            }
                            if(mcurrentUser==null){
                                //Log.d(mTAG,"rewrite");
                                mcurrentUser = new User(firebaseAuth.getCurrentUser().getEmail(), firebaseAuth.getCurrentUser().getDisplayName(), true, checkLocation(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                                usersRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                            }
                            Intent intent1 = new Intent(MainActivity.this, MyService.class);
                            intent1.putExtra("current_user",firebaseAuth.getCurrentUser().getUid());
                            Log.d(mTAG,"jaggi"+firebaseAuth.getCurrentUser().getUid());
                            startService(intent1);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            // ...
                        }
                    });
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String friend_location_display="";
                            ArrayList<String> friends= (ArrayList<String>) mcurrentUser.getmFriends();
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                                if(friends!=null && friends.contains(postSnapshot.getValue(User.class).getmEmail())){
                                    User friend=postSnapshot.getValue(User.class);
                                    friend_location_display+=friend.getmName()+":"+friend.getmUserLocation()+"\n";
                                }
                                TextView friend_text_box=findViewById(R.id.friendlocation);
                                friend_text_box.setText(friend_location_display);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("mFriends").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            if(mcurrentUser!=null){
                                for (DataSnapshot child : children)
                                {
                                 mcurrentUser.addFriend(child.getValue(String.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("mReceived").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            String display ="";
                            TextView request_display = findViewById(R.id.textView3);
                            for (DataSnapshot child : children)
                            {
                                display+=child.getValue(String.class)+"\n";
                            }
                            request_display.setText(display);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





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



        Button addFriend = (Button)findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String friendEmailID = String.valueOf(((EditText)findViewById(R.id.searchName)).getText());

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(mTAG, "printaa");
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                        {
                            Log.d(mTAG, postSnapshot.getValue(User.class).getmEmail());
                            if(postSnapshot.getValue(User.class).getmEmail().equals(friendEmailID)){
                                Log.d(mTAG, friendEmailID);
                                mFriendUser = postSnapshot.getValue(User.class);
                                mFriendUID = postSnapshot.getKey();
                                mcurrentUser.addSent(friendEmailID);
                                mFriendUser.addReceived(mcurrentUser.getmEmail());
                                usersRef.child(mAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                                usersRef.child(mFriendUID).setValue(mFriendUser);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // ...
                    }
                });


            }
        });


        Button acceptRequests = (Button)findViewById(R.id.acceptRequests);
        acceptRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String friendEmailID = String.valueOf(((EditText)findViewById(R.id.searchName2)).getText());

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(mTAG, "printaa");
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                        {
                            Log.d(mTAG, postSnapshot.getValue(User.class).getmEmail());
                            if(postSnapshot.getValue(User.class).getmEmail().equals(friendEmailID)){
                                Log.d(mTAG, friendEmailID);
                                mFriendUser = postSnapshot.getValue(User.class);
                                mFriendUID = postSnapshot.getKey();

                                mcurrentUser.addFriend(mFriendUser.getmEmail());
                                mFriendUser.addFriend(mcurrentUser.getmEmail());

                                mcurrentUser.deleteReceived(mFriendUser.getmEmail());
                                mFriendUser.deleteSent(mcurrentUser.getmEmail());

                                usersRef.child(mAuth.getCurrentUser().getUid()).setValue(mcurrentUser);
                                usersRef.child(mFriendUID).setValue(mFriendUser);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // ...
                    }
                });

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