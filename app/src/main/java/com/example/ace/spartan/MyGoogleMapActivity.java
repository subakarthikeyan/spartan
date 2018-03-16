package com.example.ace.spartan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyGoogleMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleMap mMap;
    String[]  Credincial=new String[3];
    String placeName="",address="";
    Marker mLocationMarker;
    String email;
    LocationRequest mLocationRequest;
    double latitude;
    ProgressDialog progressDialog;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
String vehicle,distance,map_type;
    private String[] hospital;
    private String[] credencial;
    private Bitmap myBitmap;
    SupportMapFragment mapFragment;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_google_map);
progressDialog=new ProgressDialog(this);
progressDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        final Bundle bundle = getIntent().getExtras();
//Extract the data…
        email=bundle.getString("EMAIL");
        date=bundle.getString("DATE");
        final String map_type = bundle.getString("MAP_TYPE");

        for(int i=0;i<8;i++){

            System.out.println("############"+map_type);

        }
switch (map_type){
    case "Normal":mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    break;
    case "Hybrid":mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    break;
    case "Terrain":mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    break;
    case "None":mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
    break;
    case "Satellite":mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    break;
}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                // Although the user’s location will update automatically on a regular basis, you can also
                // give your users a way of triggering a location update manually. Here, we’re adding a
                // ‘My Location’ button to the upper-right corner of our app; when the user taps this button,
                // the camera will update and center on the user’s current location//

                mMap.setMyLocationEnabled(true);
            }
        }
  else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        Button GetLocationBTN = (Button) findViewById(R.id.getLocation);
        GetLocationBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                String url = getUrl(latitude, longitude, "hospital");
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
              getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(MyGoogleMapActivity.this,"Nearby Hospitals", Toast.LENGTH_LONG).show();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String hosp_detail="";
                if(!marker.getTitle().contains("Current Position")) {
                    for (int i = 0; i < 2; i++) {
                        if (i == 0) {
                            for (int j = 0; j < marker.getTitle().length(); j++) {
                                if (marker.getTitle().charAt(j) != ':') {
                                    placeName += marker.getTitle().charAt(j);

                                } else {
                                    break;
                                }
                            }
                        } else {
                            address = marker.getTitle().substring(placeName.length() + 1, marker.getTitle().length());
                        }
                    }
                    session_details s=new session_details(MyGoogleMapActivity.this);
                    credencial=s.getVal();

                }

                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(MyGoogleMapActivity.this);
                final session_details s=new session_details(MyGoogleMapActivity.this);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Confirmation To Get Token");
                TextView placeView=new TextView(MyGoogleMapActivity.this);
                placeView.setText("Hospital Name : "+placeName+"\n\n"+"Address : "+address+"\n\n\nAre You Sure?");

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                placeView.setLayoutParams(layoutParams);

                alertDialog.setView(placeView);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                  //      s.insert_hopital(placeName,address);

                        for(int i=0;i<8;i++){
                            System.out.println("############################"+credencial[0]+"  "+credencial[1]+"  "+credencial[2]+"  "+placeName+"  "+date);
                        }
                        progressDialog.show();
                        new UploadImage().execute(credencial[0],credencial[2],placeName,address,date);
                        placeName="";

                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        placeName="";
                    }
                });
                if(!marker.getTitle().contains("Current Position")) {
                    alertDialog.show();
                }else {
                    Toast.makeText(MyGoogleMapActivity.this, "Please Click GET POSITION Button!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkLocationPermission(){
        // In Android 6.0 and higher you need to request permissions at runtime, and the user has
        // the ability to grant or deny each permission. Users can also revoke a previously-granted
        // permission at any time, so your app must always check that it has access to each
        // permission, before trying to perform actions that require that permission. Here, we’re using
        // ContextCompat.checkSelfPermission to check whether this app currently has the
        // ACCESS_COARSE_LOCATION permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                // If your app does have access to COARSE_LOCATION, then this method will return
                // PackageManager.PERMISSION_GRANTED//
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // If your app doesn’t have this permission, then you’ll need to request it by calling
                // the ActivityCompat.requestPermissions method//
                requestPermissions(new String[] {
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // Request the permission by launching Android’s standard permissions dialog.
                // If you want to provide any additional information, such as why your app requires this
                // particular permission, then you’ll need to add this information before calling
                // requestPermission //
                requestPermissions(new String[] {
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCfdXATlz7jtM6MEvy9Xh_3_g_Ivc5ysXE");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
    protected synchronized void buildGoogleApiClient() {
        // Use the GoogleApiClient.Builder class to create an instance of the
        // Google Play Services API client//
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(MyGoogleMapActivity.this)
                .addApi(LocationServices.API)
                .build();
        // Connect to Google Play Services, by calling the connect() method//
        mGoogleApiClient.connect();
    }
    @Override
    // If the connect request is completed successfully, the onConnected(Bundle) method
    // will be invoked and any queued items will be executed//
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Retrieve the user’s last known location//
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    // Displaying multiple ‘current location’ markers is only going to confuse your users!
    // To make sure there’s only ever one marker onscreen at a time, I’m using
    // mLocationMarker.remove to clear all markers whenever the user’s location changes.
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        // To help preserve the device’s battery life, you’ll typically want to use
        // removeLocationUpdates to suspend location updates when your app is no longer
        // visible onscreen//
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
        }
latitude=mLastLocation.getLatitude();
        longitude=mLastLocation.getLongitude();
     LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
  //move map camera
      mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MyGoogleMapActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");
    }
    // Once the user has granted or denied your permission request, the Activity’s
    // onRequestPermissionsResult method will be called, and the system will pass
    // the results of the ‘grant permission’ dialog, as an int//
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {

                // If the request is cancelled, the result array will be empty (0)//
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // If the user has granted your permission request, then your app can now perform all its
                    // location-related tasks, including displaying the user’s location on the map//
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // If the user has denied your permission request, then at this point you may want to
                    // disable any functionality that depends on this permission//
                }
                return;
            }
        }
    }
    public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
        String googlePlacesData="";
        GoogleMap mMap;
        String url;
        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d("GetNearbyPlacesData", "doInBackground entered");
                mMap = (GoogleMap) params[0];
                url = (String) params[1];
                DownloadUrl downloadUrl = new DownloadUrl();
                googlePlacesData = downloadUrl.readUrl(url);
                Log.d("GooglePlacesReadTask", "doInBackground Exit");
            } catch (Exception e) {
                Log.d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
            List<HashMap<String, String>> nearbyPlacesList = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesList =  dataParser.parse(result);
            System.out.println(nearbyPlacesList);
        ShowNearbyPlaces(nearbyPlacesList);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        }
        private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute","Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String,String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                mMap.addMarker(markerOptions);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                //move map camera

            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }
    class UploadImage extends AsyncTask<String,Void,String> {
        String email,name,hosp,address1,date;
        @Override
        protected String doInBackground(String... strs) {
 email=strs[0];
 name=strs[1];
 hosp=strs[2];
 address1=strs[3];
 date=strs[4];
            myBitmap = net.glxn.qrgen.android.QRCode.from("Email: "+email+"\nName: "+name+"\nHospital: "+hosp+"\nHospital Address : "+address1+"\nCheck-Up Date: "+date).bitmap();

            String uploadImage = getStringImage(myBitmap);

            String data=uploadImage(email,uploadImage,hosp,address1,date);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.contains("Successfully")){
                progressDialog.dismiss();
     AlertDialog.Builder builder=new AlertDialog.Builder(MyGoogleMapActivity.this);
     builder.setCancelable(false);
     builder.setTitle("Token Alert");
     TextView txt=new TextView(MyGoogleMapActivity.this);

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
txt.setLayoutParams(layoutParams);
                txt.setText("You Have Registered Token For "+hosp+" Clinic\n\n\nplease press the back button one time!!!");
builder.setView(txt);
builder.setPositiveButton("i got it", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

        dialog.cancel();
    }
});
builder.show();
                Toast.makeText(getApplicationContext(), "QR "+s, Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "QR Upload failed", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }
    private String uploadImage(String email, String image,String hosp,String address,String date) {
        String origresponseText = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/UploadImage.php");
        try {
            ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(5);
            arrayList.add(new BasicNameValuePair("email", email));
            arrayList.add(new BasicNameValuePair("image", image));
            arrayList.add(new BasicNameValuePair("hosp", hosp));
            arrayList.add(new BasicNameValuePair("addr", address));
            arrayList.add(new BasicNameValuePair("date", date));
            //1-workshop login, 2-customer login
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
