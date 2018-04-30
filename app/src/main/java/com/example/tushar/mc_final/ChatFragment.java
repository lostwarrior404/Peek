package com.example.tushar.mc_final;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private Button fab;
    private EditText input;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private User currentUser;
    //private ListView messageList;
    private RecyclerView recyclerView;
    private ArrayList<ChatMessage> messageList;
    private Spinner spinnerLocation;
    private String locationToSee;
    private CardView cardView;
    private String currentLocation="null";
    private recyler_adapter_chat adapter;
    private FirebaseDatabase firebaseDatabase;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    private boolean isStart = false;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        fab = (Button) view.findViewById(R.id.fab);
        input = (EditText) view.findViewById(R.id.input);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL)
        {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // no line
            }
        });
        messageList = new ArrayList<>();
        adapter = new recyler_adapter_chat(messageList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //getMessages();

        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_heading);
        cardView = (CardView) view.findViewById(R.id.cardMessage);

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(30);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                locationToSee = spinnerLocation.getSelectedItem().toString();
                Log.d("SPINTAG", "onItemSelected: " + locationToSee);


                messageList.clear();
                if (databaseReference!=null && childEventListener!=null)
                    databaseReference.removeEventListener(childEventListener);
                getMessages();
                adapter = new recyler_adapter_chat(messageList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                locationToSee = "Boys Hostel Old";
            }
        });
        isStart = true;
        mAuth = FirebaseAuth.getInstance();
        //getUserInfo();
        //getMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushText();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(valueEventListener);
        databaseReference.removeEventListener(childEventListener);
    }

    public void getMessages()
    {

        getUserInfo();
        databaseReference = firebaseDatabase.getReference("chat");
        childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                if (locationToSee.equals(message.getMessageLocation()))
                {
                    Log.d("MESSTAG", "onChildAdded: " + message.getMessageText());
                    messageList.add(message);
                }

                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class recyler_adapter_chat extends RecyclerView.Adapter<recyler_adapter_chat.MyViewHolder>
    {

        ArrayList<ChatMessage> messageList;

        public recyler_adapter_chat(ArrayList<ChatMessage> messageList) {
            this.messageList = messageList;
        }



        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatFragment.recyler_adapter_chat.MyViewHolder holder, final int position) {

            if (messageList.get(position).getMessageUser().equals(mAuth.getCurrentUser().getDisplayName()))
            {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                layoutParams.setMargins(0,0,20,0);
                holder.cardMessage.setCardBackgroundColor(Color.parseColor("#189ad3"));
                holder.cardMessage.setLayoutParams(layoutParams);
            }
            else
            {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutParams.setMargins(20,0,0,0);
                holder.cardMessage.setCardBackgroundColor(Color.WHITE);
                holder.cardMessage.setLayoutParams(layoutParams);
            }
            holder.messageText.setText(messageList.get(position).getMessageText());
            holder.messageUser.setText(messageList.get(position).getMessageUser());



        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView messageText;
            TextView messageUser;
            CardView cardMessage;


            public MyViewHolder(View itemView) {
                super(itemView);
                messageText  = (TextView) itemView.findViewById(R.id.message_text);
                messageUser = (TextView) itemView.findViewById(R.id.message_user);
                cardMessage = (CardView) itemView.findViewById(R.id.cardMessage);
            }
        }
    }

    public void disableInput()
    {
        input.setEnabled(false);
        input.setHint("Not in location");
        fab.setEnabled(false);

    }

    public void enableInput()
    {
        input.setEnabled(true);
        input.setHint("Type here");
        fab.setEnabled(true);
    }

    public void getUserInfo()
    {

        //    childEventListener = databaseReference.addChildEventListener(new ChildEventListener()
        valueEventListener = FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if (snapshot.getValue(User.class).getmEmail().equals(mAuth.getCurrentUser().getEmail()))
                    {
                        currentUser = snapshot.getValue(User.class);
                        Log.d("LOCTAG", "onDataChange: "+currentUser.getmUserLocation());
                        // set spinner location to current location
                        currentLocation = "null";
                        if (currentUser.getmUserLocation().split(",")[0].equals("BH"))
                        {
                            currentLocation = "Boys Hostel Old";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("DB"))
                        {
                            currentLocation = "Student Centre";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("AC") || currentUser.getmUserLocation().split(",")[0].equals("LC") || currentUser.getmUserLocation().split(",")[0].equals("NA"))
                        {
                            currentLocation = "Academic Building";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("LB") || currentUser.getmUserLocation().split(",")[0].equals("SR"))
                        {
                            currentLocation = "Library Building";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("GH"))
                        {
                            currentLocation = "Girls Hostel";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("RE"))
                        {
                            currentLocation = "Faculty Residence";
                        }
                        Log.d("MCD", "onDataChange: "+currentLocation+","+locationToSee);

                        if (currentLocation.equals(locationToSee))
                        {
                            enableInput();
                        }
                        else
                        {
                            disableInput();
                        }

                        if (isStart==true)
                        {
                            for (int j=0 ; j < spinnerLocation.getCount() ; j++)
                            {
                                if (spinnerLocation.getItemAtPosition(j).equals(currentLocation))
                                {
                                    spinnerLocation.setSelection(j);
                                    break;
                                }

                            }
                            isStart = false;
                        }



                        //locationToSee = currentLocation;


                        Log.d("USERTAG", "onDataChange: "+currentUser.toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setSpinner()
    {

        for (int j = 0 ; j < spinnerLocation.getCount() ; j++)
        {
            if (spinnerLocation.getItemAtPosition(j).equals(currentLocation))
            {
                spinnerLocation.setSelection(j);
                break;
            }

        }
    }

    public void pushText()
    {
        if (currentLocation.equals(locationToSee))
        {
            if (!input.getText().toString().trim().equals(""))
            {
                ChatMessage message = new ChatMessage(input.getText().toString(),currentLocation,mAuth.getCurrentUser().getDisplayName());
                FirebaseDatabase.getInstance().getReference("chat").push().setValue(message);
                input.setText("");
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
               recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
            else
            {
                Toast.makeText(getActivity(), "Please enter text", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
