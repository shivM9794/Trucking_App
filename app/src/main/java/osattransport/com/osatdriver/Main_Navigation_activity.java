package osattransport.com.osatdriver;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import osattransport.com.osatdriver.Utils.SessionManagement;
import osattransport.com.osatdriver.Utils.UserSession;

public class Main_Navigation_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main__navigation_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Driver Dashboard");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader;
        navHeader = navigationView.getHeaderView(0);

        ImageView imgProfile = (ImageView)navHeader.findViewById(R.id.imageView);

        TextView headerName= (TextView )navHeader.findViewById(R.id.headerName);
        TextView headerPhone= (TextView )navHeader.findViewById(R.id.headerPhone);

        Picasso.with(this).load("http://osattransport.com/back-bone/" + UserSession.photopath).resize(100,100).error(R.drawable.osat_circle_logo).placeholder(R.drawable.osat_circle_logo).noFade().into(imgProfile);

        Log.e("Name : ", UserSession.name);
        Log.e("Mobile : ", UserSession.mobile);
        Log.e("Photo: ", UserSession.photopath);

        headerName.setText(UserSession.name);
        headerPhone.setText(UserSession.mobile);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        checkPermissions();
        if(UserSession.isActive.equals("NO"))
        {

            final Dialog d=new Dialog(this);
            d.setContentView(R.layout.activate_dialog);
            d.setCanceledOnTouchOutside(false);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Main_Navigation_activity.this.finish();
                }
            });
            TextView message=(TextView)d.findViewById(R.id.txt_message);
            TextView title=(TextView)d.findViewById(R.id.title);
            CardView yesButton=(CardView)d.findViewById(R.id.yesButton);
            title.setText("NOTICE!");
            message.setText("  Your account not activated yet! Kindly contact 9313002200 to activate your account.  ");
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(d!=null)
                        d.dismiss();
                    finish();
                }
            });
            if(d!=null)
                d.show();
        }
        Home_panel hm=new Home_panel();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container_2,hm).disallowAddToBackStack().commit();
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[5]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[6]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[7]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[8]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[9]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[10]) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
        } else {
            //just request the permission
            ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                    Log.e("Permission "+i, ""+permissionStatus.getBoolean(permissionsRequired[i], false));
                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(permissionsRequired[i],true);
                    editor.commit();
                } else {
                    allgranted = false;
                    break;
                }
            }
            if(allgranted==false) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[5])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[6])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[7])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[8])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[9])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[10])) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main_Navigation_activity.this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("This app needs Camera,Storage, Contacts, SMS and Location permissions. ");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(Main_Navigation_activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Creates textview
                    TextView text = new TextView(this);
                    text.setText("This app needs Camera,Storage, Contacts, SMS and Location permissions.");
                    text.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
                    text.setTextSize(13);
                    text.setGravity(Gravity.CENTER);

                    //Creates a linearlayout layout and sets it with initial params
                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.setGravity(Gravity.CENTER);
                    ll.addView(text);  //adds textview to llayout
                    Dialog d = builder.setView(ll).create();
                    d.setTitle("Need Multiple Permissions");

                    //Fills up the entire Screen
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(d.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    d.show();
                    d.getWindow().setAttributes(lp);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main_Navigation_activity.this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("This app needs Camera,Storage, Contacts, SMS and Location permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "Go to Permissions to Grant Camera, Storage, Contacts, SMS and Location", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Creates textview
                    TextView text = new TextView(this);
                    text.setText("This app needs Camera,Storage, Contacts, SMS and Location permissions.");
                    text.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
                    text.setTextSize(13);
                    text.setGravity(Gravity.CENTER);

                    //Creates a linearlayout layout and sets it with initial params
                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.setGravity(Gravity.CENTER);
                    ll.addView(text);  //adds textview to llayout
                    Dialog d = builder.setView(ll).create();
                    d.setTitle("Need Multiple Permissions");

                    //Fills up the entire Screen
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(d.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    d.show();
                    d.getWindow().setAttributes(lp);

                    //  builder.show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__navigation_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Dialog d=new Dialog(this);
            d.setContentView(R.layout.confirm_dialog);
            TextView message=(TextView)d.findViewById(R.id.txt_message);
            TextView title=(TextView)d.findViewById(R.id.title);
            CardView yesButton=(CardView)d.findViewById(R.id.yesButton);
            CardView noButton=(CardView)d.findViewById(R.id.noButton);
            title.setText("Warning");
            message.setText("  Do you want to logout from you session?  ");
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    d.dismiss();
                    SessionManagement sessionManagement=new SessionManagement(Main_Navigation_activity.this);
                    sessionManagement.destroySession();
                    UserSession.userID="0";
                    UserSession.name="";
                    UserSession.userType="";
                    UserSession.photopath="";
                    UserSession.mobile="";
                    UserSession.state="";
                    UserSession.city="";
                    UserSession.company="";
                    UserSession.address="";
                    UserSession.emailID="";
                    UserSession.tempPhotoPath="";
                    UserSession.isActive="";
                    Intent intent=new Intent(Main_Navigation_activity.this, Main_fragment_activity.class);
                    startActivity(intent);
                    finish();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });
            d.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_allorders) {

            Intent intent=new Intent(this,All_orders.class);
            startActivity(intent);

        } else if (id == R.id.nav_Profile) {
            Intent intent=new Intent(this,Profile.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "India's Best App for Truck Booking");
            String sAux = "\nDownload OSAT for Truck Booking and view live tracking of truck\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=osattransport.com.osat\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share App on"));
        }
        else if (id == R.id.nav_call) {
            Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:9313002200"));
            startActivity(intent);
        }

        else if (id == R.id.nav_watch) {

            Intent intent=new Intent(this,YouTubeActivity.class);
            startActivity(intent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
