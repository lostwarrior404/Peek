 package com.example.tushar.mc_final;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


 /**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUsersRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView mRecyclerView;
    private static String  TAG = "MCDEBUG";
    private GridLayoutManager manager;
    private recycler_adapter adapter;
    private Button chatButton;

    private ArrayList<User> mAcceptFriend;
    private ArrayList<User> mSearchFriend;
    private ArrayList<User> mSendFriend;

    private Integer mSelected;

    public FriendFragment() {
        // Required empty public constructor
    }


     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         mSelected = 0;
         mAuth = FirebaseAuth.getInstance();
         mDatabaseReference = FirebaseDatabase.getInstance().getReference();
     }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_friend, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mAcceptFriend = new ArrayList<>();
        mSearchFriend = new ArrayList<>();
        mSendFriend = new ArrayList<>();
        // get arraylists here

         mDatabaseReference = FirebaseDatabase.getInstance().getReference();
         mUsersRef = mDatabaseReference.child("users");
         getcurrentUser();

        Button Accept_friend = (Button) view.findViewById(R.id.accept_friend);
        Accept_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected = 1;
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(manager);
                adapter = new recycler_adapter(mAcceptFriend);
                mRecyclerView.setAdapter(adapter);
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mUsersRef = mDatabaseReference.child("users");
                getcurrentUser();
                getCurrentReceivedRequests();

            }
        });

        Button Search_friend = (Button) view.findViewById(R.id.search_friend);
        Search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected = 2;
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(manager);
                adapter = new recycler_adapter(mSearchFriend);
                mRecyclerView.setAdapter(adapter);
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mUsersRef = mDatabaseReference.child("users");
                getcurrentUser();
                getCurrentFriends();
            }
        });

        Button Send_friend = (Button) view.findViewById(R.id.send_friend);
        Send_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected = 3;
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(manager);
                adapter = new recycler_adapter(mSendFriend);
                mRecyclerView.setAdapter(adapter);
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mUsersRef = mDatabaseReference.child("users");
                getcurrentUser();
                getCurrentSentRequests();
            }
        });












         return view;


    }


    public void getcurrentUser()
    {
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                        mCurrentUser = postSnapshot.getValue(User.class); // check if user exists on firebase
                    }
                }
                if(mCurrentUser==null){ //if not
                    mCurrentUser = new User(mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName(), true, checkLocation(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                    mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(mCurrentUser); // make new user and add
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void getCurrentFriends()
    {
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> currentFriends = (ArrayList<String>) mCurrentUser.getmFriends();
                ArrayList<User> templist = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot postsnapshot: dataSnapshot.getChildren())
                {
                    if(currentFriends != null && currentFriends.contains(postsnapshot.getValue(User.class).getmEmail()))
                    {
//                        Log.d(TAG, String.valueOf(currentFriends.size()));
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                        Log.d(TAG, currentFriends.size()  + '-' +  String.valueOf(mSearchFriend.size()));
                    }
                }
                adapter.setmList(templist);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUsersRef.child(mAuth.getCurrentUser().getUid()).child("mFriends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if(mCurrentUser != null)
                {
                    for(DataSnapshot child : children)
                    {

                        mCurrentUser.addFriend(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void getCurrentReceivedRequests() {
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> currentFriends = (ArrayList<String>) mCurrentUser.getmReceived();
                ArrayList<User> templist = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot postsnapshot: dataSnapshot.getChildren())
                {
                    if(currentFriends != null && currentFriends.contains(postsnapshot.getValue(User.class).getmEmail()))
                    {
//                        Log.d(TAG, String.valueOf(currentFriends.size()));
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                        Log.d(TAG, currentFriends.size()  + '-' +  String.valueOf(mSearchFriend.size()));
                    }
                }
                adapter.setmList(templist);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUsersRef.child(mAuth.getCurrentUser().getUid()).child("mReceived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if(mCurrentUser != null)
                {
                    for(DataSnapshot child : children)
                    {

                        mCurrentUser.addReceived(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void getCurrentSentRequests() {
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> currentFriends = (ArrayList<String>) mCurrentUser.getmSent();
                ArrayList<User> templist = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot postsnapshot: dataSnapshot.getChildren())
                {
                    if(currentFriends != null && currentFriends.contains(postsnapshot.getValue(User.class).getmEmail()))
                    {
//                        Log.d(TAG, String.valueOf(currentFriends.size()));
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                        Log.d(TAG, currentFriends.size()  + '-' +  String.valueOf(mSearchFriend.size()));
                    }
                }
                adapter.setmList(templist);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUsersRef.child(mAuth.getCurrentUser().getUid()).child("mSent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if(mCurrentUser != null)
                {
                    for(DataSnapshot child : children)
                    {

                        mCurrentUser.addSent(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
         WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         return  wifiInfo.getBSSID().substring(0,16);
     }





     public class recycler_adapter extends RecyclerView.Adapter<recycler_adapter.MyViewHolder> {

        ArrayList<User> mList;

        public recycler_adapter(ArrayList<User> mList){
            this.mList = mList;
        }
        public void setmList(ArrayList<User> mList){
            this.mList=mList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if(mSelected == 1)
                holder.actionButton.setImageResource(R.drawable.ic_done_black_36dp);
            else if(mSelected == 2)
            {
//                holder.actionButton.setImageResource(R.drawable.ic_search_black_36dp);
                holder.actionButton.setImageResource(0);
            }
            else if(mSelected == 3)
                holder.actionButton.setImageResource(R.drawable.ic_add_black_36dp);
            holder.textView.setText(mList.get(position).getmName());
            holder.textView2.setText(mList.get(position).getmUserLocation());
            holder.imageView.setImageBitmap(null);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public ImageButton actionButton;
            public TextView textView;
            public TextView textView2;
            placeHolderData content;

            public MyViewHolder(View itemView) {
                super(itemView);
                actionButton = (ImageButton) itemView.findViewById(R.id.actionButton);
                textView = (TextView) itemView.findViewById(R.id.placetextView);
                textView2 = (TextView) itemView.findViewById(R.id.placetextView2);
                imageView = (ImageView) itemView.findViewById(R.id.placeimageView);

            }
        }
    }

 }


class placeHolderData {
    public Bitmap mImage;
    public String mName;
    public String mLocation;

    public placeHolderData(Bitmap mImage, String mName, String mLocation)
    {
        this.mImage = mImage;
        this.mName = mName;
        this.mLocation = mLocation;
    }
}