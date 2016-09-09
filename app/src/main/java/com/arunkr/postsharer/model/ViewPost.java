package com.arunkr.postsharer.model;

/**
 * Created by arunkr on 9/9/16.
 */
public class ViewPost
{
    String postedBy; //
    int type;
    String data; //data=msg if type is TEXT, data=link of image if type is image_type
    String profile_pic; //null if profile pic is empty, else it contains the link

    public ViewPost(int type)
    {
        this.type = type;
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

    public String getProfile_pic()
    {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic)
    {
        this.profile_pic = profile_pic;
    }
}
