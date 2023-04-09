package osattransport.com.osatdriver;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

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

import osattransport.com.osatdriver.BackgroundServices.ServiceClass;
import osattransport.com.osatdriver.Utils.ExtraUtilities;
import osattransport.com.osatdriver.Utils.UserSession;

/**
 * Created by Harpreet on 2/16/2018.
 */

public class Home_panel extends Fragment {
    TextView totaldrivers, totalorders;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ViewFlipper mViewFlipper;
    private Animation.AnimationListener mAnimationListener;
    @SuppressWarnings("deprecation")
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    ImageView image1, image2, image3;



    Intent mServiceIntent;
    ServiceClass serviceClass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.home_panel,container,false);

        totaldrivers=(TextView)v.findViewById(R.id.totaldrivers);
        totalorders=(TextView)v.findViewById(R.id.totalorders);

        Button bookingform=(Button)v.findViewById(R.id.bookingfrom);
        Button viewrequest=(Button)v.findViewById(R.id.viewrequest);

        if(UserSession.userID.equalsIgnoreCase("0"))
        {
            getActivity().finish();
        }

        GetTotals obj=new GetTotals();
        obj.execute("getTotalConfirmAndComplete");
        bookingform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent bookingform=new Intent(getActivity(),All_orders.class);
                bookingform.putExtra("Tab", "one");
                startActivity(bookingform);

            }
        });


        viewrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent bookingform=new Intent(getActivity(),All_orders.class);
                bookingform.putExtra("Tab", "two");
                startActivity(bookingform);

            }
        });
        mServiceIntent = new Intent(getActivity(), ServiceClass.class);
        mServiceIntent.putExtra("UserID", UserSession.userID);
        if (!isMyServiceRunning(ServiceClass.class)) {
            Log.e("Service home ", "started");
            getActivity().startService(mServiceIntent);
        }

        image1=(ImageView)v.findViewById(R.id.image1);
        image2=(ImageView)v.findViewById(R.id.image2);
        image3=(ImageView)v.findViewById(R.id.image3);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(), SliderDetailsActivity.class);
                intent.putExtra("Image", "1");
                getActivity().startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(), SliderDetailsActivity.class);
                intent.putExtra("Image", "2");
                getActivity().startActivity(intent);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(), SliderDetailsActivity.class);
                intent.putExtra("Image", "3");
                getActivity().startActivity(intent);
            }
        });
        ExtraUtilities.deleteCache(getContext());
        Picasso.with(getActivity()).load("http://osattransport.com/back-bone/images/slider/driver/1.jpg").error(R.drawable.delhicity).into(image1);
        Picasso.with(getActivity()).load("http://osattransport.com/back-bone/images/slider/driver/2.jpg").error(R.drawable.delhicity).into(image2);
        Picasso.with(getActivity()).load("http://osattransport.com/back-bone/images/slider/driver/3.jpg").error(R.drawable.delhicity).into(image3);


        mViewFlipper = (ViewFlipper) v.findViewById(R.id.view_flipper);
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(6000);
        mViewFlipper.startFlipping();

        mAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                //animation started event
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                //TODO animation stopped event
            }
        };
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.right_out));


        return v;
    }
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_out));
                    // controlling animation
                    mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mViewFlipper.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.right_out));
                    // controlling animation
                    mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mViewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetTotals obj = new GetTotals();
        obj.execute("getTotalConfirmAndComplete");
    }

    class GetTotals extends AsyncTask<String,Void,String>
    {
        JSONObject json;

        GetTotals()
        {
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Home_panel.this.getActivity(), "Connecting", "Loading..");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String strURL="http://osattransport.com/back-bone/app/services/totaldriverdashboard/";

            String method=params[0];
            if(method.equals("getTotalConfirmAndComplete"))
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
                            URLEncoder.encode("userID","UTF-8")+"="+URLEncoder.encode(UserSession.userID,"UTF-8");
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            Log.e("API Result", result);
            JSONObject json;
            try {
                json=new JSONObject(result);
                String message=json.getString("message");
                if(message.equals("DONE")) {
                    String drivers=json.getString("drivers");
                    String orders=json.getString("orders");

                    totaldrivers.setText(drivers);
                    totalorders.setText(orders);

                }
                else {

                    Toast.makeText(Home_panel.this.getActivity(), "Not Connected to Service!! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(cntx, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
