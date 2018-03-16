package com.example.ace.spartan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ace on 19-Dec-17.
 */

public class Home extends Fragment {
    FragmentActivity listener;
String[] credencials=new String[2];
String[] userDetails=new String[2];
ProgressDialog progressDialog;
String PatientName;
    SharedPreferences sharedPreferences;
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.home, container, false);

        return view;
    }
    public class ChangePassTask extends AsyncTask<String,Void,String>{
String phone,newpass,oldpass;

        @Override
        protected String doInBackground(String... params) {
            phone=params[0];
            oldpass=params[1];
            newpass=params[2];
            for(int i=0;i<8;i++){
                System.out.println("##################### "+phone+" ######## "+oldpass+" ######## "+newpass+"####################");
            }
            String data=changePassword(phone,newpass,oldpass);
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.contains("changed")){

                progressDialog.dismiss();
                Intent intent=new Intent(getContext(),customerLoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(getContext(),"Password Changed Successfully!",Toast.LENGTH_LONG).show();
            }else{
                progressDialog.dismiss();

                Toast.makeText(getContext(),"Something wrong...please Re-Try!",Toast.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       sharedPreferences=getContext().getSharedPreferences("MyPref",0);

        final Button changePWD=(Button)view.findViewById(R.id.Save_PWD);
        final Button Logout=(Button)view.findViewById(R.id.logout);
        final EditText pass=(EditText)view.findViewById(R.id.change_password);
        final EditText confirm_PWD=(EditText)view.findViewById(R.id.confirm_password);
        final Switch changeSwitch=(Switch)view.findViewById(R.id.PWD_Switch);
        final TextView PatientNAME=(TextView)view.findViewById(R.id.Patient_Name);

        final session_details sessionDetails=new session_details(view.getContext());
        credencials=sessionDetails.getVal();
        if(sharedPreferences.contains("userId")){
         PatientNAME.setText("  Email\n  -"+sharedPreferences.getString("userId","")+"\n\n  Name\n  -"+credencials[2]);
        }else {
            PatientNAME.setText(credencials[2]);
        }
        pass.setHintTextColor(android.R.color.holo_red_light);
        confirm_PWD.setHintTextColor(android.R.color.holo_red_light);
        progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setMessage("Please wait a moment...");
        pass.setEnabled(false);
        confirm_PWD.setEnabled(false);
        changePWD.setEnabled(false);
        changeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pass.setEnabled(true);
                    confirm_PWD.setEnabled(true);
                    changePWD.setEnabled(true);

                }else {
                    pass.setEnabled(false);
                    confirm_PWD.setEnabled(false);
                    changePWD.setEnabled(false);

                }
            }
        });

        changePWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newpwd=pass.getText().toString(),newconfirm=confirm_PWD.getText().toString();
                if(newpwd.equals(newconfirm)) {
                    if(newpwd.length()>=4) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setMessage("Are You Sure ?");
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setPositiveButton("YSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                new ChangePassTask().execute(credencials[0], credencials[1],newpwd); //credencials[0]-->phoneNumber , credencials[1]-->password

                            }
                        });
builder.show();
                    }else {
                        pass.setText("");
                        confirm_PWD.setText("");
                        Toast.makeText(getActivity(),"Password must be at least 4 characters",Toast.LENGTH_LONG).show();
                    }
                }else{
                    pass.setText("");
                    confirm_PWD.setText("");
                    Toast.makeText(getActivity(),"Password Mismatching...re-try!",Toast.LENGTH_LONG).show();
                }
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setMessage("Are You Sure ?");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getActivity(),MainActivity.class);
                        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SharedPreferences.Editor e=sharedPreferences.edit();
                        //  sessionDetails.del();
                        e.clear();
                        e.commit();
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.show();



            }
        });

    }

    private String changePassword(String user, String newpass, String oldpass) {
        Activity_Identifiers activity_identifiers=new Activity_Identifiers(getActivity());
        int check = activity_identifiers.getVal();
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_change_pass.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(4);
            if (check == 1) {
                arrayList.add(new BasicNameValuePair("lic", user));
            } else {
                arrayList.add(new BasicNameValuePair("ph_no", user));

            }
            arrayList.add(new BasicNameValuePair("oldpass", oldpass));//1-workshop login, 2-customer login
            arrayList.add(new BasicNameValuePair("newpass", newpass));
            arrayList.add(new BasicNameValuePair("dbselector", String.valueOf(check)));
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


    @Override
    public void onDetach() {
        super.onDetach();
    this.listener=null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
