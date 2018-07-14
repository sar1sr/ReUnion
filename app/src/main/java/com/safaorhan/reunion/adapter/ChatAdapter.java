package com.safaorhan.reunion.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.MessageViewHolder> {

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    public static ChatAdapter get(DocumentReference conversationRef) {

        Query query = FirebaseFirestore.getInstance()
                .collection("messages")
                .whereEqualTo("conversation", conversationRef)
                .orderBy("sentAt")
                .limit(200);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        return new ChatAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message message) {
        holder.bind(message);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(itemView);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView fromTextView;
        TextView messageContentTextView;
        TextView sentAtTextView;
        User fromUser;

        public MessageViewHolder(View itemView) {
            super(itemView);

            fromTextView = itemView.findViewById(R.id.fromTextView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            sentAtTextView = itemView.findViewById(R.id.sentAtTextView);
        }

        public void bind(Message message) {

            message.getFrom().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    fromUser = documentSnapshot.toObject(User.class);
                    FirestoreHelper.getMe().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User me = documentSnapshot.toObject(User.class);
                            fromTextView.setText(fromUser.getName());
                            if (!(me.getName().equalsIgnoreCase(fromUser.getName()))) {
                                fromTextView.setTextColor(Color.parseColor("#d10c1c"));
                            }
                        }
                    });
                    fromTextView.setText(fromUser.getName());
                }
            });
            messageContentTextView.setText(message.getText());
            sentAtTextView.setText(getSentAt(message));
        }

        private String getSentAt(Message message) {
            Timestamp timestamp = message.getSentAt();
            long seconds;
            if (timestamp == null) {
                Date date = new Date();
                seconds = date.getSeconds();
            } else {
                seconds = timestamp.toDate().getTime();
            }
            SimpleDateFormat formats = new SimpleDateFormat("hh:mm");
            Date myDate = new Date(seconds);
            String sentAtDates = formats.format(myDate);
            return sentAtDates;
        }
    }

}
