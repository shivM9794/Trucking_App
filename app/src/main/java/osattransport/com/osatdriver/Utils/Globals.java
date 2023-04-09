package osattransport.com.osatdriver.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Globals
{
     public  static String phoneno;
     public static String password;
    public static int otpcode=0;
    public static String fromLoginOrSignup="";
     public static String imagepath[]=null;
     public static String log_type="false";
     public static Boolean closeApp=false;
     public static String USER_NAME;
     public  static  int countchoose=0;
     public static int counthired=0;
     public static String imagesarr[]={"","","",""};
     public static String Usertype="";




    public static String State="0",City="0",SearchingFor="0",PropertyType="0",BuildingType="0",PriceFrom="0",PriceTo="0",AreaFrom="0",AreaTo="0";

    public static boolean checkInternetConnection(Context context)
    {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((localConnectivityManager.getActiveNetworkInfo() != null)
                && (localConnectivityManager.getActiveNetworkInfo()
                .isAvailable())
                && (localConnectivityManager.getActiveNetworkInfo()
                .isConnected())) {
            return true;
        } else {
            try {

                UIUtilities uiUtilities=new UIUtilities();
                String msg="No internet connection";
                uiUtilities.showToast(context, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }
}
