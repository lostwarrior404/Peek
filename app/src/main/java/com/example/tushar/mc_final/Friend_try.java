//package com.example.tushar.mc_final;
//
//
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class Friend_try extends Fragment {
//
//    ArrayList arrayList;
//    RecyclerView rv;
//    RecyclerViewAdapter adapter;
//    GridLayoutManager manager;
//    public Friend_try() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_friend_try, container, false);
//        rv = view.findViewById(R.id.recyclerView);
//
//        arrayList = new ArrayList();
//        arrayList.add(new DataModel("Item 1", R.drawable.chat, "#09A9FF"));
//        arrayList.add(new DataModel("Item 2", R.drawable.chat, "#3E51B1"));
//        arrayList.add(new DataModel("Item 3", R.drawable.chat, "#673BB7"));
//        arrayList.add(new DataModel("Item 4", R.drawable.chat, "#4BAA50"));
//        arrayList.add(new DataModel("Item 1", R.drawable.chat, "#09A9FF"));
//        arrayList.add(new DataModel("Item 2", R.drawable.chat, "#3E51B1"));
//        arrayList.add(new DataModel("Item 3", R.drawable.chat, "#673BB7"));
//        arrayList.add(new DataModel("Item 4", R.drawable.chat, "#4BAA50"));
//        arrayList.add(new DataModel("Item 1", R.drawable.chat, "#09A9FF"));
//        arrayList.add(new DataModel("Item 2", R.drawable.chat, "#3E51B1"));
//        arrayList.add(new DataModel("Item 3", R.drawable.chat, "#673BB7"));
//        arrayList.add(new DataModel("Item 4", R.drawable.chat, "#4BAA50"));
//        arrayList.add(new DataModel("Item 1", R.drawable.chat, "#09A9FF"));
//        arrayList.add(new DataModel("Item 2", R.drawable.chat, "#3E51B1"));
//        arrayList.add(new DataModel("Item 3", R.drawable.chat, "#673BB7"));
//        arrayList.add(new DataModel("Item 4", R.drawable.chat, "#4BAA50"));
//        adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), arrayList);
//        rv.setAdapter(adapter);
//        manager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
//        rv.setLayoutManager(manager);
//        return view;
//    }
//
//}
//
//
//
//class DataModel {
//
//    public String text;
//    public int drawable;
//    public String color;
//
//    public DataModel(String t, int d, String c )
//    {
//        text=t;
//        drawable=d;
//        color=c;
//    }
//}
//
//
//
//class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
//
//    ArrayList mValues;
//    Context mContext;
//    protected ItemListener mListener;
//
//    public RecyclerViewAdapter(Context context, ArrayList values) { //, ItemListener itemListener) {
//
//        mValues = values;
//        mContext = context;
////        mListener=itemListener;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        public TextView textView;
//        public ImageView imageView;
//        public RelativeLayout relativeLayout;
//        DataModel item;
//
//        public ViewHolder(View v) {
//
//            super(v);
//
//            v.setOnClickListener(this);
//            textView = (TextView) v.findViewById(R.id.textView);
//            imageView = (ImageView) v.findViewById(R.id.imageView);
//            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
//
//        }
//
//        public void setData(DataModel item) {
//            this.item = item;
//
//            textView.setText(item.text);
//            imageView.setImageResource(item.drawable);
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));
//
//        }
//
//
////        @Override
//        public void onClick(View view) {
////            if (mListener != null) {
////                mListener.onItemClick(item);
////            }
//        }
//    }
//
//    @Override
//    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);
//
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.setData((DataModel) mValues.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//
//        return mValues.size();
//    }
//
//    public interface ItemListener {
//        void onItemClick(DataModel item);
//    }
//}
//
//
//
////class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener {
////
////    RecyclerView recyclerView;
////    ArrayList arrayList;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////
////        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
////        arrayList = new ArrayList();
////        arrayList.add(new DataModel("Item 1", R.drawable.chat, "#09A9FF"));
////        arrayList.add(new DataModel("Item 2", R.drawable.chat, "#3E51B1"));
////        arrayList.add(new DataModel("Item 3", R.drawable.chat, "#673BB7"));
////        arrayList.add(new DataModel("Item 4", R.drawable.chat, "#4BAA50"));
//////        arrayList.add(new DataModel("Item 5", R.drawable.three_d, "#F94336"));
//////        arrayList.add(new DataModel("Item 6", R.drawable.terraria, "#0A9B88"));
////
////        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arrayList, this);
////        recyclerView.setAdapter(adapter);
////
////
////        /**
////         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
////         **/
////
////        /*AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
////        recyclerView.setLayoutManager(layoutManager);*/
////
////
////        /**
////         Simple GridLayoutManager that spans two columns
////         **/
////        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
////        recyclerView.setLayoutManager(manager);
////    }
////
////    @Override
////    public void onItemClick(DataModel item) {
////
////        Toast.makeText(getApplicationContext(), item.text + " is clicked", Toast.LENGTH_SHORT).show();
////
////    }
////}