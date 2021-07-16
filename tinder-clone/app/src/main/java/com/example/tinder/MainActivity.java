package com.example.tinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.tinder.Cards.Cards;
import com.example.tinder.Cards.arrayAdapter;
import com.example.tinder.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   private Cards cards_data[];
   private arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private Button logout;


    SwipeFlingAdapterView flingContainer;
    private DatabaseReference usersDb;

    ListView listView;
    List<Cards> rowItems;
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout=findViewById(R.id.logout);
        mAuth=FirebaseAuth.getInstance();
        currentUid=mAuth.getCurrentUser().getUid();

        usersDb=FirebaseDatabase.getInstance().getReference().child("Users");



        checkUserSex();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent=new Intent(MainActivity.this,ChooseLoginRegistrationActivity.class);
                startActivity(intent);
                finish();
                return ;
            }
        });


        rowItems = new ArrayList<Cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView)findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                Cards obj=(Cards)dataObject;
                String userId=obj.getUserId();
                usersDb.child(userId).child("Connections").child("nope").child(currentUid).setValue(true);
                Toast.makeText(MainActivity.this,"left",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj=(Cards)dataObject;
                String userId=obj.getUserId();
                usersDb.child(userId).child("Connections").child("yeps").child(currentUid).setValue(true);
                isConnectionMatch(userId);

                Toast.makeText(MainActivity.this,"right:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this,"click :",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnections = usersDb.child(currentUid).child("Connections").child("yeps").child(userId);
        currentUserConnections.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this,"new Connections",Toast.LENGTH_SHORT).show();
                    String key=FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("Connections").child("Matches").child(currentUid).child("chatId").setValue(key);
                    usersDb.child(currentUid).child("Connections").child("Matches").child(dataSnapshot.getKey()).child("chatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String usersex;
    private String oppositeusersex;
    public  void checkUserSex(){
        final FirebaseUser user=mAuth.getCurrentUser();
        DatabaseReference userDb= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               if(dataSnapshot.getKey().equals(user.getUid())){
                   if(dataSnapshot.exists()){
                       if(dataSnapshot.child("sex")!=null){
                           usersex=dataSnapshot.child("sex").getValue().toString();
                           switch (usersex){
                               case "Male":
                                   oppositeusersex="Female";
                                   break;
                               case "Female":
                                   oppositeusersex="Male";
                                   break;
                           }
                           getOppositesexUser();
                       }
                   }
               }


           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


            }

    public void getOppositesexUser(){

        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

               if(dataSnapshot.exists()&&!dataSnapshot.child("Connections").child("nope").hasChild(currentUid)&&
                       !dataSnapshot.child("Connections").child("yeps").hasChild(currentUid)&&
                       dataSnapshot.child("sex").getValue().toString().equals(oppositeusersex)){
                   String profileImageUrl="default";
//                   if(!dataSnapshot.child("ProfileImageUrl").getValue().toString().equals("default")){
//                       profileImageUrl=dataSnapshot.child("ProfileImageUrl").getValue().toString();
//                   }

                   if(dataSnapshot.child("ProfileImageUrl").getValue()!=null){
                       if(!dataSnapshot.child("ProfileImageUrl").getValue().equals("default")){
                           profileImageUrl=dataSnapshot.child("ProfileImageUrl").getValue().toString();
                       }
                   }
                   Cards Item=new Cards(dataSnapshot.getKey(),dataSnapshot.child("name").getValue().toString(),profileImageUrl);
                   rowItems.add(Item);
                   arrayAdapter.notifyDataSetChanged();
               }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void goTosettings(View view) {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);

        startActivity(intent);
        return ;
    }

    public void goToMatches(View view) {
        Intent intent=new Intent(MainActivity.this,MatchesActivity.class);

        startActivity(intent);
        return ;
    }
}
