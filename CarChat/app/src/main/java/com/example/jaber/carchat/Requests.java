package com.example.jaber.carchat;


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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Requests extends Fragment {

    private View mMainView;
    private RecyclerView mFriendsList;
    private RecyclerView mFriendsRequestList;

    private FirebaseDatabase database;
    private DatabaseReference table_users;
    private DatabaseReference table_friends;
    private DatabaseReference table_friendsRequest;



    public static String loggedInUser = "";


    public Requests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_requests, container, false);

        loggedInUser = GetUserName();

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mFriendsRequestList = (RecyclerView) mMainView.findViewById(R.id.friendsRequest_list);

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users");
        table_friends = database.getReference("Friends").child(loggedInUser);
        table_friendsRequest = database.getReference("FriendsRequests").child(loggedInUser);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriendsRequestList.setHasFixedSize(true);
        mFriendsRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<NewFriends, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewFriends, UsersViewHolder>(
                NewFriends.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                table_friends
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, NewFriends model, int position) {

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message", "Remove Friend"};
                        AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    Intent profileIntent = new Intent(getContext(), Profile.class);
                                    profileIntent.putExtra("user_id", list_user_id);
                                    startActivity(profileIntent);
                                }
                                if(which == 1){
                                    Intent conversationIntent = new Intent(getContext(), Conversation.class);
                                    conversationIntent.putExtra("user_id", list_user_id);
                                    startActivity(conversationIntent);
                                }
                                if(which == 2){
                                    table_friends.child(list_user_id).removeValue();
                                    database.getReference("Friends").child(list_user_id).child(loggedInUser).removeValue();
                                    database.getReference("Friend").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    Friends friends = snapshot.getValue(Friends.class);
                                                    if(friends.getFirstUserID().equals(loggedInUser) && friends.getSecondUserID().equals(list_user_id) || friends.getFirstUserID().equals(list_user_id) && friends.getSecondUserID().equals(loggedInUser)){
                                                        database.getReference("Friend").child(snapshot.getKey()).removeValue();
                                                    }
                                                }
                                                }catch (Exception ex){
                                                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });

                table_users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String img = dataSnapshot.child("img").getValue().toString();
                        viewHolder.setName(firstName+ " " + lastName);
                        viewHolder.setStatus(status);
                        viewHolder.setImg(img, getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        FirebaseRecyclerAdapter<NewFriendsRequest, UsersViewHolder2> req = new FirebaseRecyclerAdapter<NewFriendsRequest, UsersViewHolder2>(

                NewFriendsRequest.class,
                R.layout.request_single_layout,
                UsersViewHolder2.class,
                table_friendsRequest
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder2 viewHolder, NewFriendsRequest model, int position) {

                final String list_user_id = getRef(position).getKey();

                viewHolder.accBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        table_friendsRequest.child(list_user_id).removeValue();
                        database.getReference("FriendsRequest").child(list_user_id+loggedInUser).removeValue();

                        Friends friend = new Friends(loggedInUser, list_user_id);
                        database.getReference("Friend").child(loggedInUser+list_user_id).setValue(friend);

                        table_users.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Users me = dataSnapshot.child(loggedInUser).getValue(Users.class);
                                Users friend = dataSnapshot.child(list_user_id).getValue(Users.class);

                                NewFriends newFriends = new NewFriends(me.getFirstName() + " " + me.getLastName());
                                database.getReference("Friends").child(list_user_id).child(loggedInUser).setValue(newFriends);

                                NewFriends newFriends2 = new NewFriends(friend.getFirstName() + " " + friend.getLastName());
                                database.getReference("Friends").child(loggedInUser).child(list_user_id).setValue(newFriends2);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.decBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.getReference("FriendsRequest").child(list_user_id+loggedInUser).removeValue();
                        table_friendsRequest.child(list_user_id).removeValue();                    }
                });

                viewHolder.setCarNumber(list_user_id);
            }
        };
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
        mFriendsRequestList.setAdapter(req);
    }

    public static class UsersViewHolder extends ViewHolder{
        View mView;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView statusView = mView.findViewById(R.id.user_single_status);
            statusView.setText(status);
        }

        public void setImg(String img, Context ctx){
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(img).placeholder(R.drawable.default_avatar).into(userImageView);
        }



    }

    public static class UsersViewHolder2 extends ViewHolder{
        View mView;
        Button accBtn;
        Button decBtn;
        public UsersViewHolder2(View itemView){
            super(itemView);
            mView = itemView;
            accBtn = mView.findViewById(R.id.request_single_accBtn);
            decBtn = mView.findViewById(R.id.request_single_decBtn);
        }

        public void setCarNumber(String carNumber){
            TextView userNameView = mView.findViewById(R.id.request_single_carNumber);
            userNameView.setText(carNumber);
        }

    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }

}
