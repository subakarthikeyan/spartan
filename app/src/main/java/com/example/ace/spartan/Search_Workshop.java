package com.example.ace.spartan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class Search_Workshop extends Fragment {
FragmentActivity listener;
    Spinner map_type_spinner;
public String map_type;
    View view;
Button Search;
WebView QR_image;

    private ProgressDialog progressDialog;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_workshop, container, false);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map_type_spinner = (Spinner) view.findViewById(R.id.MAP_TYPE);
        ArrayList<String> arrayList = new ArrayList<>();
        Search=(Button)view.findViewById(R.id.SearchBTN);
        ArrayAdapter<String> arrayAdapter;

        QR_image=(WebView)view.findViewById(R.id.QRIMAGE);
        QR_image.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = QR_image.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        progressDialog= ProgressDialog.show(getContext(),"Getting QRcode page","Wait a moment...",false,false);

        new GetImageTask().execute();

        arrayList.add("Satellite");
        arrayList.add("Normal");
        arrayList.add("Hybrid");
        arrayList.add("Terrain");
        arrayList.add("None");
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        map_type_spinner.setAdapter(arrayAdapter);

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_type=map_type_spinner.getSelectedItem().toString();
                Intent intent=new Intent(getActivity(),DateActivity.class);
//Create the bundle
                Bundle bundle = new Bundle();

//Add your data to bundle
                for(int i=0;i<8;i++){
                    System.out.println("############"+map_type);
                }
                bundle.putString("MAP_TYPE",map_type);
//Add the bundle to the intent
                intent.putExtras(bundle);

//Fire that second activity
                startActivity(intent);
            }

        });

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


    public class GetImageTask extends AsyncTask<String, Void, String> {

        private String EmailAdd;
        private String mPassword;
        private String dbselector;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(String... params) {

            // TODO: attempt authentication against a network service.
            String data = null;
         data = GetImage();

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
for(int i=0;i<8;i++){
    System.out.println("*****************************"+result);
}
if(result.contains("Use This QR")){
    Search.setEnabled(false);
    Search.setText("Verify this QR with Receptionist");
    map_type_spinner.setEnabled(false);
}else{
    Search.setEnabled(true);
map_type_spinner.setEnabled(true);

}
progressDialog.cancel();
    QR_image.loadData(result, "text/html", null);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String GetImage() {
        session_details s=new session_details(getContext());
        String email=s.getVal()[0];
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/getAllImages.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(3);
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
