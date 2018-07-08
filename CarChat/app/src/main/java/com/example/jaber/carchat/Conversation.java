package com.example.jaber.carchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Friends;
import Models.MessageAdapter;
import Models.Messages;
import de.hdodenhof.circleimageview.CircleImageView;

public class Conversation extends AppCompatActivity {

    public static String loggedInUser = "";

    private Toolbar chatToolbar;

    private FirebaseDatabase database;
    private DatabaseReference mRootRef;
    private StorageReference mImageStorage;
    private DatabaseReference table_users;
    private DatabaseReference table_friends;

    private String chatUser;

    private TextView titleView;
    private CircleImageView profileImg;

    private ImageButton chatAddBtn;
    private ImageButton chatSendBtn;
    private EditText chatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;


    private static final int GALLERY_PICK = 1;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;
    private int itemPos = 0;
    private String lastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        loggedInUser = GetUserName();

        database = FirebaseDatabase.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        table_users = database.getReference("Users");
        table_friends = database.getReference("Friend");

        chatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        Bundle extras = getIntent().getExtras();
        chatUser= extras.getString("user_id");

        //getSupportActionBar().setTitle(chatUser + " " + chatName);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- custom action bar items ---- //

        titleView = (TextView) findViewById(R.id.custom_bar_title);
        profileImg = (CircleImageView)findViewById(R.id.custom_bar_img);

        chatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        chatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        chatMessageView = (EditText) findViewById(R.id.chat__message_view);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        loadMessages();

        table_friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Friends friends1 = dataSnapshot.child(loggedInUser+chatUser).getValue(Friends.class);
                Friends friends2 = dataSnapshot.child(chatUser+loggedInUser).getValue(Friends.class);
                if(friends1 != null || friends2 != null){

                    table_users.child(chatUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            String lastName = dataSnapshot.child("lastName").getValue().toString();
                            String img = dataSnapshot.child("img").getValue().toString();
                            titleView.setText(firstName + " " + lastName);
                            Picasso.with(Conversation.this).load(img).placeholder(R.drawable.default_avatar).into(profileImg);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    titleView.setText(chatUser);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        chatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;

                itemPos = 0;

                loadMoreMessages();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + loggedInUser + "/" + chatUser;
            final String chat_user_ref = "messages/" + chatUser + "/" + loggedInUser;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(loggedInUser).child(chatUser).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", loggedInUser);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        chatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void loadMoreMessages(){

        DatabaseReference messageRef = mRootRef.child("messages").child(loggedInUser).child(chatUser);

        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);
                }else{
                    mPrevKey = lastKey;
                }

                if(itemPos == 1){
                    lastKey = messageKey;
                }


                mAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);
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
    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(loggedInUser).child(chatUser);

        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();

                    lastKey = messageKey;
                    mPrevKey = messageKey;
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                swipeRefreshLayout.setRefreshing(false);
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
    }

    private void sendMessage(){

        String message = chatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String current_user_ref = "messages/" + loggedInUser + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + loggedInUser;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(loggedInUser).child(chatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", loggedInUser);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            chatMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });
        }
    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Intent conversationIntent = new Intent(Conversation.this, Conversation.class);
//        //startActivity(conversationIntent);
//
//        table_users.child("1234567").child("lastName").setValue("Amsha").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    //Intent conversationIntent = new Intent(Conversation.this, Settings.class);
//                    //startActivity(conversationIntent);
//                }
//            }
//        });
    }
}
