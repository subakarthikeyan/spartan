package com.example.ace.spartan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
/**
 * A login screen that offers login via EMAIL/password.
 */
public class customerLoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG ="FireBase_Realtime_Databse" ;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
String PatientName;


    String notifying = "";

    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    private int CHECK;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public Activity_Identifiers activity_identifiers = new Activity_Identifiers(customerLoginActivity.this);
    private String DB_Selector = "2";
    private UserLoginTask mAuthTask;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        // Set up the login form.

        mEmailView = (AutoCompleteTextView) findViewById(R.id.Email);
        mPasswordView = (EditText) findViewById(R.id.password);

        CHECK = activity_identifiers.getVal();
        mEmailView.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        if (CHECK == 1) {

            DB_Selector = String.valueOf(CHECK);
            notifying = "Workshop Owner's ";
            Button button=(Button)findViewById(R.id.signUp);
            button.setBackground(Drawable.createFromPath("@drawable/transeblack"));
            button.setEnabled(false);
            button.setText("");

        } else if (CHECK == 2) {


DB_Selector=String.valueOf(CHECK);
            notifying = "Customer's ";
        } else {
            DB_Selector=String.valueOf(CHECK);
            Toast.makeText(getApplicationContext(), "Try Again!!!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(customerLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEMAILSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEMAILSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();


            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid EMAIL, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
       mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String EMAIL = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid EMAIL address.
        if (CHECK == 2) {
            if (TextUtils.isEmpty(EMAIL)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if ( EMAIL.length() <= 0||!EMAIL.contains("@")||!EMAIL.contains(".com")) {
                mEmailView.setError("Invalid Email!");
                focusView = mEmailView;
                cancel = true;
            }
        } else {
            if (TextUtils.isEmpty(EMAIL)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = (UserLoginTask) new UserLoginTask().execute(EMAIL, password, DB_Selector, "0");
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(customerLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private String EmailAdd;
        private String mPassword;
        private String dbselector;

        @Override
        protected String doInBackground(String... params) {

            // TODO: attempt authentication against a network service.
            String data = "";
            EmailAdd = params[0];
            mPassword = params[1];
            dbselector = params[2];
            data = sendHttpRequest(EmailAdd, mPassword, dbselector);

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            showProgress(false);
            for(int i=0;i<8;i++){
                System.out.println("##############"+result);
            }
            if (result.contains("Welcome")) {
                PatientName=result.substring(8,result.length()-2);
                session_details session=new session_details(customerLoginActivity.this);
                session.inserts(EmailAdd,mPassword,PatientName,CHECK);

                SharedPref sharedPref=new SharedPref();
                sharedPref.login(EmailAdd,mPassword,customerLoginActivity.this);
                Intent intent=null;
                if(CHECK==1){
intent=new Intent(customerLoginActivity.this,ReceptionistTabbedActivity.class);
}else {
    intent = new Intent(customerLoginActivity.this, WorkShop.class);

}
                    startActivity(intent);
                Toast.makeText(getApplicationContext(),  result , Toast.LENGTH_SHORT).show();

                finish();
            } else {
                if (CHECK == 1) {
                    mEmailView.setError("Invalid LicenseNumber/Password!");
                } else {
                    mEmailView.setError("Invalid PhoneNumber/Password!");
                }
                mEmailView.requestFocus();
            }
        }
    }
        private String sendHttpRequest(String email, String pass, String dbselector) {
            String origresponseText = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://sk68.000webhostapp.com/Flash/Flash_Login.php");
            try {
                ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>(3);
                arrayList.add(new BasicNameValuePair("email", email));
                arrayList.add(new BasicNameValuePair("pass", pass));
                arrayList.add(new BasicNameValuePair("dbselector", dbselector));//1-workshop login, 2-customer login
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
                HttpResponse response = httpClient.execute(httpPost);
                origresponseText = readContent(response);
                for (int i = 0; i < 9; i++) {
                    System.out.println("#################OUTPUT:" + email);
                    System.out.println("RESPONSE: " + origresponseText);
                }
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
public void forget(View view){

        Intent intent=new Intent(customerLoginActivity.this,OtpEmailSender.class);
        startActivity(intent);
        finish();
}

            //REGISTER USER ACCOUNT
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public void registerAC(View view) {

                //call registration page
                Intent intent = new Intent(customerLoginActivity.this, Register.class);
                startActivity(intent);
      }

}

