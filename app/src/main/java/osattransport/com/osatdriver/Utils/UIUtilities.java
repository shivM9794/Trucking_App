
/**
 * its  Utilities class contain Globally static methods whose use mostlly time in this app.
 * like - checkInternetConnection, getAvailableMemory, encodeTobase64 etc.
 *
 * @author Pramod and Chandan 15/11/2013.
 * Copyright on � 2013 - 2014.
 */

/**
 * @author Pramod and Chandan 15/11/2013.
 * Copyright on � 2013 - 2014.
 */

package osattransport.com.osatdriver.Utils;


import android.app.NotificationManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class UIUtilities {

    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotifyManager = null;
    int FILEDOWNLOAD_NOTIFICATION_ID = 10;

    static UIUtilities instance = null;

    public NotificationCompat.Builder showNotificationforDownload(Context context,
                                                                  String title, String subTitle, int image,
                                                                  final int filesize) {
        mNotifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(context);
        }
        mBuilder.setContentTitle(title).setContentText(subTitle);
        mBuilder.setSmallIcon(image);
        mNotifyManager.cancelAll();
        return mBuilder;
    }

    public static UIUtilities getInstance() {
        if (instance == null)
            instance = new UIUtilities();
        return instance;
    }

    // stop the notification
    public void stopNotification(Context context,
                                 String downloadCompleteText) {
        if (mBuilder != null) {
            mNotifyManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setContentText(downloadCompleteText).setProgress(0, 0, false);
            mNotifyManager.notify(FILEDOWNLOAD_NOTIFICATION_ID, mBuilder.build());
            mNotifyManager.cancel(FILEDOWNLOAD_NOTIFICATION_ID);
            mBuilder = null;
            mNotifyManager = null;
        }
    }

    public void notifyProgress(int total, int current, Context context) {
        if (mNotifyManager != null && mBuilder != null) {
            if (!(total == 0 || current == 0)) {
                mBuilder.setProgress(total, current, false);
            }
            mNotifyManager.notify(FILEDOWNLOAD_NOTIFICATION_ID, mBuilder.build());
        }
    }

    public void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }


    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    /*****************16 Mar By Abhishek******************/
    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            return true;
        } else {
            return false;
        }
    }


    public void sendSMS(String phoneno, int rannumber) {
        if (phoneno.length() == 10) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            try {
                //http://buzz.yogiinfocom.in/http-api.php?username=osat&password=osat123&senderid=OSATAP&route=4&number=9215212223&message=message
                Globals.otpcode = rannumber;
                String authkey="osat";
                String api_password="osat123";
                String sms_type="4";
                String sender_id="OSATAP";
                String strMessage="Your OTP verification code for OSAT App is " + rannumber+" Team OSAT.";
                String encoded_message= URLEncoder.encode(strMessage);
                StringBuilder sbPostData= new StringBuilder("http://buzz.yogiinfocom.in/http-api.php?");
                sbPostData.append("username="+authkey);
                sbPostData.append("&password="+api_password);
                sbPostData.append("&senderid="+sender_id);
                sbPostData.append("&route="+sms_type);
                sbPostData.append("&number="+phoneno);
                sbPostData.append("&message="+encoded_message);
                String strURL=sbPostData.toString();

                URL url = new URL(strURL); // here is your URL path

                Log.e("URL",""+url);
                // HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                URLConnection urlConnection = url.openConnection();
                try {

                    urlConnection.connect();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Log.e("SMS_RET", stringBuilder.toString());
                }
                catch (Exception ee)
                {
                    Log.e("SMS API 1", ee+"");
                }
                finally{
                    //urlConnection.disconnect();
                }
            }
            catch (Exception ee)
            {
                Log.e("SMS API 2", ee+"");
            }

        }
    }


}
