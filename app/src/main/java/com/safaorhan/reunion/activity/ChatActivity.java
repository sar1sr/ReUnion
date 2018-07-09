package com.safaorhan.reunion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.firestore.DocumentReference;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;

public class ChatActivity extends AppCompatActivity {

    public final int CONVERSATION_SELECTED = 2;
    public final int NEW_CONVERSATION_SELECTED = 1;

    EditText chatBoxEditText;
    RelativeLayout chatSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatBoxEditText = findViewById(R.id.chatBoxEditText);
        chatSendButton = findViewById(R.id.chatSendButton);

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatBoxEditText.getText().toString().trim();
                sendMessage(message);
            }
        });

        chatBoxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() == 0){
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

    private void sendMessage(String message) {
        //TODO :
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONVERSATION_SELECTED) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                String id = intent.getStringExtra("idString");
                DocumentReference newConversationRef = FirestoreHelper.getConversationRefById(id);
                //TODO :
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == NEW_CONVERSATION_SELECTED) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                String id = intent.getStringExtra("idString");
                DocumentReference ConversationRef = FirestoreHelper.getConversationRefById(id);
                //TODO :
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}