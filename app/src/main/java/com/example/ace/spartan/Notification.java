package com.example.ace.spartan;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ace on 19-Dec-17.
 */

public class Notification extends Fragment {
    FragmentActivity listener;
    TextView hospitalDetail,routeMap;
    HashMap<String,String> hashMap;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.notification, container, false);

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.listener=(FragmentActivity)context;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txt=(TextView) view.findViewById(R.id.notificationTEXT);
        hospitalDetail=(TextView)view.findViewById(R.id.hospitalDetails);
        routeMap=(TextView)view.findViewById(R.id.routeMap);
        txt.setText("NOTIFICATION PAGE");
        session_details s=new session_details(getContext());
progressDialog=new ProgressDialog(getContext());
progressDialog.setCancelable(false);
progressDialog.show();
    new GetDetailTask().execute(s.getVal()[0]);


    routeMap.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(listener, "Google Map will be here!", Toast.LENGTH_SHORT).show();
    }
});
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener=null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    class GetTokenNumber extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
String TID=strings[0];
String HOSP=strings[1];
String DATE=strings[2];
String data=getTokenNum(TID,HOSP,DATE);
            return data;
        }

        @Override
        protected void onPostExecute(String s){
            if(!s.contains("failed")){
                if(hashMap!=null) {
                    hospitalDetail.setText(" Hospital : " + hashMap.get("3") + "\n\n Token No : " + s + "\n Date to Check-Up : " + hashMap.get("5") + "\n\n Address : " + hashMap.get("4"));
                }else{hospitalDetail.setText("Nothing You have registered!");}
progressDialog.dismiss();
            }else {
                progressDialog.dismiss();
                Toast.makeText(listener, "Nothing You have Registered!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
    private String getTokenNum(String tid,String hosp,String date) {

        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_TokenNumber.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(3);
            arrayList.add(new BasicNameValuePair("tid", tid));
            arrayList.add(new BasicNameValuePair("hosp", hosp));
            arrayList.add(new BasicNameValuePair("date", date));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));

            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContent(response);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origresponseText;
    }
class GetDetailTask extends AsyncTask<String,Void,String>{

    @Override
    protected String doInBackground(String... strings) {
        String email=strings[0];
        String data=getDetail(email);
        return data;
    }

    @Override
    protected void onPostExecute(String s){
        JSONObject jsonObject;
        hashMap=new HashMap<>();
        if(!s.contains("failed")){
for(int i=0;i<=9;i++){
    System.out.println("********************"+s);
}

            try {
                jsonObject = new JSONObject(s);
     hashMap.put(""+0,jsonObject.getString("0"));
                hashMap.put(""+1,jsonObject.getString("1"));
                hashMap.put(""+2,jsonObject.getString("2"));
                hashMap.put(""+3,jsonObject.getString("3"));
                hashMap.put(""+4,jsonObject.getString("4"));
                hashMap.put(""+5,jsonObject.getString("5"));
} catch (JSONException e) {
                e.printStackTrace();
            }
System.out.println("##########################"+hashMap.get("0"));
            System.out.println("##########################"+hashMap.get("1"));
            System.out.println("##########################"+hashMap.get("2"));
            System.out.println("##########################"+hashMap.get("3"));
            System.out.println("##########################"+hashMap.get("4"));
            System.out.println("##########################"+hashMap.get("5"));

new GetTokenNumber().execute(hashMap.get("0"),hashMap.get("3"),hashMap.get("5"));
            Toast.makeText(listener, "Success Details!", Toast.LENGTH_SHORT).show();
        }else {
     progressDialog.dismiss();
            Toast.makeText(listener, "Nothing Details!", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(s);
    }
}
    private String getDetail(String email) {

        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_PatientTokenDetails.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(1);
            arrayList.add(new BasicNameValuePair("email", email));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));

            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContent(response);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origresponseText;
    }

    private String readContent(HttpResponse response) {
        String text = "";
        InputStream in = null;

        try {
            in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }

        return text;
    }
}
