package osattransport.com.osatdriver.BackgroundServices;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class App extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        if (!isMyServiceRunning(ServiceClass.class)) {
        Log.e("App se Service", "Started");
            startService(new Intent(this,ServiceClass.class));
        }

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.e ("isMyServiceRunning?", false+"");
        return false;
    }
}
