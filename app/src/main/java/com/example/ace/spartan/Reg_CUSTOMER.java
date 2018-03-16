package com.example.ace.spartan;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by ace on 02-Jan-18.
 */

public class Reg_CUSTOMER extends Fragment {


    Button reg;
    View view;
    UserSignUpTask userSignUpTask;
ProgressDialog dialog;
String phoneNum,Name,password,EmailAddr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reg_customer, container, false);
        super.onCreate(savedInstanceState);

        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText L_P = (EditText) view.findViewById(R.id.PHONE);
        final EditText pass = (EditText) view.findViewById(R.id.password);
        final EditText confirmPASS = (EditText) view.findViewById(R.id.confirm);
final EditText email=(EditText)view.findViewById(R.id.Email);

        reg = (Button) view.findViewById(R.id.sign_up_button);
        reg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                PhoneNumberUtils phoneNumberUtils=new PhoneNumberUtils();
                if (!name.getText().toString().isEmpty() &&!email.getText().toString().isEmpty()&& !pass.getText().toString().isEmpty() && !confirmPASS.getText().toString().isEmpty() && !L_P.getText().toString().isEmpty()) {
if(L_P.getText().toString().length()==10){
                    if(pass.getText().toString().equals(confirmPASS.getText().toString())) {
                        if(pass.getText().toString().length()>=4){
                        if(email.getText().toString().contains(".com")&&email.getText().toString().contains("@")) {
                            if (!pass.getText().toString().contains(" ") && !L_P.getText().toString().contains(" ")) {
                                dialog = new ProgressDialog(view.getContext());
                                dialog.setCancelable(false);
                                dialog.setMessage("Wait a Moment...");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setCancelable(false);
                                builder.setTitle("OTP verification");
                                builder.setMessage("Enter OTP");

                                final EditText editText = new EditText(getContext());
                                LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                editText.setLayoutParams(linearLayout);
                                builder.setView(editText);

                                Name = name.getText().toString();
                                password = pass.getText().toString();
                                phoneNum = L_P.getText().toString();
                             EmailAddr=email.getText().toString();

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which) {
                                        dialog1.cancel();//AlertDialog
                                    }
                                });
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which) {
                                        dialog.show();//progress
                                        new GetOTPTask().execute(EmailAddr, editText.getText().toString());
                                    }
                                });
                                builder.show();
                                for(int i=0;i<8;i++){
                                    System.out.println("################# SET OTP: "+EmailAddr);
                                }
                                new SetPhoneOtpTask().execute(EmailAddr);
                            } else {
                                Toast.makeText(view.getContext(), "Don't use any spaces other than your Name!!!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            email.setError("Invalid Email!");
//                            Toast.makeText(view.getContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                            pass.setError("Passworo should be At least 4 characters");
                        }
                    }
                    else {
  pass.setError("Password Missmatching");
                      //  Toast.makeText(view.getContext(),"Password Missmatching!!!",Toast.LENGTH_LONG).show();
                    }
}
else {
    L_P.setError("Invalid Phone Number");
    //Toast.makeText(view.getContext(),"Invalid Phone Number!!!",Toast.LENGTH_LONG).show();
}
                }
                else {
                    Toast.makeText(view.getContext(), "Please fill all the details!", Toast.LENGTH_LONG).show();
                }
            }

        });
        return view;
    }



    public class UserSignUpTask extends AsyncTask<String, Void, String> {

        private String ph_LIC;
        private String Password;
        private String dbselector;
        private String Name;
private String Email;
        @Override
        protected String doInBackground(String... params) {
            String data = "";
            dialog.show();
            // TODO: attempt authentication against a network service.
                Name = params[0];
                ph_LIC = params[1];
                Password = params[2];
                dbselector = params[3];
                Email =params[4];
                data = sendHttpRequest(Name, ph_LIC, Password, Email,dbselector);

            return data;
        }

        @Override
        protected void onPostExecute(String result) {


for(int i=0;i<8;i++){
    System.out.println("##############"+result);
}
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

    private String sendHttpRequest(String Name, String ph, String pass,String email ,String dbselector) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_Reg.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(5);
            arrayList.add(new BasicNameValuePair("name", Name));
            arrayList.add(new BasicNameValuePair("ph_no", ph));
            arrayList.add(new BasicNameValuePair("pass", pass));
            arrayList.add(new BasicNameValuePair("email", email));
            arrayList.add(new BasicNameValuePair("dbselector", dbselector));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContent(response);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return origresponseText;
    }


    String readContent(HttpResponse response) {
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
    public class SetPhoneOtpTask extends AsyncTask<String, Void, String> {
        private String email;
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String data = "";
            email = params[0];
            data = SetOtp(email);
            for (int i = 0; i < 8; i++) {
                System.out.println("############" + data);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Activity_Identifiers activity_identifiers = new Activity_Identifiers(getContext());
            int check = activity_identifiers.getVal();
            //String otp="";
            if (result.contains("success")) {

                Toast.makeText(getContext(),"Ready",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
    private String SetOtp(String email) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/smtpMail.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(1);
            arrayList.add(new BasicNameValuePair("email",email));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContent(response);
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return origresponseText;
    }
    public class GetOTPTask extends AsyncTask<String, Void, String> {
        private String phone,OTP;

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String data = "";

            phone = params[0];
            OTP=params[1];
            data = GetOtp(phone,OTP);
            for (int i = 0; i < 8; i++) {
                System.out.println("############" + data);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Activity_Identifiers activity_identifiers=new Activity_Identifiers(getContext());
            int check = activity_identifiers.getVal();
            //String otp="";
            if (result.contains("success")) {

new UserSignUpTask().execute(Name,phoneNum,password,String.valueOf(2),EmailAddr);
            } else {
dialog.dismiss();
                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_LONG).show();
            }


        }
    }

    private String GetOtp(String email,String otp) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/VerifyOTPmail.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(2);
            arrayList.add(new BasicNameValuePair("email", email));
            arrayList.add(new BasicNameValuePair("otp", otp));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContent(response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origresponseText;
    }
}