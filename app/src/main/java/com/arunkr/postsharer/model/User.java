package com.arunkr.postsharer.model;

/**
 * Created by arunkr on 2/9/16.
 */
public class User
{
    String displayName;

    public User()
    {
    }

    public User(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
}
