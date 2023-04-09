package osattransport.com.osatdriver.BackgroundServices;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by MAC2 on 21-May-17.
 */

public class ServiceBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
         //if (intent.getAction().equals(ACTION)) {
            //Service
            Intent serviceIntent = new Intent(context, ServiceClass.class);
            Log.e("Running Again", "Service");
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startService(serviceIntent);
       // }
    }

}
