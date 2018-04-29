package com.example.tushar.mc_final;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button fab;
    private EditText input;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private User currentUser;
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView messageList;
    private Spinner spinnerLocation;
    private String locationToSee;
    private CardView cardView;
    private String currentLocation="null";

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
        fab = (Button) view.findViewById(R.id.fab);
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
                onStop();
                messageList.destroyDrawingCache();
                messageList.setVisibility(ListView.INVISIBLE);
                messageList.setVisibility(ListView.VISIBLE);
                onStart();
                adapter.notifyDataSetChanged();
                if (currentLocation.equals(locationToSee))
                    enableInput();
                else
                    disableInput();
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
                if (model.getMessageLocation().equals(locationToSee))
                {
                    //TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());


                    Log.d("USERTAG", "onDataChange: "+model.getMessageLocation() + locationToSee);



                    if (messageUser.getText().equals(mAuth.getCurrentUser().getDisplayName()))
                    {
                        Log.d("CHATTAG", "populateView: " + model.getMessageUser() + model.getMessageText());
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                        layoutParams.setMargins(0,0,20,0);
                        cardMessage.setCardBackgroundColor(Color.parseColor("#189ad3"));
                        cardMessage.setLayoutParams(layoutParams);
                    }
                    else
                    {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                        layoutParams.setMargins(20,0,0,0);
                        cardMessage.setCardBackgroundColor(Color.WHITE);
                        cardMessage.setLayoutParams(layoutParams);
                    }
                }

                else
                {
                    messageText.setText("");
                    messageUser.setText("");
                    cardMessage.setVisibility(View.GONE);
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
                        Log.d("LOCTAG", "onDataChange: "+currentUser.getmUserLocation());
                        // set spinner location to current location
                        currentLocation = null;
                        if (currentUser.getmUserLocation().split(",")[0].equals("BH"))
                        {
                            currentLocation = "Boys Hostel Old";
                        }
                        else if (currentUser.getmUserLocation().split(",")[0].equals("DB"))
                        {
                            currentLocation = "Student Center";
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

                        locationToSee = currentLocation;

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
               messageList.smoothScrollToPosition(adapter.getCount());
            }
            else
            {
                Toast.makeText(getActivity(), "Please enter text", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
