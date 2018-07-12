package com.safaorhan.reunion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.firestore.DocumentReference;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.adapter.MessagesAdapter;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    MessagesAdapter messagesAdapter;
    DocumentReference conversationRef;
    RecyclerView recyclerView;

    EditText chatBoxEditText;
    RelativeLayout chatSendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String id = getIntent().getStringExtra("id");
        conversationRef = FirestoreHelper.getConversationRefById(id);

        messagesAdapter = MessagesAdapter.get(conversationRef);

        chatBoxEditText = findViewById(R.id.chatBoxEditText);
        chatSendButton = findViewById(R.id.chatSendButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);


        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatBoxEditText.getText().toString().trim();
                FirestoreHelper.sendMessage(message, conversationRef); //TODO : ???
                chatBoxEditText.setText("");
            }
        });


        chatBoxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    chatSendButton.setEnabled(false);
                } else {
                    chatSendButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesAdapter.stopListening();
    }

}