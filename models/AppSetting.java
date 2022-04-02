package com.example.parkingassist.models;

public class AppSetting {

    private static float s_DistancePreference;
    private static boolean s_LikesPreference;

    public static float getDistancePreference()
    {
        return s_DistancePreference;
    }

    public static void setDistancePreference(float i_value)
    {
        s_DistancePreference = i_value;
    }

    public static boolean getLikesPreference()
    {
        return s_LikesPreference;
    }

    public static void setLikesPreference(boolean i_value)
    {
        s_LikesPreference = i_value;
    }

}
