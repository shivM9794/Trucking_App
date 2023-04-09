package osattransport.com.osatdriver.Utils;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class UploadImageBackground
{

    Context context;

    public UploadImageBackground(Context context)
    {
        this.context=context;
    }

    public void uploadFile(ArrayList<File> fileimages, int source)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()//if network error is occur
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        FTPClient client = new FTPClient();
        try
        {

            client.connect("osattransport.com", 21);
            client.login("osattransport@osattransport.com", "0$@Ttr@nMd@p0rt");
            client.setPassive(true);
            client.setType(FTPClient.TYPE_BINARY);
            //if(source==1)
                client.changeDirectory("/back-bone/images/drivers/");
//            else if(source==2)
//                client.changeDirectory("/back-bone/images/transporter/");
//            else if(source==3)
//                client.changeDirectory("/back-bone/images/license/");

            for(int i=0;i<fileimages.size();i++)
            {
                client.upload(fileimages.get(i), new MyTransferListener());
            }
        }
        catch (Exception e)
        {
            Log.e("conn uploading back", "" + e.toString());
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                Log.e("disccont",e2.toString());
            }
        }

    }

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            // Transfer started
            //System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {

            // Yet other length bytes has been transferred since the last time this
            // method was called
            //System.out.println(" transferred ..." + length);
        }

        public void completed() {

            // Transfer completed
            //System.out.println(" completed ..." );
        }
        public void aborted() {

            // Transfer aborted
            //System.out.println(" aborted ..." );
        }

        public void failed() {

            // Transfer failed
            System.out.println(" failed ..." );
        }

    }

}
