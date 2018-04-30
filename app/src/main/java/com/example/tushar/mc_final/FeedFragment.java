package com.example.tushar.mc_final;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mcurrent_user_db;
    private String mCurrentLocation;
    private ArrayList<Data> mDataList;
    private ValueEventListener mloc_event_listener;
    private GridLayoutAdapter mLayoutAdapter;
    public Data parser(String id,String name,int file,int cols,String building,String floor,int layout_type,int frag_type,ArrayList<String> visiblity,Boolean hasPhone){
        String next[] = {};
        Data data = new Data();
        ArrayList<HashMap<String,String>> m = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        int count = 0;
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(file)));
            for (; ; ) {
                next = reader.readNext();
                if (next != null) {
                    if(count==0){
                        for(int i=0;i<cols;i++){
                            keys.add(next[i]);
                        }
                        count++;
                    }
                    else{

                        HashMap<String,String> temp = new HashMap<>();
                        for(int i=0;i<cols;i++){
                            temp.put(keys.get(i),next[i]);
                        }
                        m.add(temp);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.setName(name);
        data.setBuilding(building);
        data.setDisplay_data(m);
        data.setFloor(floor);
        data.setLayout_type(layout_type);
        data.setFrag_type(frag_type);
        data.setVisiblity(visiblity);
        data.setKeys(keys);
        data.setId(id);
        data.setHasPhone(hasPhone);
        return data;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    public ArrayList<Data> load_data(){
        ArrayList<Data> templist = new ArrayList<Data>();
        String[] visiblity =  new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        ArrayList<String> temp  = new ArrayList<>();
        temp.addAll(Arrays.asList(visiblity));

        Data d = new Data();
        ArrayList<String> k = new ArrayList<>();
        k.add("Location");
        ArrayList<HashMap<String,String>> hm = new ArrayList<>();
        HashMap<String,String> t = new HashMap<>();
        t.put("Location",mCurrentLocation);
        hm.add(t);
        d.setName("Your Location");
        d.setBuilding("SU");
        d.setDisplay_data(hm);
        d.setFloor("null");
        d.setLayout_type(5);
        d.setFrag_type(2);
        d.setVisiblity(temp);
        d.setKeys(k);
        d.setId("location");
        templist.add(d);

        visiblity = new String[]{"BH,GH"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("b_hostel","Hosteliers",R.raw.boys_hostel,4,"BH","null",1,3,temp,Boolean.FALSE));

        visiblity = new String[] {"BH,GH"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("g_hostel","Hosteliers",R.raw.girls_hostel,4,"BH","null",1,3,temp,Boolean.FALSE));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA","Unknown"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("cdx","CDX Menu",R.raw.cdx,2,"AC","0",1,1,temp,Boolean.TRUE));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA","Unknown"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("chai","Chai Point Menu",R.raw.chai_point,3,"NA","0",1,1,temp,Boolean.TRUE));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA","Unknown"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("club","Student Clubs",R.raw.club_coordinators,4,"DB","null",1,1,temp,Boolean.FALSE));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA","Unknown"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("mess","Mess Menu",R.raw.mess_menu,8,"DB","2",1,1,temp,Boolean.TRUE));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA","Unknown"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("water","Water Cooler on this floor.",R.raw.water,1,"SU","null",1,3,temp,Boolean.FALSE));
        return templist;
    }
    public ArrayList<Data> sort(String mCurrentLocation,ArrayList<Data> mDataList){
        String [] arr  = mCurrentLocation.split(",");
        String building = arr[0];
        String floor = arr[2];
        ArrayList<Data> templist = new ArrayList<>();

        if(mDataList.get(0).getBuilding().equals("SU")){
            templist.add(mDataList.get(0));
        }

        for(int i=0;i<mDataList.size();i++){
            if(mDataList.get(i).getBuilding().equals(building) && !(mDataList.get(i).getFloor().equals(floor) || floor.equals("null"))){
                templist.add(mDataList.get(i));
            }
        }
        for(int i=1;i<mDataList.size();i++){
            if(mDataList.get(i).getBuilding().equals("SU")){
                templist.add(mDataList.get(i));
            }
        }
        for(int i=0;i<mDataList.size();i++){
            if(!mDataList.get(i).getBuilding().equals(building) && !(mDataList.get(i).getFloor().equals(floor) || floor.equals("null")) && !mDataList.get(i).getBuilding().equals("SU") ){
                if(mDataList.get(i).getVisiblity().indexOf(arr[0])!=-1){
                    templist.add(mDataList.get(i));
                }
            }
        }
        return templist;
    }

    @Override
    public void onPause() {
        super.onPause();
        mcurrent_user_db.removeEventListener(mloc_event_listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMain);
        mDataList = sort("Unknown,Unknown,Unknown",load_data());
        mLayoutAdapter = new GridLayoutAdapter(mDataList);
        final StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mLayoutAdapter);


        FirebaseAuth temp_auth = FirebaseAuth.getInstance();
        String current_user_uid = temp_auth.getCurrentUser().getUid();
        mcurrent_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_uid);
        mloc_event_listener = mcurrent_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentLocation = dataSnapshot.child("mUserLocation").getValue(String.class);
                mDataList = sort(mCurrentLocation,load_data());
                mLayoutAdapter.setmDataList(mDataList);
                mLayoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    private class GridHolder1 extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        private final Button mButton;
        private final RecyclerView mRecyclerView;
        private final RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        public GridHolder1(View itemView) {
            super(itemView);
            mTitle= (TextView) itemView.findViewById(R.id.place_menu);
            mButton=(Button)itemView.findViewById(R.id.phone_button);
            mRecyclerView = itemView.findViewById(R.id.list_menu);
        }
        public void onBind(Data param_data){
            mTitle.setText(param_data.getName());
            ArrayList<String> temp_key = param_data.getKeys();
            ArrayList<HashMap<String,String>> temp_display = param_data.getDisplay_data();
            if(param_data.getHasPhone()){
                final String phone = temp_display.get(0).get("phone");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        startActivity(intent);
                    }
                });
            }
            else{
                mButton.setVisibility(View.INVISIBLE);
            }
            if(param_data.getId().equals("cdx")){
                ArrayList<Menu> item_list = new ArrayList<>();
                for (HashMap<String,String> s: temp_display){
                    item_list.add(new Menu(s.get(temp_key.get(0)),s.get(temp_key.get(1))));
                }
                mRecyclerView.setLayoutManager( new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                mRecyclerView.setAdapter(new CustomAdapter(item_list));
                mRecyclerView.addOnItemTouchListener(mScrollTouchListener);

            }
            else if(param_data.getId().equals("chai")){
                ArrayList<Menu> item_list = new ArrayList<>();
                for (HashMap<String,String> s: temp_display){
                    item_list.add(new Menu(s.get(temp_key.get(0)),s.get(temp_key.get(1))));
                }
                mRecyclerView.setLayoutManager( new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                mRecyclerView.setAdapter(new CustomAdapter(item_list));
                mRecyclerView.addOnItemTouchListener(mScrollTouchListener);
            }
        }
    }
    private class CustomViewHolder extends RecyclerView.ViewHolder{
        private TextView mitem1;
        private TextView mitem2;
        public CustomViewHolder(View itemView) {
            super(itemView);
            mitem1 = itemView.findViewById(R.id.item1);
            mitem2 = itemView.findViewById(R.id.item2);
        }
        public void onDataBind(Menu smenu){
            mitem1.setText(smenu.getCol1());
            mitem2.setText(smenu.getCol2());
        }

    }
    private class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private  ArrayList<Menu> mItemList;
        public CustomAdapter(ArrayList<Menu> mItemList){
            this.mItemList = mItemList;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_list, parent, false);
            RecyclerView.ViewHolder viewholder = new CustomViewHolder(view);
            return viewholder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder = (CustomViewHolder) holder;
            ((CustomViewHolder) holder).onDataBind(mItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }
    private class Menu{
        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public String getCol2() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2 = col2;
        }

        private String col1;
        private String col2;
        public Menu(String col1,String col2){
            this.col1=col1;
            this.col2=col2;
        }
    }
    private class GridHolder2 extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        private final TextView mBody;
        private final TextView mFooter;
        public GridHolder2(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.heading);
            mBody = (TextView) itemView.findViewById(R.id.location);
            mFooter = (TextView) itemView.findViewById(R.id.sub_location);
        }
        public void onBind(Data param_data){
            ArrayList<String> temp_key = param_data.getKeys();
            ArrayList<HashMap<String, String>> temp_display = param_data.getDisplay_data();
            mTitle.setText(param_data.getName());
            if(param_data.getId().equals("location")){
                HashMap<String, String> data = temp_display.get(0);
                String mlocation = data.get(temp_key.get(0));
                if(mlocation==null){
                    mlocation="Unknown,Unknown,Unknown";
                }
                String[] split_loc = mlocation.split(",");
                mBody.setText(split_loc[1]);
                String[] numNames = {
                        "Ground",
                        "1st",
                        "2nd",
                        "3rd",
                        "4th",
                        "5th",
                        "6th",
                        "7th",
                        "8th",
                        "9th",
                        "10th"
                };
                String footer = new String();
                if(!split_loc[2].equals("Unknown")){
                     footer=numNames[Integer.parseInt(split_loc[2])]+" Floor"+"\n";
                }else{
                    footer="Unknown"+"\n";
                }

                if(split_loc[0].equals("BH")){
                    footer+="Boys Hostel";
                }
                else if(split_loc[0].equals("DB")){
                    footer+="Student Centre";
                }
                else if(split_loc[0].equals("AC")||split_loc.equals("LC")){
                    footer+="Old Academic Building";
                }
                else if(split_loc[0].equals("LB")){
                    footer+="Library Building";
                }
                else if(split_loc[0].equals("SR")){
                    footer+="Service Block";
                }
                else if(split_loc[0].equals("RE")){
                    footer+="Faculty Residence";
                }
                else if(split_loc[0].equals("GH")){
                    footer+="Girls Hostel";
                }
                else if(split_loc[0].equals("NA")){
                    footer+="New Academic Building";
                }
                mFooter.setText(footer);
            }

        }
    }
    private class GridHolder3 extends RecyclerView.ViewHolder {
        private final ImageView mImage;
        private final TextView mDescript;
        public GridHolder3(View itemView) {
            super(itemView);
            mImage = (ImageView)itemView.findViewById(R.id.coolerImg);
            mDescript = (TextView)itemView.findViewById(R.id.coolerText);
        }
        public void onBind(Data param_data){
            mDescript.setText(param_data.getName());
            ArrayList<String> temp_key = param_data.getKeys();
            ArrayList<HashMap<String,String>> temp_display = param_data.getDisplay_data();
            if(param_data.getId().equals("b_hostel")){
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.cooler));
            }


        }
    }
    private class GridHolder4 extends RecyclerView.ViewHolder {
        private final Button mButton;
        private final TextView mText;
        public GridHolder4(View itemView) {
            super(itemView);
            mButton=itemView.findViewById(R.id.callButton);
            mText = itemView.findViewById(R.id.callText);
        }
        public void onBind(Data param_data){
            mText.setText(param_data.getName());
            ArrayList<String> temp_key = param_data.getKeys();
            ArrayList<HashMap<String,String>> temp_display = param_data.getDisplay_data();
            final String phone = temp_display.get(0).get("phone");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });

        }

    }
    private class GridLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<Data> mDataList;
        public GridLayoutAdapter(ArrayList<Data> mDataList) {
            this.mDataList=mDataList;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder=null;
            Log.d("FRAG",""+viewType);
            if(viewType==1){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.context_menu, parent, false);
                viewHolder = new GridHolder1(view);
            }
            else if(viewType==2){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.context_your_location, parent, false);
                viewHolder = new GridHolder2(view);

            }
            else if(viewType==3){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.context_cooler, parent, false);
                viewHolder = new GridHolder3(view);

            }
            else if(viewType==4){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.context_contact, parent, false);
                viewHolder = new GridHolder4(view);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position)==1){
                holder= (GridHolder1)holder;
                ((GridHolder1) holder).onBind(mDataList.get(position));
            }
            else if(getItemViewType(position)==2){
                holder= (GridHolder2)holder;
                ((GridHolder2) holder).onBind(mDataList.get(position));
            }
            else if(getItemViewType(position)==3){
                holder= (GridHolder3)holder;
                ((GridHolder3) holder).onBind(mDataList.get(position));
            }
            else if(getItemViewType(position)==4){
                holder= (GridHolder4)holder;
                ((GridHolder4) holder).onBind(mDataList.get(position));
            }
        }
        @Override
        public int getItemViewType(int position){
            return mDataList.get(position).getFrag_type();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
        public void setmDataList(ArrayList<Data> mDataList){
            this.mDataList=mDataList;
        }
    }

}
