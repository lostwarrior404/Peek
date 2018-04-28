package com.example.tushar.mc_final;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FloatingActionButton fab;
    private EditText input;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private User currentUser;
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView messageList;
    private Spinner spinnerLocation;
    private String locationToSee;
    private CardView cardView;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        input = (EditText) view.findViewById(R.id.input);
        messageList = (ListView) view.findViewById(R.id.list_of_messages);
        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_heading);
        cardView = (CardView) view.findViewById(R.id.cardMessage);



        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(30);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                locationToSee = spinnerLocation.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        mAuth = FirebaseAuth.getInstance();
        getUserInfo();
        displayChatMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushText();
            }
        });
        return view;
    }

    public void displayChatMessages()
    {

        Query query = FirebaseDatabase.getInstance().getReference("chat");
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>().setQuery(query,ChatMessage.class).setLayout(R.layout.message).build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText  = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                CardView cardMessage = (CardView) v.findViewById(R.id.cardMessage);
                //TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                Log.d("USERTAG", "onDataChange: "+messageText.getText() + messageUser.getText());
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                if (messageUser.getText().equals(mAuth.getCurrentUser().getDisplayName()))
                {
                    Log.d("CHATTAG", "populateView: " + model.getMessageUser());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    layoutParams.setMargins(0,0,20,0);
                    cardMessage.setCardBackgroundColor(Color.parseColor("#189ad3"));
                    cardMessage.setLayoutParams(layoutParams);
                }
            }
        };
        messageList.setAdapter(adapter);
    }

    public void getUserInfo()
    {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if (snapshot.getValue(User.class).getmEmail().equals(mAuth.getCurrentUser().getEmail()))
                    {
                        currentUser = snapshot.getValue(User.class);
                        currentUser.setmUserLocation("Boys hostel 3rd floor C wing");
                        // set spinner location to current location
                        String currentLocation = null;
                        if (currentUser.getmUserLocation().equals("Boys hostel 3rd floor C wing"))
                        {
                            currentLocation = "Old Hostel";
                        }


                        for (int i = 0 ; i < spinnerLocation.getCount() ; i++)
                        {
                            if (spinnerLocation.getItemAtPosition(i).equals(currentLocation))
                            {
                                spinnerLocation.setSelection(i);
                                break;
                            }
                        }
                        Log.d("USERTAG", "onDataChange: "+currentUser.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pushText()
    {
        ChatMessage message = new ChatMessage(input.getText().toString(),currentUser.getmUserLocation(),mAuth.getCurrentUser().getDisplayName());
        FirebaseDatabase.getInstance().getReference("chat").push().setValue(message);
        input.setText("");
    }

}
