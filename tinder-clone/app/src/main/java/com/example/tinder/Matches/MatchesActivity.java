package com.example.tinder.Matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Adapter mMatchesAdapter;
    private LayoutManager mMatchesLayoutManager;
    private String currentUserId;
    private FirebaseUser mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        mAuth=FirebaseAuth.getInstance().getCurrentUser();
        currentUserId= mAuth.getUid();

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        recyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataMatches(),MatchesActivity.this);
        recyclerView.setAdapter(mMatchesAdapter);
        getUserMatchId();
    }

    private void getUserMatchId() {
        DatabaseReference matchDb= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Connections").child("Matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot match:dataSnapshot.getChildren()){
                    FetchMatchInformation(match.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchesActivity.this,"error:",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name1 = "";
                    String profileImageUrl1 = "";

                    if(dataSnapshot.child("name").getValue()!=null){
                        name1=dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("ProfileImageUrl").getValue()!=null){
                        profileImageUrl1=dataSnapshot.child("ProfileImageUrl").getValue().toString();
                    }

                    MatchesObject obj=new MatchesObject(userId,name1,profileImageUrl1);
                    resultMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchesActivity.this,"error:"+databaseError,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private ArrayList<MatchesObject> resultMatches=new ArrayList<MatchesObject>();

    private List<MatchesObject> getDataMatches() {
        return resultMatches;
    }
}
