package com.safaorhan.reunion.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.adapter.MessagesAdapter;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.User;

public class ChatActivity extends AppCompatActivity {

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

        setTitleOfActivity(conversationRef);

        chatBoxEditText = findViewById(R.id.chatBoxEditText);
        chatSendButton = findViewById(R.id.chatSendButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);

        onChatSendButtonClicked(chatSendButton);

        chatBoxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                chatSendButton.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    chatSendButton.setEnabled(true);
                } else {
                    chatSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void onChatSendButtonClicked(RelativeLayout chatSendButton) {
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatBoxEditText.getText().toString().trim();
                FirestoreHelper.sendMessage(message, conversationRef);
                chatBoxEditText.setText("");
                messagesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setTitleOfActivity(DocumentReference conversationRef) {
        conversationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Conversation conversation = documentSnapshot.toObject(Conversation.class);
                conversation.getOpponent().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String title = user.getName();
                        setTitle(title);
                    }
                });
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