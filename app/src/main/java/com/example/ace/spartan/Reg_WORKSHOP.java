package com.example.ace.spartan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ace on 02-Jan-18.
 */

public class Reg_WORKSHOP extends Fragment {

    EditText NewEdit;
    EditText NewEdit1;
    EditText NewEdit2;
    Button reg;
    View view;
    UserSignUpTask userSignUpTask;
    ProgressDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reg_workshop, container, false);
        super.onCreate(savedInstanceState);
NewEdit=(EditText)view.findViewById(R.id.WorkShopName);
NewEdit1=(EditText)view.findViewById(R.id.STREET);
NewEdit2=(EditText)view.findViewById(R.id.PIN);
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText L_P = (EditText) view.findViewById(R.id.LICENSE);
        final EditText pass = (EditText) view.findViewById(R.id.password);
        final EditText confirmPASS = (EditText) view.findViewById(R.id.confirm);

        reg = (Button) view.findViewById(R.id.sign_up_button);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!name.getText().toString().isEmpty() && !pass.getText().toString().isEmpty() && !confirmPASS.getText().toString().isEmpty() && !L_P.getText().toString().isEmpty() && !NewEdit.getText().toString().isEmpty() && !NewEdit1.getText().toString().isEmpty() && !NewEdit2.getText().toString().isEmpty()) {

                    if(pass.getText().toString().equals(confirmPASS.getText().toString())){
                 if(NewEdit2.getText().toString().length()>=6&&NewEdit2.getText().toString().length()<=10){
                     if(!pass.getText().toString().contains(" ")&&!L_P.getText().toString().contains(" ")&&!NewEdit1.getText().toString().contains(" ")&&!NewEdit2.getText().toString().contains(" ")) {
                         dialog = new ProgressDialog(view.getContext());
                         dialog.setMessage("Wait a Moment...");
                         dialog.show();
                         userSignUpTask = (UserSignUpTask) new UserSignUpTask().execute(name.getText().toString(), L_P.getText().toString(), pass.getText().toString(), NewEdit.getText().toString(), NewEdit1.getText().toString(), NewEdit2.getText().toString(), String.valueOf(1));
                     }else {
                         Toast.makeText(view.getContext(),"Don't use any spaces other than your Name!!!",Toast.LENGTH_LONG).show();
                     }
                     }else {
                     Toast.makeText(view.getContext(),"Invalid PIN Code!!!",Toast.LENGTH_LONG).show();
                 }
                 }else {
                     Toast.makeText(view.getContext(),"Password Missmatching!!!",Toast.LENGTH_LONG).show();
                 }

                } else {
                    Toast.makeText(view.getContext(), "Please fill all the details", Toast.LENGTH_LONG).show();
                }


            }

        });
return view;
    }


    public class UserSignUpTask extends AsyncTask<String, Void, String> {

        private  String ph_LIC;
        private  String Password;
        private String dbselector;
        private String Name;
        private String Street;
        private String PIN;
        private String ShopName;

        @Override
        protected String doInBackground(String... params) {
            String data="";
            // TODO: attempt authentication against a network service.
                Name=params[0];
                ph_LIC=params[1];
                Password=params[2];
                ShopName=params[3];
                Street=params[4];
                PIN=params[5];
                dbselector=params[6];
                data = sendHttpRequest( Name,ph_LIC,Password,ShopName,Street,PIN,dbselector);


            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            userSignUpTask = null;

            if (result.contains("Done")) {
                dialog.dismiss();
                Intent intent = new Intent(view.getContext(), customerLoginActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

                Toast.makeText(view.getContext(),  result , Toast.LENGTH_LONG).show();

            }
            if(result.contains("Already")) {
                dialog.dismiss();
                Intent intent=new Intent(view.getContext(),view.getContext().getClass());
                ///    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(view.getContext(), result, Toast.LENGTH_LONG).show();

            }
            if(result.contains("Failed")||result.contains("Wrong")){
                dialog.dismiss();
                Toast.makeText(view.getContext(), "SomeThing Wrong! Try Again!", Toast.LENGTH_LONG).show();

            }
        }


    }


    private String sendHttpRequest( String Name,String LIC,String pass,String shop,String street,String PIN,String dbselector) {
        String origresponseText="";
        HttpClient httpClient  = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_Reg.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(7);
            arrayList.add(new BasicNameValuePair("name",Name));
            arrayList.add(new BasicNameValuePair("lic",LIC));
            arrayList.add(new BasicNameValuePair("pass",pass));
            arrayList.add(new BasicNameValuePair("shopname",shop));
            arrayList.add(new BasicNameValuePair("street",street));
            arrayList.add(new BasicNameValuePair("pin",PIN));
            arrayList.add(new BasicNameValuePair("dbselector",dbselector));
            httpPost.setEntity(new UrlEncodedFormEntity( arrayList));
            HttpResponse response= httpClient.execute(httpPost);
            origresponseText=readContent(response);
            for (int i=0;i<9;i++){
                System.out.println("#################OUTPUT:"+LIC);
                System.out.println("RESPONSE: "+origresponseText);
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return origresponseText;
    }

    String readContent(HttpResponse response)
    {
        String text = "";
        InputStream in =null;

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
        }
        finally {
            try {

                in.close();
            } catch (Exception ex) {
            }
        }

        return text;
    }
}
