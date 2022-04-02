package com.example.parkingassist.models;

/**
 * This class represent a user in the app.
 **/
public class User {

    private final String m_FullName;
    private final String m_Age;
    private final String m_Email;
    private final Double m_MaxDistancePreference;
    private final Boolean m_LikesOnlyPreference;

    public User(String i_FullName, String i_Age, String i_Email, Double i_MaxDistancePreference,
                Boolean i_LikesOnlyPreference) {
        this.m_FullName = i_FullName;
        this.m_Age = i_Age;
        this.m_Email = i_Email;
        this.m_MaxDistancePreference = i_MaxDistancePreference;
        this.m_LikesOnlyPreference = i_LikesOnlyPreference;
    }

    public String FullName(){
        return m_FullName;
    }

    public String Age(){
        return m_Age;
    }

    public String Email(){
        return m_Email;
    }

    public Double MaxDistancePreference(){
        return m_MaxDistancePreference;
    }

    public Boolean LikesOnlyPreference(){
        return m_LikesOnlyPreference;
    }
}
