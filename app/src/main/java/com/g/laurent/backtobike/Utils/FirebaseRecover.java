package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;



public class FirebaseRecover {

    DatabaseReference databaseReferenceUsers;

    public FirebaseRecover(Context context) {
        FirebaseApp.initializeApp(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUsers= databaseReference.child("users");

    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------------- RECOVER WORKMATES --------------------------------------------
    // ----------------------------------------------------------------------------------------------

    public void recover_list_workmates(String user_id){

        DatabaseReference databaseReferenceInvitation = databaseReferenceUsers.child(user_id).child("invitations");

        databaseReferenceInvitation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*databaseReferenceWorkmates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datas) {

                for(DataSnapshot id : datas.getChildren()){


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    // ----------------------------------------------------------------------------------------------
    // ------------------------------- RECOVER DATAS FROM 1 WORKMATE --------------------------------
    // ----------------------------------------------------------------------------------------------

 /*   private Workmate create_workmate_with_firebase_datas(DataSnapshot datas){

        List<String> list_resto_liked = new ArrayList<>();

        for (DataSnapshot datas_child : datas.child("list_resto_liked").getChildren())
            list_resto_liked.add((String) datas_child.getValue());

        return new Workmate(
                (String) datas.child("name").getValue(),
                (String) datas.child("id").getValue(),
                (String) datas.child("photo_url").getValue(),
                (Boolean) datas.child("chosen").getValue(),
                (String) datas.child("resto_id").getValue(),
                (String) datas.child("resto_name").getValue(),
                (String) datas.child("resto_address").getValue(),
                (String) datas.child("resto_type").getValue(),
                list_resto_liked);
    }*/


}
