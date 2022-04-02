package com.example.parkingassist.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {

    private static final DatabaseReference s_DbRefLocations =
            FirebaseDatabase.getInstance().getReference("ParkingLocation");
    private static final DatabaseReference s_DbRefUsers =
            FirebaseDatabase.getInstance().getReference("Users");
    private static final FirebaseUser m_User = FirebaseAuth.getInstance().getCurrentUser();
    private static final String m_UserID = FirebaseDB.FirebaseUser().getUid();
    private static final DatabaseReference s_UserRef = s_DbRefUsers.child(m_UserID);




    public static String UserID(){
        assert m_UserID != null;
        return m_UserID;
    }

    public static FirebaseUser FirebaseUser(){
        assert m_User != null;
        return m_User;
    }

    public static DatabaseReference UserRef()
    {
        return s_UserRef;
    }

    public static DatabaseReference DbRefLocations()
    {
        return s_DbRefLocations;
    }

    public static DatabaseReference DbRefUsers()
    {
        return s_DbRefUsers;
    }
}
