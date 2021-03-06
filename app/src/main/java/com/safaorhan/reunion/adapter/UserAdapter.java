package com.safaorhan.reunion.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;
import com.safaorhan.reunion.model.User;

import java.util.Random;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {
    private static final String TAG = UserAdapter.class.getSimpleName();
    UserClickListener userClickListener;


    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    public UserClickListener getUserClickListener() {
        if (userClickListener == null) {
            userClickListener = new UserClickListener() {
                @Override
                public void onUserClick(DocumentReference userRef) {
                    Log.e(TAG, "You need to call setUserClickListener() to set the click listener of UserAdapter");
                }
            };
        }

        return userClickListener;
    }

    public void setUserClickListener(UserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }


    public static UserAdapter get() {
        Query query = FirestoreHelper.getUsers()
                //.orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        return new UserAdapter(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User user) {
        user.setId(getSnapshots().getSnapshot(position).getId());
        holder.bind(user);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(itemView);
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView nameText;
        TextView emailText;
        ImageView profile_image;
        TextView firstLetterTextView;

        public UserHolder(View itemView) {

            super(itemView);
            this.itemView = itemView;
            nameText = itemView.findViewById(R.id.nameText);
            emailText = itemView.findViewById(R.id.emailText);
            profile_image = itemView.findViewById(R.id.profile_image);
            firstLetterTextView = itemView.findViewById(R.id.firstLetterTextView);
        }

        public void bind(final User user) {
            nameText.setText(user.getName() + " " + user.getSurname());
            emailText.setText(user.getEmail());

            if (profile_image.getColorFilter() == null) {
                profile_image.setColorFilter(getRandomColor());
            }

            firstLetterTextView.setText(String.valueOf(user.getName().charAt(0)));

            if (user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                itemView.setOnClickListener(null);
            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUserClickListener().onUserClick(FirestoreHelper.getUserRef(user));
                    }
                });
            }
        }

        public int getRandomColor() {
            Random rand = new Random();
            int r = rand.nextInt(200);
            int g = rand.nextInt(200);
            int b = rand.nextInt(200);
            return Color.rgb(r, g, b);
        }
    }

    public interface UserClickListener {
        void onUserClick(DocumentReference userRef);
    }
}
