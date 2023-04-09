package osattransport.com.osatdriver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import osattransport.com.osatdriver.Utils.UserSession;

public class Single_order_details extends AppCompatActivity {
    TextView orderidText,transactionidText, nooftrucksText, dateText, fromaddressText, toaddressText, materialText,
    truckTypeText, statusText, priceText, priceTitleText, weightText, lengthText, priceRs, heightText, phoneText, companyText, nameText, toPhoneText;
    LinearLayout transportLayout, transactionIDLayout;
    Button maptrackings, cancelButton, viewrequestButton;
    String orderid="0", driverorderid="0", requestid="0", fromLocation="", toLocation="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_order_show);

        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        String fromAddress = getIntent().getExtras().get("fromAddress").toString();
        String toAddress = getIntent().getExtras().get("toAddress").toString();
        orderid = getIntent().getExtras().get("orderid").toString();
        String material = getIntent().getExtras().get("material").toString();
        String weight = getIntent().getExtras().get("weight").toString();
        String height = getIntent().getExtras().get("height").toString();
        String length= getIntent().getExtras().get("length").toString();
        String phone = getIntent().getExtras().get("phone").toString();
        String tophone = getIntent().getExtras().get("tophone").toString();
        String company= getIntent().getExtras().get("compnay").toString();
        String name= getIntent().getExtras().get("name").toString();
        String truckType = getIntent().getExtras().get("trucktype").toString();
        String date = getIntent().getExtras().get("date").toString();
        String nooftrucks = getIntent().getExtras().get("nooftrucks").toString();
        String status = getIntent().getExtras().get("status").toString();
        String booked = getIntent().getExtras().get("booked").toString();
        String tranno = getIntent().getExtras().get("tranid").toString();
        String amount = getIntent().getExtras().get("amount").toString();
        fromLocation= getIntent().getExtras().get("fromLocation").toString();
        toLocation= getIntent().getExtras().get("toLocation").toString();
        driverorderid= getIntent().getExtras().get("driverorderid").toString();
        requestid= getIntent().getExtras().get("requestid").toString();

        Log.e("Driver ID", UserSession.userID);
        Log.e("Driver OrderID", driverorderid);
        Log.e("RequestID", requestid);
        Log.e("OrderID", orderid);
        orderidText = (TextView) findViewById(R.id.orderidText);
        nooftrucksText = (TextView) findViewById(R.id.totaltrucks);
        dateText = (TextView) findViewById(R.id.date);
        fromaddressText = (TextView) findViewById(R.id.fromPlace);
        toaddressText = (TextView) findViewById(R.id.toPlace);
        materialText = (TextView) findViewById(R.id.materialtype);
        weightText = (TextView) findViewById(R.id.weight);
        statusText = (TextView) findViewById(R.id.statusText);
        priceTitleText = (TextView) findViewById(R.id.priceTitleText);
        priceRs= (TextView) findViewById(R.id.priceRs);
        lengthText= (TextView) findViewById(R.id.length);
        heightText= (TextView) findViewById(R.id.height);
        companyText= (TextView) findViewById(R.id.companyname);
        nameText= (TextView) findViewById(R.id.name);
        phoneText= (TextView) findViewById(R.id.transpohone);
        toPhoneText= (TextView) findViewById(R.id.tophone);

        transportLayout=(LinearLayout)findViewById(R.id.transportLayout);
        transactionIDLayout=(LinearLayout)findViewById(R.id.transactionIDLayout);

        maptrackings=(Button)findViewById(R.id.maptracking);
        cancelButton=(Button)findViewById(R.id.cancel_booking);
        viewrequestButton=(Button)findViewById(R.id.viewrequest);

        statusText.setText(status);
        orderidText.setText(orderid);
       // truckTypeText.setText(truckType);
        nooftrucksText.setText(nooftrucks);
        materialText.setText(material);
        fromaddressText.setText(fromAddress);
        toaddressText.setText(toAddress);
        dateText.setText(date);
        weightText.setText(weight);
        lengthText.setText(length);
        heightText.setText(height);
        nameText.setText(name);
        companyText.setText(company);
        phoneText.setText(phone);
        toPhoneText.setText(tophone);

        transactionIDLayout.setVisibility(View.GONE);
//        transportLayout.setVisibility(View.GONE);
//        maptrackings.setVisibility(View.GONE);

        if(status.equals("Completed") || status.equalsIgnoreCase("Completion Request"))
        {
            viewrequestButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
        viewrequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d=new Dialog(Single_order_details.this);
                d.setContentView(R.layout.confirm_dialog);
                TextView message=(TextView)d.findViewById(R.id.txt_message);
                CardView yesButton=(CardView)d.findViewById(R.id.yesButton);
                CardView noButton=(CardView)d.findViewById(R.id.noButton);
                message.setText("  Do you want to start you ride?  ");
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent maptracking=new Intent(getApplicationContext(),Strat_map_tracking.class);
                        Intent maptracking=new Intent(getApplicationContext(),StartTrackingActivity.class);
                        maptracking.putExtra("driverorderid", driverorderid);
                        maptracking.putExtra("fromLocation", fromLocation);
                        maptracking.putExtra("toLocation", toLocation);
                        startActivity(maptracking);
                        d.dismiss();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
                d.show();

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d=new Dialog(Single_order_details.this);
                d.setContentView(R.layout.confirm_dialog);
                TextView message=(TextView)d.findViewById(R.id.txt_message);
                CardView yesButton=(CardView)d.findViewById(R.id.yesButton);
                CardView noButton=(CardView)d.findViewById(R.id.noButton);
                message.setText("  Are you sure about to complete your ride?  ");
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CompleteRiding obj=new CompleteRiding();
                        obj.execute("CompleteRiding");
                        d.dismiss();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
                d.show();
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return true;
    }

    class CompleteRiding extends AsyncTask<String,Void,String>
    {
        JSONObject json;

        CompleteRiding()
        {
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Single_order_details.this, "Connecting", "Updating..");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String strURL="http://osattransport.com/back-bone/app/services/drivercompleteridingrequest/";

            String method=params[0];

            if(method.equals("CompleteRiding"))
            {
                try {
                    URL url=new URL(strURL);
                    HttpURLConnection httpconnection=(HttpURLConnection) url.openConnection();
                    httpconnection.setRequestMethod("POST");
                    httpconnection.setDoOutput(true);
                    httpconnection.setDoInput(true);
                    OutputStream outputstream=httpconnection.getOutputStream();
                    BufferedWriter bufferwriter=new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                    String data= URLEncoder.encode("query","UTF-8")+"="+URLEncoder.encode(method,"UTF-8")+"&"+
                            URLEncoder.encode("orderID","UTF-8")+"="+URLEncoder.encode(orderid,"UTF-8")+"&"+
                            URLEncoder.encode("driverorderID","UTF-8")+"="+URLEncoder.encode(driverorderid,"UTF-8")+"&"+
                            URLEncoder.encode("requestID","UTF-8")+"="+URLEncoder.encode(requestid,"UTF-8");
                    Log.e("API URL", data);
                    bufferwriter.write(data);
                    bufferwriter.flush();
                    bufferwriter.close();
                    outputstream.close();


                    InputStream inputstream=httpconnection.getInputStream();
                    BufferedReader bufferreader=new BufferedReader(new InputStreamReader(inputstream,"UTF-8"));

                    String response="";
                    String line="";
                    while ((line=bufferreader.readLine())!=null)
                    {
                        response+=line;
                    }

                    bufferreader.close();
                    inputstream.close();
                    httpconnection.disconnect();
                    return response;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in API", e+"");
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
           // Log.e("API Result", result);
            JSONObject json;
            try {
                json=new JSONObject(result);
                String message=json.getString("message");
                if(message.equals("DONE"))
                {
                    Toast.makeText(Single_order_details.this, "Your riding status has been updated successfully!!", Toast.LENGTH_LONG).show();
                    Single_order_details.this.finish();
                }
                else {
                    Toast.makeText(Single_order_details.this, "Not Connected to Service!! Try Again Later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
