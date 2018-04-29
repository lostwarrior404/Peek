package com.example.tushar.mc_final;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    RecyclerView recyclerView;

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
        String floor = arr[2];

        ArrayList<Data> templist = new ArrayList<Data>();
        //Load All Data
        return templist;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mDataList = load_data();

        FirebaseAuth temp_auth = FirebaseAuth.getInstance();
        String current_user_uid = temp_auth.getCurrentUser().getUid();
        mcurrent_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_uid);
        mcurrent_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentLocation = dataSnapshot.child("mUserLocation").getValue(String.class);
//                mDataList = sort(mCurrentLocation);
                //Update The sort and recylcer view;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMain);
        Bitmap[] bitmaps = setUpBitmaps();
        recyclerView.setAdapter(new GridLayoutAdapter(bitmaps));
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        return view;
    }
    private class GridHolder extends RecyclerView.ViewHolder {

        ImageView imageView1;
        ImageView imageView2;
        TextView textView;

        public GridHolder(View itemView) {
            super(itemView);
            imageView1 =     itemView.findViewById(R.id.table_icon);
            imageView2 =     itemView.findViewById(R.id.table_button);
            textView = itemView.findViewById(R.id.table_heading);
        }
    }
    private class GridLayoutAdapter extends RecyclerView.Adapter<GridHolder> {
        Bitmap[] bitmaps;

        public GridLayoutAdapter(Bitmap[] bitmaps) {
            this.bitmaps = bitmaps;
        }
        @Override
        public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.feed_table, parent, false);
            return new GridHolder(view);
        }

        @Override
        public void onBindViewHolder(GridHolder holder, int position) {
            holder.imageView1.requestLayout();
            holder.imageView2.requestLayout();
            holder.imageView1.setImageBitmap(bitmaps[position]);
            holder.imageView2.setImageBitmap(bitmaps[position]);
            holder.textView.setText("Caption: " + position);
        }

        @Override
        public int getItemCount() {
            return bitmaps.length;
        }
    }
    private Bitmap[] setUpBitmaps() {
        Bitmap[] bitmaps = new Bitmap[10];
        bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.peek_logo);
        bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.feed);
        bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.peek);
        return bitmaps;
    }

}
