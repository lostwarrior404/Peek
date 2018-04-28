package com.example.tushar.mc_final;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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

public class Chat extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText input;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private User currentUser;
    private FirebaseListAdapter<ChatMessage> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        mAuth = FirebaseAuth.getInstance();
        getUserInfo();
        displayChatMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushText();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void displayChatMessages()
    {
        ListView messageList = (ListView) findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference("chat");
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>().setQuery(query,ChatMessage.class).setLayout(R.layout.message).build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText  = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                //TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                Log.d("USERTAG", "onDataChange: "+messageText.toString());
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
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
        if (!input.getText().toString().trim().equals(""))
            FirebaseDatabase.getInstance().getReference("chat").push().setValue(message);
        input.setText("");
    }
}
