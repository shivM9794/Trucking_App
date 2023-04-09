package osattransport.com.osatdriver.BackgroundServices;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

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
import java.util.Timer;
import java.util.TimerTask;

import osattransport.com.osatdriver.All_orders;
import osattransport.com.osatdriver.R;
import osattransport.com.osatdriver.Utils.SessionManagement;
import osattransport.com.osatdriver.Utils.UserSession;

import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;

public class ServiceClass extends Service {
     Activity context;

    int numMessages=0,numMessages1=0,numMessages2=0,numMessages3=0,numMessages4=0,numMessages5=0,numMessages6=0;
    SharedPreferences pref;
    private static final String MYPREFERENCES="User_Session";
    int Private_Mode=0;
    WifiManager.WifiLock wifiLock;
    private String strUserID="0";
    public ServiceClass(Activity context) {
        super();
        this.context=context;
        Log.e("HERE", "here I am!");

    }

    public ServiceClass()
    {

        Log.e("service running"," 1");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SessionManagement sessionManagement=new SessionManagement(this);
        Log.e("service running"," -1");
        pref=getSharedPreferences(MYPREFERENCES,Private_Mode);

        Log.e("service running"," 0");
        UserSession.userID=sessionManagement.getUserID()+"";
        UserSession.userType="DRIVER";
        StrictMode.ThreadPolicy tp=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
//        wifiLock=((WifiManager)this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
//        wifiLock.acquire();
//        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
//        wakeLock.acquire(20000);
        //onTaskRemoved(intent);

        startTimer();
        Log.e("service running"," 1");
        strUserID=UserSession.userID;
       //
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(getApplicationContext(), ServiceBroadcastReceiver.class);
        broadcastIntent.setPackage(getPackageName());
        Log.e("EXIT Service ", strUserID);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 20000, 20000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                new GetNotification().execute("GetNotifications");
//                ObboDBConnect db=new ObboDBConnect();
//                if(db.setConnectionForService(ServiceClass.this)) {
//                    // Toast.makeText(getApplicationContext(), strUserID+"", Toast.LENGTH_SHORT).show();
//                    String strRating = db.getUserRating(UserSession.userID);
//                    if (Integer.parseInt(UserSession.rating) < Integer.parseInt(strRating)) {
//                        Intent intent = new Intent(ServiceClass.this, RatingDialogActivity.class);
//                        UserSession.rating = strRating;
//
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                }

                Log.e("User ID IN SERVICE", UserSession.userID);


            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        Log.e("Stopping Timer", "1");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class GetNotification extends AsyncTask<String,Void,String>
    {
        JSONObject json;

        GetNotification()
        {
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String strURL="http://osattransport.com/back-bone/app/services/notifications/";

            String method=params[0];

            if(method.equals("GetNotifications"))
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
                            URLEncoder.encode("userID","UTF-8")+"="+URLEncoder.encode(UserSession.userID,"UTF-8")+"&"+
                            URLEncoder.encode("userType","UTF-8")+"="+URLEncoder.encode(UserSession.userType,"UTF-8");
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
            JSONObject json;
            try {
                if(result!=null) {
                    json = new JSONObject(result);
                    JSONArray jsonArr = json.getJSONArray("data");
                    int tot = jsonArr.length();
                    if (tot > 0) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        for (int i = 0; i < tot; i++) {
                            JSONObject jsonOBJ = jsonArr.getJSONObject(i);
                            String message = jsonOBJ.getString("message");
                            String messageType = jsonOBJ.getString("messagetype");
                            PendingIntent resultPendingIntent = null;
                            if (messageType.equals("Complete") && UserSession.userType.equals("DRIVER")) {
                                Intent resultIntent = new Intent(ServiceClass.this, All_orders.class);
                                //resultIntent.putExtra("Home","Inbox");
                                resultIntent.setAction(Long.toString(System.currentTimeMillis()));
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(ServiceClass.this);
                                stackBuilder.addParentStack(All_orders.class);
                                stackBuilder.addNextIntent(resultIntent);
                                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
                                numMessages = 0;
                                Log.e("Transporter Noti", "Order wala");
                            }

                            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(ServiceClass.this)
                                    .setContentTitle("OSAT")
                                    .setTicker(message)
                                    .setAutoCancel(true)
                                    //.setNumber()
                                    //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                    //.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setContentIntent(resultPendingIntent)
                                    .setContentText(message)
                                    .setSmallIcon(R.drawable.osat_circle_logo);
                            // ++numMessages;
                            Notification noti = notiBuilder.build();
                            //noti.flags |= Notification.FLAG_AUTO_CANCEL;

                            notificationManager.notify(numMessages, noti);
                            //db1.stopNotifications(UserSession.userID);
                        }
                    }
                }
            } catch (JSONException e) {
               Log.e("Notification Error", e+"");
            }
        }
    }

    @Override public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(ELAPSED_REALTIME, elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    class DeleteNotification extends AsyncTask<String,Void,String>
    {
        JSONObject json;
        String phone;

        DeleteNotification(String phone)
        {
            this.phone=phone;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String strURL="http://osattransport.com/back-bone/app/services/notifications/";

            String method=params[0];

            if(method.equals("DeleteNotification"))
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
                            URLEncoder.encode("userID","UTF-8")+"="+URLEncoder.encode(UserSession.userID,"UTF-8")+"&"+
                            URLEncoder.encode("userType","UTF-8")+"="+URLEncoder.encode(UserSession.userType,"UTF-8");
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
            JSONObject json;
            try {
                json=new JSONObject(result);
                String message=json.getString("message");

                Log.e("Notification Delete", message);
            } catch (JSONException e) {
                Log.e("Notification Error", e+"");
            }
        }
    }
}