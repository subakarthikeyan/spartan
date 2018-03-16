package com.example.ace.spartan;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ace on 07-Feb-18.
 */

public class SharedPref {

    SharedPreferences pref ;
    SharedPreferences.Editor editor;

    Boolean login(String user,String pass,Context context){

        pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
//on the login store the login
    if(!user.equals("")&&!pass.equals("")) {
        editor.putString("userId", user);
        editor.putString("password", pass);
        editor.commit();
    return true;
    }else{
        return false;
    }
    }


}
