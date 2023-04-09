package osattransport.com.osatdriver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import osattransport.com.osatdriver.Utils.ExtraUtilities;
import osattransport.com.osatdriver.Utils.SessionManagement;
import osattransport.com.osatdriver.Utils.UploadImageBackground;
import osattransport.com.osatdriver.Utils.UserSession;

public class Profile extends AppCompatActivity  implements View.OnClickListener{

    SessionManagement sessionManagement;
    private static final int CAMERA_REQUEST =2 ;
    public static final int PERMISSION_GALLERY_REQUEST=123;
    int permission_gallery=0,permission_camera=0;

    Bitmap imageBitmap=null;


    ImageView selectImage, userImage;

    Button save;
    EditText name, address, city, mobile, email, company, state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        sessionManagement=new SessionManagement(this);

        selectImage=(ImageView)findViewById(R.id.selectImage);
        userImage=(ImageView)findViewById(R.id.userImage);

        name=(EditText)findViewById(R.id.name);
        company=(EditText)findViewById(R.id.companyname);
        mobile=(EditText)findViewById(R.id.mobile);
        email=(EditText)findViewById(R.id.email);
        address=(EditText)findViewById(R.id.address);
        city=(EditText)findViewById(R.id.city);
        state=(EditText)findViewById(R.id.state);
        mobile.setEnabled(false);
        name.setText(UserSession.name);
        address.setText(UserSession.address);
        city.setText(UserSession.city);
        state.setText(UserSession.state);
        mobile.setText(UserSession.mobile);
        email.setText(UserSession.emailID);
        company.setText(UserSession.company);
        Log.e("User Photo : ", UserSession.photopath);
        Picasso.with(this).load("http://osattransport.com/back-bone/"+UserSession.photopath).error(R.drawable.osat_circle_logo).into(userImage);
        selectImage.setOnClickListener(this);
        save=(Button)findViewById(R.id.saveButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent profile=new Intent(getApplication(),OtpForOthers.class);
//                startActivity(profile);

                if(name.getText().toString().equals(""))
                {
                    Toast.makeText(Profile.this, "Name is required!", Toast.LENGTH_LONG).show();
                }

                else if(address.getText().toString().equals(""))
                {
                    Toast.makeText(Profile.this, "Address is required!", Toast.LENGTH_LONG).show();
                }
                else if(city.getText().toString().equals(""))
                {
                    Toast.makeText(Profile.this, "City is required!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    UserSession.name=name.getText().toString();
                    UserSession.address=address.getText().toString();
                    UserSession.city=city.getText().toString();
                    UserSession.state=state.getText().toString();
                    UserSession.emailID=email.getText().toString();
                    UserSession.company=company.getText().toString();
                    UpdateProfile updateProfile=new UpdateProfile();
                    updateProfile.execute("updateProfile");
                }
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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.selectImage)
        {
            final Dialog d=new Dialog(this);
            d.setContentView(R.layout.addphotos_dialog);
            d.setTitle("Choose Source");
            RelativeLayout opencamera=(RelativeLayout)d.findViewById(R.id.opencamera);
            RelativeLayout opengallery=(RelativeLayout)d.findViewById(R.id.opengallery);
            d.show();
            opencamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera();
                    d.dismiss();
                }
            });

            opengallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                    d.dismiss();
                }
            });
        }
    }
    public void openCamera()
    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void openGallery()
    {
        if (permission_gallery==1 ||permission_gallery==-1)
        {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),1);
        }
        else
            Toast.makeText(this,"Allow permissions in app settings",Toast.LENGTH_LONG).show();
    }


    private void checkAndroidVersion()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkPermission();
        }
        else
        {
            permission_gallery=-1;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            permission_gallery=1;
        }
        else if
                (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(this.findViewById(R.id.profile_Layout),"Please Allow Permissions",Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSION_GALLERY_REQUEST);
                            }
                        }).show();
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_GALLERY_REQUEST);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_GALLERY_REQUEST:
                if (grantResults.length >0)
                {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile)
                    {
                        permission_gallery=1;
                        Toast.makeText(this,"Permissions granted",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Snackbar.make(this.findViewById(R.id.profile_Layout).findViewById(android.R.id.content),
                            "Please Grant Permissions to upload profile photo",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    requestPermissions(new String[]{Manifest.permission
                                                    .READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                                            PERMISSION_GALLERY_REQUEST);
                                }
                            }).show();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String selectedImageType = "";

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            try {
                String[] filepathcolumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filepathcolumn, null, null, null);
                cursor.moveToFirst();

                int index = cursor.getColumnIndex(filepathcolumn[0]);

                String imagedecode = cursor.getString(index);

                imageBitmap= BitmapFactory.decodeFile(imagedecode);

                userImage.setImageBitmap(imageBitmap);

                Log.e("imagecode",imagedecode);
                UserSession.tempPhotoPath=imagedecode;

                cursor.close();
                String filename=imagedecode.substring(imagedecode.lastIndexOf("/")+1);
                if(UserSession.userType.equals("DRIVER"))
                {
                    UserSession.photopath="images/drivers/"+filename;
                }
            }
            catch (Exception ee)
            {
                Toast.makeText(this, "not uploaded" + ee, Toast.LENGTH_LONG).show();
            }
        }

        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {

            try
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = ExtraUtilities.getImageUri(this, photo);
                String imagepath = ExtraUtilities.getRealPathFromURI(this, tempUri);
                imageBitmap= BitmapFactory.decodeFile(imagepath);
                userImage.setImageBitmap(imageBitmap);
                Log.e("imagecode",imagepath);
                UserSession.tempPhotoPath=imagepath;
                String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
                if(UserSession.userType.equals("DRIVER"))
                {
                    UserSession.photopath="images/drivers/"+filename;
                }

            }
            catch (Exception ee)
            {
                Toast.makeText(this, "not uploaded" + ee, Toast.LENGTH_LONG).show();
            }
        }

    }

    class UpdateProfile extends AsyncTask<String,Void,String>
    {
        JSONObject json;

        UpdateProfile()
        {
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Profile.this, "Connecting", "Updating..");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params)
        {

            String strURL="http://osattransport.com/back-bone/app/services/drivers/";

            String method=params[0];

            if(method.equals("updateProfile"))
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
                            URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(UserSession.name,"UTF-8")+"&"+
                            URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(UserSession.address,"UTF-8")+"&"+
                            URLEncoder.encode("city","UTF-8")+"="+URLEncoder.encode(UserSession.city,"UTF-8")+"&"+
                            URLEncoder.encode("state","UTF-8")+"="+URLEncoder.encode(UserSession.state,"UTF-8")+"&"+
                            URLEncoder.encode("mobile","UTF-8")+"="+URLEncoder.encode(UserSession.mobile,"UTF-8")+"&"+
                            URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(UserSession.emailID,"UTF-8")+"&"+
                            URLEncoder.encode("company","UTF-8")+"="+URLEncoder.encode(UserSession.company,"UTF-8")+"&"+
                            URLEncoder.encode("photopath","UTF-8")+"="+URLEncoder.encode(UserSession.photopath,"UTF-8")+"&"+
                            URLEncoder.encode("userType","UTF-8")+"="+URLEncoder.encode(UserSession.userType,"UTF-8")+"&"+
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
                    Toast.makeText(Profile.this, "Your Details updated successfully!", Toast.LENGTH_SHORT).show();
                    if (UserSession.userType.equals("DRIVER")) {
                        if(!UserSession.tempPhotoPath.equals("")) {
                            File f = new File(UserSession.tempPhotoPath);
                            ArrayList<File> files = new ArrayList<File>();
                            files.add(f);
                            UploadImageBackground uploadImageBackground = new UploadImageBackground(Profile.this);
                            uploadImageBackground.uploadFile(files, 1);
                            Log.e("Upload Image", "Uploading");
                        }
                    }

                    sessionManagement.createLoginSession(UserSession.mobile, UserSession.userType);
                    sessionManagement.checkLogin();
                    Profile.this.finish();
                }
                else {

                    Toast.makeText(Profile.this, "Not Connected to Service!! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(cntx, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
