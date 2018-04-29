package com.example.tushar.mc_final;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {


    private DatabaseReference mcurrent_user_db;
    private String mCurrentLocation;
    private ArrayList<Data> mDataList;

    public FeedFragment() {
        // Required empty public constructor
    }

    public ArrayList<Data> load_data(){
        ArrayList<Data> templist = new ArrayList<Data>();
        //Load All Data
        return templist;
    }
    public ArrayList<Data> sort(String mCurrentLocation){
        String [] arr  = mCurrentLocation.split(",");
        String building = arr[0];
        String floor = arr[1];//Do some preprocessing here;
        ArrayList<Data> templist = new ArrayList<Data>();
        //Load All Data
        return templist;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mDataList = load_data();

        FirebaseAuth temp_auth = FirebaseAuth.getInstance();
        String current_user_uid = temp_auth.getCurrentUser().getUid();
        mcurrent_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_uid);
        mcurrent_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentLocation = dataSnapshot.child("mUserLocation").getValue(String.class);
                mDataList = sort(mCurrentLocation);
                //Update The sort and recylcer view;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

}
