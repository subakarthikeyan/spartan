package com.example.ace.spartan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

public class ReceptionistTabbedActivity extends AppCompatActivity {
    IntentIntegrator qrScan;
   static View rootview;
    static TextView txt,verified;
String email,hospital,dt;
   static String QRresult="";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private HashMap hashMap;
    private String tokenNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
       final SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPref",0);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(ReceptionistTabbedActivity.this);
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

                        SharedPreferences.Editor share=sharedPreferences.edit();
                        share.clear();
                        share.commit();
                        Intent intent=new Intent(ReceptionistTabbedActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
           txt.setText("");
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
QRresult=result.getContents();

String detail=result.getContents();
 email="";
 hospital="";
for(int i=0;i<detail.length();i++){
if(detail.charAt(i)!='\n'){
    email+=detail.charAt(i);
}
else {
    break;
}
}
int cnt=0,k=0;

                    for(int i=0;i<detail.length();i++){
    if(detail.charAt(i)!='\n'){
        detail.replace(String.valueOf(detail.charAt(i)),"");

    }
    if(detail.charAt(i)=='\n'){
        cnt++;
    }
    if(cnt==2){
        int match=0;
        for(int j=i+1;j<detail.length();j++){
            if(detail.charAt(j)!='\n') {
                hospital += detail.charAt(j);
            match=0;
            }else {
                match=1;
                k=j;
            break;
            }
        }
        if(match==1){

            break;

        }
    }
}

 dt="";

                    cnt=0;
for(int i=k;i<detail.length();i++){
    if(detail.charAt(i)=='\n'){
        cnt++;
    }
    if(cnt==2){

k=i;
break;
    }
}
                    for(int j=k+1;j<detail.length();j++){

                        dt+=detail.charAt(j);

                    }
dt=dt.replaceAll("Check-Up Date: ","");
                    hospital=hospital.replaceAll("Hospital: ","");
email=email.replaceAll("Email: ","");
                    email=email.replaceAll(" ","");
for(int i=0;i<8;i++)
System.out.println("###########################################EMAIL: "+email+"#################HOSPITAL: "+hospital+"###################DATE : "+dt);

                    new GetDetailTask().execute(email);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receptionist_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private IntentIntegrator qrScan;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        ProgressDialog progressDialog1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

           rootview = null;
           int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);//INDEX of selected TAB
switch (sectionNumber){
    case 1:  rootview= page1(inflater,container);

        break;
    case 2:   rootview=page2(inflater,container);
    break;
    case 3:rootview = page3(inflater,container);
    break;
}
            return rootview;
        }
     View page1(LayoutInflater inflater,ViewGroup container){
            View rootView=inflater.inflate(R.layout.fragment_receptionist_tabbed, container, false);
            Button scan=(Button)rootView.findViewById(R.id.SCAN);
verified=(TextView)rootView.findViewById(R.id.VERIFIED);
verified.setVisibility(View.INVISIBLE);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            textView.setText("Scan Patient");
          txt=(TextView)rootView.findViewById(R.id.USERDETAILS);
         txt.setText("Click the above scan button!");
            qrScan = new IntentIntegrator((Activity) rootView.getContext());
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
QRresult="";

                    txt.setText("Wait...");
                    qrScan.initiateScan();

                }
            });

return rootView;
        }
        View page2(LayoutInflater inflater,ViewGroup container){
           View rootView = inflater.inflate(R.layout.reception_mainpage, container, false);
            Button generate=(Button)rootView.findViewById(R.id.GENERATE);
            final EditText Text=(EditText)rootView.findViewById(R.id.WordToGenerate);
            final ImageView QR=(ImageView)rootView.findViewById(R.id.QRcode);
            generate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Text.getText().toString().equals("")) {
                        Bitmap myBitmap = net.glxn.qrgen.android.QRCode.from(Text.getText().toString()).bitmap();
                        QR.setImageBitmap(myBitmap);
                        System.out.println("******************************" + myBitmap + "**********************************");
                    }else {
                        Text.setError("Should not be Empty");
                        QR.setImageBitmap(null);
                    }
                }
            });
            return rootView;
        }
        View page3(LayoutInflater inflater,ViewGroup container){
            View rootView = inflater.inflate(R.layout.recep_profile, container, false);
            final TextView Name,Email,Hospital;
            final Button Save;
            final EditText Password,ConfirmPassword;
            Switch ChangeSwitch;
            Name=(TextView)rootView.findViewById(R.id.NAME);
            Email=(TextView)rootView.findViewById(R.id.EMAIL);
            Hospital=(TextView)rootView.findViewById(R.id.HOSPITAL);
            Save=(Button)rootView.findViewById(R.id.SavePASS);
            Password=(EditText)rootView.findViewById(R.id.PASS);
            ConfirmPassword=(EditText)rootView.findViewById(R.id.CONFIRM_PASS);
            ChangeSwitch=(Switch)rootView.findViewById(R.id.CHANGE_PASS);
            Password.setEnabled(false);
            ConfirmPassword.setEnabled(false);
            Save.setEnabled(false);
            session_details s=new session_details(rootView.getContext());
            final String[] cred={s.getVal()[0],s.getVal()[1],s.getVal()[2]};

            Name.setText(Name.getText().toString()+cred[2]);
            Email.setText(Email.getText().toString()+cred[0]);
            ChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Password.setEnabled(true);
                        ConfirmPassword.setEnabled(true);
                        Save.setEnabled(true);
                    }else {
                        Password.setEnabled(false);
                        ConfirmPassword.setEnabled(false);
                        Save.setEnabled(false);
                    }
                }
            });
            Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Password.getText().toString().isEmpty()&&!ConfirmPassword.getText().toString().isEmpty()) {
                        if (Password.getText().toString().equals(ConfirmPassword.getText().toString())) {
                            if (Password.getText().toString().length() >= 4) {

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
                                        progressDialog1=new ProgressDialog(getContext());
                                        progressDialog1.setMessage("wait a moment...");
                                        progressDialog1.show();
                                        new ChangePassTask().execute(cred[0],cred[1],Password.getText().toString());
                                    }
                                });
builder.show();
                            } else {
                                Password.setError("Password At least 4 Characters");
                            }
                        }else{
                       Password.setError("Password Mismatching");
                        }
                    } else {
                            Password.setError("Should Not Be Empty");
                        }

                }
            });
            return rootView;
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

                    progressDialog1.dismiss();
                    Intent intent=new Intent(getContext(),customerLoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    Toast.makeText(getContext(),"Password Changed Successfully!",Toast.LENGTH_LONG).show();
                }else{
                    progressDialog1.dismiss();

                    Toast.makeText(getContext(),"Something wrong...please Re-Try!",Toast.LENGTH_LONG).show();
                }
            }

        }

        private String changePassword(String user, String newpass, String oldpass) {
            Activity_Identifiers activity_identifiers=new Activity_Identifiers(getContext());
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
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public class DeleteImageTask extends AsyncTask<String, Void, String> {
String EMAIl,HOSP,DATE;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(String... params) {
EMAIl=params[0];
HOSP=params[1];
DATE=params[2];
            // TODO: attempt authentication against a network service.
            String data = null;
            data = DeleteImage(EMAIl,HOSP,DATE);

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
if(result.contains("QR removed successfully!")){
    txt.setText("\n"+QRresult+"\nToken Number : "+tokenNo);
verified.setVisibility(View.VISIBLE);
    Toast.makeText(ReceptionistTabbedActivity.this, result, Toast.LENGTH_SHORT).show();
}
else{
    txt.setText("Click the above scan button!");
    Toast.makeText(ReceptionistTabbedActivity.this, "Not Availabe!", Toast.LENGTH_SHORT).show();
     }
   }
 }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String DeleteImage(String email,String hosp,String date) {

        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/DeleteImage.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(3);
            arrayList.add(new BasicNameValuePair("email", email));
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



    class GetDetailTask extends AsyncTask<String,Void,String>{
        String email1;
        @Override
        protected String doInBackground(String... strings) {
            email1=strings[0];
            String data=getDetail(email1);
            return data;
        }

        @Override
        protected void onPostExecute(String s){
            JSONObject jsonObject;
            hashMap=new HashMap<String,String>();
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
             //   System.out.println("##########################"+hashMap.get("1"));
               // System.out.println("##########################"+hashMap.get("2"));
                System.out.println("##########################"+hashMap.get("3"));
              //  System.out.println("##########################"+hashMap.get("4"));
                System.out.println("##########################"+hashMap.get("5"));

                new GetTokenNumber().execute(String.valueOf(hashMap.get("0")),String.valueOf(hashMap.get("3")), String.valueOf(hashMap.get("5")),email1);

            }else {
                Toast.makeText(getApplicationContext(), "Nothing Details Available!", Toast.LENGTH_SHORT).show();
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
    class GetTokenNumber extends AsyncTask<String,Void,String>{
        String TID,HOSP,DATE,EMAIL;
        @Override
        protected String doInBackground(String... strings) {
            TID=strings[0];
             HOSP=strings[1];
             DATE=strings[2];
             EMAIL=strings[3];
            String data=getTokenNum(TID,HOSP,DATE);
            return data;
        }

        @Override
        protected void onPostExecute(String s){
            if(!s.contains("failed")){

                new DeleteImageTask().execute(EMAIL,HOSP,DATE);
tokenNo=s;
            }else {
                Toast.makeText(getApplicationContext(), "No Tokens Available for them!", Toast.LENGTH_SHORT).show();
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

}
