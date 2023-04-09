package osattransport.com.osatdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;

import osattransport.com.osatdriver.Utils.UserSession;
import osattransport.com.osatdriver.models.OrderDetails;

/**
 * Created by Harpreet on 2/16/2018.
 */

public class Confirmed_orders extends Fragment {
    TextView totalRequest;
    ArrayList<OrderDetails> data;
    ListView order_list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.confirmed_orders_list,container,false);

        order_list=(ListView)v.findViewById(R.id.allconfirmed_list);
        data=new ArrayList<OrderDetails>();

        totalRequest=(TextView)v.findViewById(R.id.totalRequest);
        GetOrderDetails obj=new GetOrderDetails();
        obj.execute("ViewOrders");



        return v;
    }


    class GetOrderDetails extends AsyncTask<String,Void,String> {

        JSONObject json;
        String myresult=null;

        ProgressDialog progressDialog;

        public GetOrderDetails ()
        {
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Confirmed_orders.this.getActivity(), "Connecting", "Loading..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            String userdetails_url="http://osattransport.com/back-bone/app/services/viewdriverorders/";

            String method=params[0];

            if(method.equals("ViewOrders"))
            {
//


                try {
                    URL url=new URL(userdetails_url);
                    HttpURLConnection httpconnection=(HttpURLConnection) url.openConnection();
                    httpconnection.setRequestMethod("POST");
                    httpconnection.setDoOutput(true);
                    httpconnection.setDoInput(true);
                    OutputStream outputstream=httpconnection.getOutputStream();
                    BufferedWriter bufferwriter=new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                    String data= URLEncoder.encode("query","UTF-8")+"="+URLEncoder.encode(method,"UTF-8")+"&"+
                            URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode("Confirmed","UTF-8")+"&"+
                            URLEncoder.encode("userID","UTF-8")+"="+URLEncoder.encode(UserSession.userID,"UTF-8");

                    Log.e("User ID : ", UserSession.userID);
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

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("View Order Error ", e+"");
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

            JSONArray jsonarray=null;

            int arraylenth=0;

            try {
                json = new JSONObject(result);
                String message = json.getString("message");
                jsonarray = json.getJSONArray("data");
                Log.e("all", jsonarray+"");

                if(jsonarray != null)
                {
                    int tot=jsonarray.length();
                    data=new ArrayList<OrderDetails>();

                    totalRequest.setText(tot+"");

                    for (int i = 0; i < tot; i++) {
                        JSONObject finalobj = null;
                        finalobj = jsonarray.getJSONObject(i);
                        String id, sdate, strucktype, snooftrucks, sfromaddress, sfromcity, sfromstate, stoaddress, stocity,
                                stostate, smaterial, sweight, sheight, slength, sbookedtruck,sstatus, stransactionid, samount,
                                scompanyname, sname, stophone, sphone, sdriverorderid, srequestid;
                        id = finalobj.getString("id");
                        sdate= finalobj.getString("date");
                        strucktype= finalobj.getString("trucktype");
                        snooftrucks= finalobj.getString("nooftrucks");
                        sfromaddress= finalobj.getString("fromaddress");
                        sfromcity = finalobj.getString("fromcity");
                        sfromstate = finalobj.getString("fromstate");
                        stoaddress = finalobj.getString("toaddress");
                        stocity= finalobj.getString("tocity");
                        stostate= finalobj.getString("tostate");
                        smaterial= finalobj.getString("material");
                        sweight= finalobj.getString("weight");
                        sheight= finalobj.getString("height");
                        slength= finalobj.getString("length");
                        sbookedtruck= finalobj.getString("bookedtruck");
                        stransactionid= finalobj.getString("transactionid");
                        samount= finalobj.getString("amount");
                        sstatus= finalobj.getString("status");
                        scompanyname= finalobj.getString("companyname");
                        sname= finalobj.getString("name");
                        sphone= finalobj.getString("phone");
                        stophone= finalobj.getString("tophone");
                        sdriverorderid= finalobj.getString("driverorderid");
                        srequestid= finalobj.getString("requestid");

                        OrderDetails obj= new OrderDetails();


                        obj.setOrderid(id);
                        obj.setDate(sdate);
                        obj.setTruckType(strucktype);
                        obj.setNoOfTrucks(snooftrucks);
                        obj.setFromAddress(sfromaddress);
                        obj.setFromCity(sfromcity);
                        obj.setFromState(sfromstate);
                        obj.setToAddress(stoaddress);
                        obj.setToCity(stocity);
                        obj.setToState(stostate);
                        obj.setMaterial(smaterial);
                        obj.setWeight(sweight);
                        obj.setHeight(sheight);
                        obj.setLength(slength);
                        obj.setBookedTruck(sbookedtruck);
                        obj.setStatus(sstatus);
                        obj.setTransactionNo(stransactionid);
                        obj.setAmount(samount);
                        obj.setName(sname);
                        obj.setCompany(scompanyname);
                        obj.setPhone(sphone);
                        obj.setToPhone(stophone);
                        obj.setDriverOrderID(sdriverorderid);
                        obj.setRequestID(srequestid);

                        data.add(obj);

                    }

                    all_orders_adapter adapter=new all_orders_adapter(getActivity(),data);
                    order_list.setAdapter(adapter);

                    order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            OrderDetails obj=data.get(i);
                            Intent profile=new Intent(Confirmed_orders.this.getActivity(),Single_order_details.class);
                            profile.putExtra("orderid", obj.getOrderid());
                            profile.putExtra("trucktype", obj.getTruckType());
                            profile.putExtra("nooftrucks", obj.getNoOfTrucks());
                            profile.putExtra("fromAddress", obj.getFromAddress()+", "+obj.getFromCity()+", "+obj.getFromState());
                            profile.putExtra("toAddress", obj.getToAddress()+", "+obj.getToCity()+", "+obj.getToState());
                            profile.putExtra("material", obj.getMaterial());
                            profile.putExtra("date", obj.getDate());
                            profile.putExtra("status", obj.getStatus());
                            profile.putExtra("booked", obj.getBookedTruck());
                            profile.putExtra("weight", obj.getWeight());
                            profile.putExtra("height", obj.getHeight());
                            profile.putExtra("length", obj.getLength());
                            profile.putExtra("name", obj.getName());
                            profile.putExtra("compnay", obj.getCompany());
                            profile.putExtra("phone", obj.getPhone());
                            profile.putExtra("tophone", obj.getToPhone());
                            profile.putExtra("tranid", obj.getTransactionNo());
                            profile.putExtra("amount", obj.getAmount());
                            profile.putExtra("driverorderid", obj.getDriverOrderID());
                            profile.putExtra("fromLocation", obj.getFromCity()+", "+obj.getFromState());
                            profile.putExtra("toLocation", obj.getToCity()+", "+obj.getToState());
                            profile.putExtra("requestid", obj.getRequestID());

                            startActivity(profile);
                        }
                    });


                }
                else
                {
                    Toast.makeText(Confirmed_orders.this.getActivity(), "sorry", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }


//            userdetails.setMessage(result);
//            userdetails.show();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //order_list=(ListView)v.findViewById(R.id.allcompleted_list);
        // data=new ArrayList<OrderDetails>();
        Log.e("On Resume", "chala");
        GetOrderDetails obj=new GetOrderDetails();
        obj.execute("ViewOrders");

    }
}
