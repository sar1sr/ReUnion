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
import com.safaorhan.reunion.adapter.ChatAdapter;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.User;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ChatAdapter chatAdapter;
    DocumentReference conversationRef;
    RecyclerView recyclerView;

    EditText chatBoxEditText;
    RelativeLayout chatSendButton;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String id = getIntent().getStringExtra("id");
        conversationRef = FirestoreHelper.getConversationRefById(id);

        chatAdapter = ChatAdapter.get(conversationRef);

        setTitleOfActivity(conversationRef);

        chatBoxEditText = findViewById(R.id.chatBoxEditText);
        chatSendButton = findViewById(R.id.chatSendButton);
        recyclerView = findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

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

        linearLayoutManager.scrollToPosition(chatAdapter.getItemCount() - 1);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void onChatSendButtonClicked(final RelativeLayout chatSendButton) {
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = chatBoxEditText.getText().toString().trim();
                FirestoreHelper.sendMessage(message, conversationRef);
                chatBoxEditText.setText("");
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
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
        chatAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }

}