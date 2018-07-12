package com.safaorhan.reunion.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.Conversation;
import com.safaorhan.reunion.model.Message;
import com.safaorhan.reunion.model.User;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.MessageViewHolder> {

    /*
    MessageClickListener messageClickListener;

    public MessageClickListener getMessageClickListener() {

        if (messageClickListener == null) {
            messageClickListener = new MessageClickListener() {
                @Override
                public void onMessageSent(User userRef) {

                }
            };
        }
        return messageClickListener;
    }

    public void setMessageClickListener(MessageClickListener messageClickListener) {
        this.messageClickListener = messageClickListener;
    }

    */
    public MessagesAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }


    public static MessagesAdapter get(DocumentReference conversationRef) {


        Query query = FirebaseFirestore.getInstance()
                .collection("messages")
                .whereEqualTo("conversation", conversationRef)
                //.orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        return new MessagesAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message message) {
        holder.bind(message);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView fromTextView;
        TextView messageContentTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            fromTextView = itemView.findViewById(R.id.fromTextView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
        }

        public void bind(Message message) {

            message.getFrom().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    fromTextView.setText(user.getName());
                }
            });

            messageContentTextView.setText(message.getText());
        }
    }

    public interface MessageClickListener {
        void onMessageSent(User userRef);
    }

}
