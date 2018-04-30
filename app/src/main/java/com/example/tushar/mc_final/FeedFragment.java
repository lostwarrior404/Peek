package com.example.tushar.mc_final;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mcurrent_user_db;
    private String mCurrentLocation;
    private ArrayList<Data> mDataList;
    private ValueEventListener mloc_event_listener;

    public Data parser(String id,String name,int file,int cols,String building,String floor,int layout_type,int frag_type,ArrayList<String> visiblity){
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
        d.setName("Your Location");
        d.setBuilding("SU");
        d.setDisplay_data(hm);
        d.setFloor("null");
        d.setLayout_type(5);
        d.setFrag_type(2);
        d.setVisiblity(temp);
        d.setKeys(k);
        templist.add(parser("location","Your Location",R.raw.boys_hostel,4,"BH","null",1,3,temp));

        visiblity = new String[]{"BH,GH"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("b_hostel","Hosteliers",R.raw.boys_hostel,4,"BH","null",1,3,temp));

        visiblity = new String[] {"BH,GH"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("g_hostel","Hosteliers",R.raw.girls_hostel,4,"BH","null",1,3,temp));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("cdx","CDX Menu",R.raw.cdx,2,"AC","0",1,1,temp));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("chai","Chai Point Menu",R.raw.chai_point,4,"NA","0",1,1,temp));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("club","Club Coordinators",R.raw.club_coordinators,4,"DB","null",1,1,temp));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("mess","Mess Menu",R.raw.mess_menu,8,"DB","2",1,1,temp));

        visiblity = new String[] {"BH,GH","DB","AC","LB","LC","SR","RE","NA"};
        temp.clear();
        temp.addAll(Arrays.asList(visiblity));
        templist.add(parser("water","Water Cooler on this floor.",R.raw.water,8,"SU","null",1,1,temp));
        return templist;
    }
    public ArrayList<Data> sort(String mCurrentLocation,ArrayList<Data> mDataList){
        String [] arr  = mCurrentLocation.split(",");
        String building = arr[0];
        String floor = arr[2];
        ArrayList<Data> templist = new ArrayList<>();
        for(int i=0;i<mDataList.size();i++){
            if(mDataList.get(i).getBuilding().equals(building) && (mDataList.get(i).getFloor().equals(floor) || floor.equals("null"))){
                templist.add(mDataList.get(i));
            }
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
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        FirebaseAuth temp_auth = FirebaseAuth.getInstance();
        String current_user_uid = temp_auth.getCurrentUser().getUid();
        mcurrent_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_uid);
        mloc_event_listener = mcurrent_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentLocation = dataSnapshot.child("mUserLocation").getValue(String.class);
                mDataList = sort(mCurrentLocation,load_data());
                recyclerView.setAdapter(new GridLayoutAdapter(mDataList));

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
        private final ListView mList;

        public GridHolder1(View itemView) {
            super(itemView);
            mTitle= (TextView) itemView.findViewById(R.id.place_menu);
            mButton=(Button)itemView.findViewById(R.id.phone_button);
            mList = (ListView)itemView.findViewById(R.id.list_menu);
        }
        public void onBind(Data param_data){
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

        }

    }
    private class GridLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int[] test = new int[] {0,1,1,0,1};
        private ArrayList<Data> mDataList;
        public GridLayoutAdapter(ArrayList<Data> mDataList) {
            this.mDataList=mDataList;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder=null;
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
            return mDataList.get(position).getLayout_type();
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }

}
