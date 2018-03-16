package com.example.ace.spartan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
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

public class OtpEmailSender extends AppCompatActivity {
    private Activity_Identifiers activity_identifiers;
    EditText editText;
    Button btn;

    private String EmailAdd;
    private ProgressDialog progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_email_sender);
progressDialog1=new ProgressDialog(OtpEmailSender.this);
        progressDialog1.setCancelable(false);
            activity_identifiers=new Activity_Identifiers(this);
            btn=(Button)findViewById(R.id.BTN);
            editText=(EditText)findViewById(R.id.Email);
            forget();





        }
        public class SetPhoneOtpTask1 extends AsyncTask<String, Void, String> {
            private String email;
            @Override
            protected String doInBackground(String... params) {
                // TODO: attempt authentication against a network service.
                String data = "";
                email = params[0];

                if(!email.equals(""))
                    data = SetOtp1(email);
                for (int i = 0; i < 8; i++) {
                    System.out.println("############" + data);
                }
                return data;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            protected void onPostExecute(String result) {

                //String otp="";
                if (result.contains("success")&&!email.equals("")) {
                    progressDialog1.dismiss();

                    btn.setText("Verify");
                    Toast.makeText(getApplicationContext(),"ready", Toast.LENGTH_LONG).show();
                    otpCheck(email);
                }
                else {
                    progressDialog1.dismiss();
                    if(!email.equals(""))
                        Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    void otpCheck(final String email){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals("")){
                    progressDialog1.show();
                    new GetOTPTask1().execute(email,editText.getText().toString());
                }else{
                    Toast.makeText(OtpEmailSender.this, "It must not be Empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private String SetOtp1(String email) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/smtpMail.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(1);
            arrayList.add(new BasicNameValuePair("email", email));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContents(response);
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return origresponseText;
    }
    public class GetOTPTask1 extends AsyncTask<String, Void, String> {
        private String  OTP;
        private String email;
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String data = "";

            email=params[0];
            OTP = params[1];
            data = GetOtp1(email, OTP);
            for (int i = 0; i < 8; i++) {
                System.out.println("############" + data);
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            int check = activity_identifiers.getVal();
            //String otp="";

            if (result.contains("success")) {


                new ForgetPasswordTask1().execute(email, String.valueOf(check));
            } else {
                progressDialog1.dismiss();
                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
            }


        }
    }


    private String GetOtp1(String email,String otp) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/VerifyOTPmail.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(2);
            arrayList.add(new BasicNameValuePair("email", email));
            arrayList.add(new BasicNameValuePair("otp", otp));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost);
            origresponseText = readContents(response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origresponseText;
    }

    private void sendPasswordToPhone(String password) {
        final Intent intent = new Intent(this, customerLoginActivity.class);

        progressDialog1.dismiss();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        dialog.setCancelable(false);
        dialog.setTitle("Your Password");
        dialog.setMessage(password);
        dialog.setNegativeButton("I got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivity(intent);
                        finish();
                    }
                });
        dialog.show();

    }

    private String readContents(HttpResponse response) {
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


    public class ForgetPasswordTask1 extends AsyncTask<String, Void, String> {

        private String dbselector;
        private String email;

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String data = "";

            email=params[0];
            dbselector = params[1];
            data = GetPasswords1(email, dbselector);
            for (int i = 0; i < 8; i++) {
                System.out.println("############" + data);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            String passWord = "";
            if (!result.contains("User Not Exists")) {

                passWord = result;
                sendPasswordToPhone(passWord);


            } else {
                progressDialog1.dismiss();
                Toast.makeText(getApplicationContext(), "User Not Exists!", Toast.LENGTH_LONG).show();
            }


        }

        private String GetPasswords1(String email1, String dbselector) {
            int check = activity_identifiers.getVal();

            String origresponseText = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_Forget.php");
            try {
                ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(2);
                if (check == 1) {
                    arrayList.add(new BasicNameValuePair("lic", email1));
                } else {
                    for (int i = 0; i < 8; i++) {
                        System.out.println("############TESTING MAIL" + email1);
                    }
                    Bundle bundle=getIntent().getExtras();

                    arrayList.add(new BasicNameValuePair("email", email1));

                }
                arrayList.add(new BasicNameValuePair("dbselector", dbselector));//1-workshop login, 2-customer login
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
                HttpResponse response = httpClient.execute(httpPost);
                origresponseText = readContents(response);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return origresponseText;
        }

    }
    public class GetPhoneNumberTask1 extends AsyncTask<String, Void, String> {

        private String dbselector;
        private String email;

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String data = "";

            email = params[0];
            dbselector = params[1];
            data = Emails(email, dbselector);
            for(int i=0;i<8;i++){
                System.out.println("############"+data);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {


            if (!result.contains("Not Exists")) {

                SetPhoneOtpTask1 setPhoneOtpTask=new SetPhoneOtpTask1();
                setPhoneOtpTask.execute(EmailAdd);
                EmailAdd=email;
                //}

            } else {
                progressDialog1.dismiss();
                Toast.makeText(getApplicationContext(), "User not Exists!", Toast.LENGTH_LONG).show();
            }


        }

    }




    private String Emails(String user,String dbselector) {
        Activity_Identifiers activity_identifiers=new Activity_Identifiers(OtpEmailSender.this);
        int CHECK=activity_identifiers.getVal();
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost1 = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_Phone.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(2);
            if (CHECK == 1) {
                arrayList.add(new BasicNameValuePair("lic", user));
            } else {
                arrayList.add(new BasicNameValuePair("email", user));

            }
            arrayList.add(new BasicNameValuePair("dbselector", dbselector));//1-workshop login, 2-customer login
            httpPost1.setEntity(new UrlEncodedFormEntity(arrayList));
            HttpResponse response = httpClient.execute(httpPost1);
            origresponseText = readContents(response);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return origresponseText;
    }

    //FORGET PASSWORD
    public void forget() {
        Activity_Identifiers activity_identifiers=new Activity_Identifiers(OtpEmailSender.this);
        final int CHECK=activity_identifiers.getVal();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OtpEmailSender.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Forget Password");
        if(CHECK==1) {
            alertDialog.setMessage("Enter LicenseNumber");
        }else {
            alertDialog.setMessage("Enter Email");
        }
        final EditText input = new EditText(OtpEmailSender.this);
        if(CHECK==1){
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }else{
            input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //COUNT=0;
                        EmailAdd = input.getText().toString();
                        if(!input.getText().toString().equals("")) {
                            if (!EmailAdd.equals("")) {

                                progressDialog1.setMessage("wait...");
                                progressDialog1.show();

                                new GetPhoneNumberTask1().execute(EmailAdd, String.valueOf(CHECK));

                            } else {
                                if (CHECK == 1) {
                                    Toast.makeText(getApplicationContext(), "Please Enter the LicenseNumber to get your Password!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter the Email to get your Password!", Toast.LENGTH_LONG).show();
                                }

                            }
                        }else{
                            input.setError("Invalid Email");
                        }

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                   Intent intent=new Intent(OtpEmailSender.this,customerLoginActivity.class);

                        dialog.cancel();
                   startActivity(intent);
                   finish();
                    }
                });

        alertDialog.show();

    }
}
