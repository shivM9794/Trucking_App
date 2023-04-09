package osattransport.com.osatdriver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import osattransport.com.osatdriver.Utils.Globals;
import osattransport.com.osatdriver.Utils.SessionManagement;
import osattransport.com.osatdriver.Utils.UIUtilities;
import osattransport.com.osatdriver.Utils.UserSession;

/**
 * Created by Harpreet on 2/16/2018.
 */

public class SignIn extends Fragment {

    SessionManagement sessionManagement;
    UIUtilities uiUtilities;
    EditText phoneText;
    Button login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sign_in, container, false);
        TextView text=(TextView)v.findViewById(R.id.Retry);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container_1, new SignIn()).addToBackStack(null).commit();
            }
        });
        if (Globals.checkInternetConnection(getActivity())) {
            v = inflater.inflate(R.layout.sign_in, container, false);
            login = (Button) v.findViewById(R.id.login);
            phoneText = (EditText) v.findViewById(R.id.phoneText);
            uiUtilities = new UIUtilities();

            sessionManagement = new SessionManagement(getActivity());
            if (sessionManagement.isLoggedIn()) {
                Log.e("User Type", sessionManagement.getUserType());
                Log.e("Mobile", sessionManagement.getMobile());
                Globals.Usertype = sessionManagement.getUserType();
                Globals.phoneno = sessionManagement.getMobile();
                CheckPhoneNumber ob = new CheckPhoneNumber(Globals.phoneno);
                ob.execute("checkDriverNumber");
            }
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (phoneText.getText().toString().trim().equals("")) {
                        Toast.makeText(SignIn.this.getActivity(), "Phone Number is required!!", Toast.LENGTH_SHORT).show();
                    } else if (phoneText.getText().toString().length() != 10) {
                        Toast.makeText(SignIn.this.getActivity(), "Phone Number is not valid!!", Toast.LENGTH_SHORT).show();
                    } else {
                        CheckPhoneNumber obj = new CheckPhoneNumber(phoneText.getText().toString());
                        obj.execute("checkDriverNumber");
                    }
                }
            });
        }
        return v;

    }

    class CheckPhoneNumber extends AsyncTask<String, Void, String> {
        JSONObject json;
        String phone;

        CheckPhoneNumber(String phone) {
            this.phone = phone;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Connecting", "Checking..");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String strURL = "http://osat.ramakrishnatransport.in/back-bone/app/services/checkdrivermobile/";

            String method = params[0];

            if (method.equals("checkDriverNumber")) {


                try {
                    URL url = new URL(strURL);
                    HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
                    httpconnection.setRequestMethod("POST");
                    httpconnection.setDoOutput(true);
                    httpconnection.setDoInput(true);
                    OutputStream outputstream = httpconnection.getOutputStream();
                    BufferedWriter bufferwriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));

                    String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8") + "&" +
                            URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                    Log.e("API URL", data);
                    bufferwriter.write(data);
                    bufferwriter.flush();
                    bufferwriter.close();
                    outputstream.close();


                    InputStream inputstream = httpconnection.getInputStream();
                    BufferedReader bufferreader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));

                    String response = "";
                    String line = "";
                    while ((line = bufferreader.readLine()) != null) {

                        response += line;
                    }

                    bufferreader.close();
                    inputstream.close();
                    httpconnection.disconnect();
                    return response;


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                //Toast.makeText(cntx, "Invalid", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            if (result != null) {
                try {
                    json = new JSONObject(result);
                    String message = json.getString("message");
                    String userType = json.getString("userType");
                    String id = json.getString("id");
                    String name = json.getString("name");
                    String companyname = json.getString("companyname");
                    String address = json.getString("address");
                    String city = json.getString("city");
                    String state = json.getString("state");
                    String email = json.getString("email");
                    String photopath = json.getString("photopath");
                    String isactive = json.getString("isactive");
                    if (message.equals("EXIST")) {
                        Log.e("API RESULT", message + ", " + userType);


                        UserSession.userID = id;

                        UserSession.name = name;
                        UserSession.address = address;
                        UserSession.city = city;
                        UserSession.state = state;
                        UserSession.mobile = phone;
                        UserSession.emailID = email;
                        UserSession.photopath = photopath;
                        UserSession.isActive = isactive;
                        UserSession.userType = userType;
                        UserSession.company = companyname;

                        if (UserSession.name.equals("null"))
                            UserSession.name = "";
                        if (UserSession.address.equals("null"))
                            UserSession.address = "";
                        if (UserSession.city.equals("null"))
                            UserSession.city = "";
                        if (UserSession.state.equals("null"))
                            UserSession.state = "";
                        if (UserSession.mobile.equals("null"))
                            UserSession.mobile = "";
                        if (UserSession.emailID.equals("null"))
                            UserSession.emailID = "";
                        if (UserSession.company.equals("null"))
                            UserSession.company = "";
                        if (UserSession.photopath.equals("null"))
                            UserSession.photopath = "";

                        Log.e("User name :", UserSession.name);
                        if (sessionManagement.isLoggedIn()) {
                            sessionManagement.setUserID(Integer.parseInt(UserSession.userID));
                            sessionManagement.checkLogin();
                            SignIn.this.getActivity().finish();
                        } else {
                            Random random = new Random();
                            int rannumber = random.nextInt(100000);
                            uiUtilities.sendSMS(phone, rannumber);
                            //uiUtilities.showToast(SignIn.this.getActivity(),""+rannumber);


                            Globals.phoneno = phone;
                            Globals.otpcode = rannumber;
                            Globals.fromLoginOrSignup = "Login";
                            Globals.Usertype = userType;

                            Otp sgn = new Otp();
                            Bundle b = new Bundle();
                            b.putString("phone", phone);
                            sgn.setArguments(b);
                            SignIn.this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_container_1, sgn).disallowAddToBackStack().commit();
                        }
                    } else {
                        sessionManagement.destroySession();
                        //Toast.makeText(SignIn.this.getActivity(), "Mobile Number not registered with us!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignIn.this.getActivity(), "Not Connected to Service!! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            } else {

            }
        }
    }
}
