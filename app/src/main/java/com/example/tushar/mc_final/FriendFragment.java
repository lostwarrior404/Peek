 package com.example.tushar.mc_final;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.media.Image;
import android.net.Uri;
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
import java.net.MalformedURLException;
import java.net.URL;
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
    private static String  TAG = "ZXY";
    private GridLayoutManager manager;
    private recycler_adapter adapter;
    private Button chatButton;
    private ArrayList<String> allPeople;
    private ArrayList<User> allPeopleUser;
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
        allPeople = new ArrayList<>();
        allPeopleUser = new ArrayList<>();
        // get arraylists here

         mDatabaseReference = FirebaseDatabase.getInstance().getReference();
         mUsersRef = mDatabaseReference.child("users");
         getcurrentUser();

        Button Accept_friend = (Button) view.findViewById(R.id.accept_friend);
        Accept_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected = 1;
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
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
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
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
                manager = new GridLayoutManager(getActivity().getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
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
                    Uri temp_uri = mAuth.getCurrentUser().getPhotoUrl();
                    mCurrentUser = new User(mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName(), true, "Unknown,Unknown,Unknown", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),temp_uri.toString());
                    mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(mCurrentUser);
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
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                    }
                }
                adapter.setmList(templist);
                mSearchFriend = templist;
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
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                    }
                }
                adapter.setmList(templist);
                mAcceptFriend = templist;
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
                allPeople = new ArrayList<>();
                ArrayList<User> templist = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot postsnapshot: dataSnapshot.getChildren())
                {
                    User friend = postsnapshot.getValue(User.class);
//                    templist.add(friend);
                    allPeople.add(friend.getmEmail());
                }

                ArrayList<String> temp2 = (ArrayList<String>) mCurrentUser.getmFriends();
                ArrayList<String> temp3 = (ArrayList<String>) mCurrentUser.getmReceived();
                if(temp2!=null)
                {
                    for(int i=0; i<temp2.size(); i++)
                        allPeople.remove(temp2.get(i));
                }
                if(temp3!=null)
                {
                    for(int i=0; i<temp3.size(); i++)
                        allPeople.remove(temp3.get(i));

                }
                allPeople.remove(mCurrentUser.getmEmail());

                for(DataSnapshot postsnapshot: dataSnapshot.getChildren())
                {
                    if(allPeople != null && allPeople.contains(postsnapshot.getValue(User.class).getmEmail()))
                    {
                        User friend = postsnapshot.getValue(User.class);
                        templist.add(friend);
                    }

                }


                adapter.setmList(templist);
                allPeopleUser = templist;
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
            {
                holder.actionButton.setImageResource(R.drawable.ic_done_black_36dp);
                holder.textView.setText(mList.get(position).getmName());
                holder.textView2.setText(mList.get(position).getmEmail());
            }
            else if(mSelected == 2)
            {
                holder.actionButton.setImageResource(R.drawable.ic_block_black_36dp);
                holder.textView.setText(mList.get(position).getmName());
                holder.textView2.setText(mList.get(position).getmUserLocation());

            }
            else if(mSelected == 3)
            {
                holder.actionButton.setImageResource(R.drawable.ic_add_black_36dp);
                holder.textView.setText(mList.get(position).getmName());
                holder.textView2.setText(mList.get(position).getmEmail());
            }
            URL url = null;
            Bitmap bmp = null;
            try {
                url = new URL(mList.get(position).getmImageUri());
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.imageView.setImageBitmap(bmp);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView imageView;
            public ImageButton actionButton;
            public TextView textView;
            public TextView textView2;
            placeHolderData content;

            public MyViewHolder(View itemView) {
                super(itemView);

                actionButton = (ImageButton) itemView.findViewById(R.id.actionButton);
                actionButton.setOnClickListener(this);
                textView = (TextView) itemView.findViewById(R.id.placetextView);
                textView2 = (TextView) itemView.findViewById(R.id.placetextView2);
                imageView = (ImageView) itemView.findViewById(R.id.placeimageView);

            }

            @Override
            public void onClick(View view) {
//                Log.d(TAG+"AAAAA", String.valueOf(mSelected));
                if(mSelected == 1)
                {

                    final int pos = getAdapterPosition();
                    Log.d(TAG+"AAAAA", String.valueOf(pos));
                    mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mFriendUID;
                            User mFriendUser = mAcceptFriend.get(pos);
                            String friendEmailID = mFriendUser.getmEmail();
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                if(postSnapshot.getValue(User.class).getmEmail().equals(friendEmailID)){
                                    mFriendUser = postSnapshot.getValue(User.class);
                                    mFriendUID = postSnapshot.getKey();
                                    mCurrentUser.addFriend(friendEmailID);
                                    mFriendUser.addFriend(mCurrentUser.getmEmail());
                                    mCurrentUser.deleteReceived(friendEmailID);
                                    mFriendUser.deleteSent(mCurrentUser.getmEmail());
                                    mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(mCurrentUser);
                                    mUsersRef.child(mFriendUID).setValue(mFriendUser);
//                                    Log.d(TAG+"AAAAA", "A");

                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // accept friend
                    // add to User1 - mFriend
                    // remove from User1 - mReceived
                    // add to User2 - mFriend
                    // remove from User2 - mSent
                }
                else if(mSelected == 2)
                {
                    final int pos = getAdapterPosition();
                    Log.d(TAG+"AAAAA", String.valueOf(pos));
                    mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mFriendUID;
                            User mFriendUser = mSearchFriend.get(pos);
                            String friendEmailID = mFriendUser.getmEmail();
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                if(postSnapshot.getValue(User.class).getmEmail().equals(friendEmailID)){
                                    mFriendUser = postSnapshot.getValue(User.class);
                                    mFriendUID = postSnapshot.getKey();
                                    mCurrentUser.deleteFriends(friendEmailID);
                                    mFriendUser.deleteFriends(mCurrentUser.getmEmail());
                                    mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(mCurrentUser);
                                    mUsersRef.child(mFriendUID).setValue(mFriendUser);
//                                    Log.d(TAG+"AAAAA", "A");

                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // block
                    // remove from User1 - mFriend
                    // remove from User2 - mFriend
                }
                else
                {

                    final int pos = getAdapterPosition();
                    Log.d(TAG+"AAAAA", String.valueOf(pos));
                    mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mFriendUID;
                            User mFriendUser = allPeopleUser.get(pos);
                            String friendEmailID = mFriendUser.getmEmail();
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                if(postSnapshot.getValue(User.class).getmEmail().equals(friendEmailID)){
                                    mFriendUser = postSnapshot.getValue(User.class);
                                    mFriendUID = postSnapshot.getKey();
                                    mCurrentUser.addSent(friendEmailID);
                                    mFriendUser.addReceived(mCurrentUser.getmEmail());
                                    mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(mCurrentUser);
                                    mUsersRef.child(mFriendUID).setValue(mFriendUser);
//                                    Log.d(TAG+"AAAAA", "A");

                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // add new unknown friend
                    // add to User1 - mSent
                    // add to User2 - mReceived
                }

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