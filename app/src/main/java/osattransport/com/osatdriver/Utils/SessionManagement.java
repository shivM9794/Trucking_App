package osattransport.com.osatdriver.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import osattransport.com.osatdriver.Main_Navigation_activity;

/**
 * Created by MAC2 on 02-Mar-18.
 */

public class SessionManagement {

    public Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String MYPREFERENCES="User_Session";
    private static final String Is_Login="Is_Logged_In";
    private static final String Key_Phone="Phone_no";
    private static final String User_Type="User_Type";
    private static final String User_ID="User_ID";
    private static final String Show_Slider="Show_Slider";

    int Private_Mode=0;

    public SessionManagement(Context context)
    {
        this.context=context;
        pref=context.getSharedPreferences(MYPREFERENCES,Private_Mode);
        editor=pref.edit();

    }

    public void checkLogin()
    {
        Intent intent;
        // Check login status
        if(this.isLoggedIn())
        {

            if(getUserType().equals("DRIVER"))
            {
                intent=new Intent(context,Main_Navigation_activity.class);
                context.startActivity(intent);
            }

            //      Toast.makeText(context,"Not log in",Toast.LENGTH_LONG).show();
        }

    }
    public boolean isLoggedIn()
    {

        return pref.getBoolean(Is_Login,false);
    }


    public void createLoginSession(String phone, String usertype)
    {
        try
        {
            editor.putBoolean(Is_Login, true);
            editor.putString(Key_Phone, phone);
            editor.putString(User_Type,usertype);

            Log.e("usertpe",usertype);
        }
        catch (Exception ee)
        {
            Log.e("sign up type ee",ee.toString());
        }
        editor.commit();

    }

    public void destroySession()
    {
        try
        {
            editor.putBoolean(Is_Login, false);
            editor.putString(Key_Phone, "");
            editor.putString(User_Type,"");
            UserSession.userType="";
            UserSession.userID="0";
            UserSession.name="";
            UserSession.address="";
            UserSession.city="";
            UserSession.photopath="";
            UserSession.tempPhotoPath="";
            UserSession.emailID="";
            UserSession.isActive="";
            UserSession.state="";
            UserSession.company="";


            // Log.e("usertpe",usertype);
        }
        catch (Exception ee)
        {
            Log.e("logout session",ee.toString());
        }
        editor.commit();

    }


    public void showSlider()
    {
        editor.putBoolean(Show_Slider,false);
        editor.commit();
    }

    public Boolean isShowSlider()
    {
        return  pref.getBoolean(Show_Slider,true);
    }

    public int getUserID()
    {
        return  pref.getInt(User_ID,0);
    }

    public String getMobile()
    {
        return  pref.getString(Key_Phone,"");
    }

    public void setUserID(int i)
    {
        editor.putInt(User_ID,i);
        editor.commit();
    }

    public void setUserType(String s)
    {
        editor.putString(User_Type,s);
        editor.commit();
    }
    public String getUserType()
    {
        return  pref.getString(User_Type,"");
    }


}
