package com.arunkr.postsharer.model;

/**
 * Created by arunkr on 2/9/16.
 */
public class Post
{
    String postedBy;
    int type;
    String data;

    public Post()
    {
    }

    public Post(String postedBy, int type, String data)
    {
        this.postedBy = postedBy;
        this.type = type;
        this.data = data;
    }

    public String getPostedBy()
    {
        return postedBy;
    }

    public void setPostedBy(String postedBy)
    {
        this.postedBy = postedBy;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
}
