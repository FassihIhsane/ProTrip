package com.example.protrip.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DB {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    //private DatabaseReference users = database.getReference(Constant.USERS);

    private DB(){}

    public static String getKey(String reference){

        return DB.getReference(reference)
                 .push()
                 .getKey();
    }

    public static DatabaseReference getReference(String reference){

        return database.getReference(reference);
    }

}
