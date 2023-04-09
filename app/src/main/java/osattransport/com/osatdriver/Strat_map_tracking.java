package osattransport.com.osatdriver;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import osattransport.com.osatdriver.Utils.ExtraUtilities;
import osattransport.com.osatdriver.Utils.GPSTracker;

public class Strat_map_tracking extends FragmentActivity implements OnMapReadyCallback, Runnable {

    private GoogleMap mMap;
    private ArrayList<LatLng> points; //added
    Polyline line; //added

    String strFromLocation, strToLocation, driverOrderID, strCurrentLocation;

    Marker marker =null;

    LatLng fromLatLng;
    LatLng toLatLng;

    Handler handler;
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strat_map_tracking);
        points = new ArrayList<LatLng>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        strFromLocation=getIntent().getExtras().get("fromLocation").toString();
        strToLocation=getIntent().getExtras().get("toLocation").toString();
        driverOrderID=getIntent().getExtras().get("driverorderid").toString();

        handler=new Handler();
        mapFragment.getMapAsync(this);
    }


    @Override
    public void run() {
        // create class object
        gps = new GPSTracker(Strat_map_tracking.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            strCurrentLocation=latitude + "," + longitude;
            LatLng newLatLng = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    newLatLng).zoom(12).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            marker.setPosition(newLatLng);

            new UpdateDriverLocation().execute("UpdateLocation");
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        fromLatLng = null;
        toLatLng= null;

        try {
            fromLatLng = ExtraUtilities.getLocationFromString(strFromLocation+", India");
            toLatLng= ExtraUtilities.getLocationFromString(strToLocation+", India");

        }
        catch (Exception ee)
        {}
        if(fromLatLng!=null && toLatLng!=null)
        {
            String url = getMapsApiDirectionsUrl(fromLatLng, toLatLng);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    fromLatLng).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        handler.postDelayed(Strat_map_tracking.this,1);

//        GeocodingLocation obb=new GeocodingLocation();
//        obb.getAddressFromLocation(strFromLocation+", India", this, new FromGeocoderHandler());
//        obb.getAddressFromLocation(strToLocation+", India", this, new ToGeocoderHandler());

    }
    private class FromGeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if(locationAddress!=null)
            {
                strCurrentLocation=locationAddress;
                String arr[]=locationAddress.split(",");
                fromLatLng=new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                marker= mMap.addMarker(new MarkerOptions().position(fromLatLng).title(""));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        fromLatLng).zoom(12).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);

                handler.postDelayed(Strat_map_tracking.this,1);


            }
            Log.e("Location Address :", locationAddress);
        }
    }
    private class ToGeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if(locationAddress!=null)
            {
                String arr[]=locationAddress.split(",");
                toLatLng=new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                if(fromLatLng!=null && toLatLng!=null)
                {
                    String url = getMapsApiDirectionsUrl(fromLatLng, toLatLng);
                    ReadTask downloadTask = new ReadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);


                }
            }

            Log.e("Location Address :", locationAddress);
        }
    }
    ////////////////////////////////////////
    private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;

    }

    private class ReadTask extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }
    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line ="";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();


            }
            catch (Exception e) {
                Log.d("Exception reading url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }
    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i=0 ; i < jRoutes.length() ; i ++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String,String>>();
                    for(int j = 0 ; j < jLegs.length() ; j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for(int k = 0 ; k < jSteps.length() ; k ++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for(int l = 0 ; l < list.size() ; l ++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }}
    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Strat_map_tracking.this.getResources().getColor(R.color.colorPrimaryDark));
            }

            mMap.addPolyline(polyLineOptions);

        }}



    class UpdateDriverLocation extends AsyncTask<String,Void,String> {

        JSONObject json;
        String myresult=null;

        public UpdateDriverLocation ()
        {
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {


            String userdetails_url="http://osattransport.com/back-bone/app/services/updatedriverlocation/";

            String method=params[0];

            if(method.equals("UpdateLocation"))
            {

                try {
                    URL url=new URL(userdetails_url);
                    HttpURLConnection httpconnection=(HttpURLConnection) url.openConnection();
                    httpconnection.setRequestMethod("POST");
                    httpconnection.setDoOutput(true);
                    httpconnection.setDoInput(true);
                    OutputStream outputstream=httpconnection.getOutputStream();
                    BufferedWriter bufferwriter=new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                    String data= URLEncoder.encode("query","UTF-8")+"="+URLEncoder.encode(method,"UTF-8")+"&"+
                            URLEncoder.encode("driverorderid","UTF-8")+"="+URLEncoder.encode(driverOrderID,"UTF-8")+"&"+
                            URLEncoder.encode("cuurentLocation","UTF-8")+"="+URLEncoder.encode(strCurrentLocation,"UTF-8");

                    Log.e("Driver Order ID : ", driverOrderID);
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
                    Log.e("Driver Location Error ", e+"");
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

            handler.postDelayed(Strat_map_tracking.this,1);
        }
    }


}
