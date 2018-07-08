package com.example.jaber.carchat;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Models.Messages;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Models.Friends;
import Models.NewFriends;
import Models.NewFriendsRequest;
import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private View mMainView;
    private RecyclerView chats_list;

    private FirebaseDatabase database;
    private DatabaseReference table_users;
    private DatabaseReference table_messages;

    private static String loggedInUser = "";


    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_chat, container, false);

        loggedInUser = GetUserName();

        chats_list = (RecyclerView) mMainView.findViewById(R.id.chats_list);

        database = FirebaseDatabase.getInstance();
        table_messages = database.getReference("messages").child(loggedInUser);
        table_users = database.getReference("Users");


        chats_list.setHasFixedSize(true);
        chats_list.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Messages, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Messages, UsersViewHolder>(
                Messages.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                table_messages
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Messages model, int position) {
                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery = table_messages.child(list_user_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                          viewHolder.setMessage(data);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                table_users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String firstName = dataSnapshot.child("firstName").getValue().toString();
                        final String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String img = dataSnapshot.child("img").getValue().toString();


                        viewHolder.setName(firstName + " " + lastName);
                        viewHolder.setImg(img, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getContext(), Conversation.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                startActivity(chatIntent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                table_users.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String firstName = dataSnapshot.child("firstName").getValue().toString();
//                        String lastName = dataSnapshot.child("lastName").getValue().toString();
//                        String status = dataSnapshot.child("status").getValue().toString();
//                        String img = dataSnapshot.child("img").getValue().toString();
//                        viewHolder.setName(firstName+ " " + lastName);
//                        viewHolder.setStatus(status);
//                        viewHolder.setImg(img, getContext());
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                //viewHolder.setName(model.getFrom());
                //viewHolder.setImg(model.get);
            }
        };
        chats_list.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setMessage(String message){
            TextView messsageText = mView.findViewById(R.id.user_single_status);
            messsageText.setText(message);
        }

        public void setImg(String img, Context ctx){
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(img).placeholder(R.drawable.default_avatar).into(userImageView);
        }



    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }

}
