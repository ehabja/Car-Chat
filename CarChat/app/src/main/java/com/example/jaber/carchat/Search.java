package com.example.jaber.carchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import Models.Friends;
import Models.FriendsRequest;
import Models.NewFriends;
import Models.NewFriendsRequest;
import Models.Users;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment{

    ImageButton search;
    EditText txtSearch;
    LinearLayout amLayout;
    Button confirm;
    Button msg;
    TextView people;

    private View mMainView;
    private String user_id;

    private FirebaseDatabase database;
    private DatabaseReference table_users;
    private DatabaseReference table_friends;
    private DatabaseReference newtable_friends;
    private DatabaseReference table_friendsRequest;
    private DatabaseReference newtable_friendsRequest;
    private DatabaseReference table_notifications;

    public static String loggedInUser = "";

    public Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView =  inflater.inflate(R.layout.fragment_search, container, false);

        search = (ImageButton) mMainView.findViewById(R.id.btnSearch);
        txtSearch = (EditText) mMainView.findViewById(R.id.txtSearch);
        amLayout = (LinearLayout) mMainView.findViewById(R.id.log);
        confirm = (Button) mMainView.findViewById(R.id.btnAddFriend);
        msg = (Button) mMainView.findViewById(R.id.btnMessage);
        people = (TextView) mMainView.findViewById(R.id.txtPeople);


        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users");
        table_friends = database.getReference("Friend");
        newtable_friends = database.getReference("Friends");
        table_friendsRequest = database.getReference("FriendsRequest");
        newtable_friendsRequest = database.getReference("FriendsRequests");
        table_notifications = database.getReference().child("notifications");

        try{
            init();
        }catch (Exception ex){
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Inflate the layout for this fragment
        return mMainView;
    }

    void init(){
        loggedInUser = GetUserName();

        amLayout.setVisibility(mMainView.INVISIBLE);
        people.setVisibility(mMainView.INVISIBLE);


        table_users.child(loggedInUser).child("online").child("true");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirm.getText().toString().toUpperCase().equals("PENDING")){
                    table_friendsRequest.child(loggedInUser+txtSearch.getText().toString()).removeValue();
                    newtable_friendsRequest.child(txtSearch.getText().toString()).child(loggedInUser).removeValue();
                    confirm.setText("ADD FRIEND");
                }else if(confirm.getText().toString().toUpperCase().equals("CONFIRM")){
                    confirmFriendRequest();
                    table_friendsRequest.child(txtSearch.getText().toString()+loggedInUser).removeValue();
                    newtable_friendsRequest.child(loggedInUser).child(txtSearch.getText().toString()).removeValue();
                    confirm.setText("REMOVE FRIEND");
                }else if(confirm.getText().toString().toUpperCase().equals("REMOVE FRIEND")){
                    removeFriend();
                    newRemoveFriend();
                    confirm.setText("ADD FRIEND");
                }else if(confirm.getText().toString().toUpperCase().equals("ADD FRIEND")){
                    sendFriendRequest();
                    confirm.setText("PENDING");
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isValid()){
                    Toast.makeText(getActivity(),"This is'nt a car number", Toast.LENGTH_LONG).show();
                    amLayout.setVisibility(getView().INVISIBLE);
                    people.setVisibility(getView().INVISIBLE);
                }
                else{
                    isExist();
                }
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent conversationIntent = new Intent(getContext(), Conversation.class);
                conversationIntent.putExtra("user_id", txtSearch.getText().toString());
                startActivity(conversationIntent);
            }
        });
    }

    public void isExist(){
        table_users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users friend = dataSnapshot.child(txtSearch.getText().toString()).getValue(Users.class);
                if (friend != null ) {
                    if(loggedInUser.equals(txtSearch.getText().toString()) ){
                        Toast.makeText(getActivity(), "You can't add yourself!", Toast.LENGTH_LONG).show();
                        amLayout.setVisibility(getView().INVISIBLE);
                        people.setVisibility(getView().INVISIBLE);
                    }else{
                        people.setText(friend.getFirstName() + " " + friend.getLastName());
                        table_friendsRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                try {
                                    FriendsRequest friendsRequest1 = dataSnapshot1.child(loggedInUser+txtSearch.getText().toString()).getValue(FriendsRequest.class);
                                    FriendsRequest friendsRequest2 = dataSnapshot1.child(txtSearch.getText().toString()+loggedInUser).getValue(FriendsRequest.class);
                                    if(friendsRequest1 != null){
                                        Toast.makeText(getActivity(), "Pending", Toast.LENGTH_SHORT).show();
                                        confirm.setText("Pending");
                                        msg.setText("Send Message");
                                        amLayout.setVisibility(getView().VISIBLE);
                                        people.setVisibility(getView().VISIBLE);
                                    }
                                    else if(friendsRequest2 != null){
                                        Toast.makeText(getActivity(), "Confirm", Toast.LENGTH_SHORT).show();
                                        confirm.setText("Confirm");
                                        msg.setText("Send Message");
                                        amLayout.setVisibility(getView().VISIBLE);
                                        people.setVisibility(getView().VISIBLE);
                                    }else{
                                        table_friends.addListenerForSingleValueEvent(new ValueEventListener() {
                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                try {
                                                    Friends friends1 = dataSnapshot2.child(loggedInUser+txtSearch.getText().toString()).getValue(Friends.class);
                                                    Friends friends2 = dataSnapshot2.child(txtSearch.getText().toString()+loggedInUser).getValue(Friends.class);
                                                    if(friends1 != null ){
                                                        Toast.makeText(getActivity(), "REMOVE", Toast.LENGTH_SHORT).show();
                                                        confirm.setText("Remove Friend");
                                                        msg.setText("Send Message");
                                                        amLayout.setVisibility(getView().VISIBLE);
                                                        people.setVisibility(getView().VISIBLE);
                                                    }
                                                    else if(friends2 != null ){
                                                        Toast.makeText(getActivity(), "REMOVE", Toast.LENGTH_SHORT).show();
                                                        confirm.setText("Remove Friend");
                                                        msg.setText("Send Message");
                                                        amLayout.setVisibility(getView().VISIBLE);
                                                        people.setVisibility(getView().VISIBLE);
                                                    }else{
                                                        Toast.makeText(getActivity(), "ADDFRIEND", Toast.LENGTH_SHORT).show();
                                                        confirm.setText("Add Friend");
                                                        msg.setText("Send Message");
                                                        amLayout.setVisibility(getView().VISIBLE);
                                                        people.setVisibility(getView().VISIBLE);
                                                    }
                                                }catch (Exception ex){ }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) { }
                                        });
                                    }
                                }catch (Exception ex){
                                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }
                } else {
                    amLayout.setVisibility(getView().INVISIBLE);
                    people.setVisibility(getView().INVISIBLE);
                    Toast.makeText(getActivity(), "This Username Doesn't Exist!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public boolean isValid(){

        if(txtSearch.getText().length() != 8 && txtSearch.getText().length() != 7){
            return false;
        }
        for(int i = 0; i < txtSearch.getText().length(); i++) {
            if (txtSearch.getText().charAt(i) < '0' || txtSearch.getText().charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public void sendFriendRequest(){
        FriendsRequest friendReq = new FriendsRequest(loggedInUser, txtSearch.getText().toString());
        table_friendsRequest.child(loggedInUser+txtSearch.getText().toString()).setValue(friendReq);

        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        NewFriendsRequest newFriendsRequest = new NewFriendsRequest(dateFormatter.format(now).toString());
        newtable_friendsRequest.child(txtSearch.getText().toString()).child(loggedInUser).setValue(newFriendsRequest);

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", loggedInUser);
        notificationData.put("type", "request");

        //user_id = txtSearch.getText().toString();
        table_notifications.child(txtSearch.getText().toString()).push().setValue(notificationData);

    }

    public void confirmFriendRequest(){
        Friends friend = new Friends(loggedInUser, txtSearch.getText().toString());
        table_friends.child(loggedInUser+txtSearch.getText().toString()).setValue(friend);

        table_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users me = dataSnapshot.child(loggedInUser).getValue(Users.class);
                Users friend = dataSnapshot.child(txtSearch.getText().toString()).getValue(Users.class);

                NewFriends newFriends = new NewFriends(me.getFirstName() + " " + me.getLastName());
                newtable_friends.child(txtSearch.getText().toString()).child(loggedInUser).setValue(newFriends);

                NewFriends newFriends2 = new NewFriends(friend.getFirstName() + " " + friend.getLastName());
                newtable_friends.child(loggedInUser).child(txtSearch.getText().toString()).setValue(newFriends2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void removeFriend(){
        table_friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Friends friends = snapshot.getValue(Friends.class);
                        if(friends.getFirstUserID().equals(loggedInUser) && friends.getSecondUserID().equals(txtSearch.getText().toString()) || friends.getFirstUserID().equals(txtSearch.getText().toString()) && friends.getSecondUserID().equals(loggedInUser)){
                            table_friends.child(snapshot.getKey()).removeValue();
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

    public void newRemoveFriend(){
        newtable_friends.child(loggedInUser).child(txtSearch.getText().toString()).removeValue();
        newtable_friends.child(txtSearch.getText().toString()).child(loggedInUser).removeValue();
    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }
}