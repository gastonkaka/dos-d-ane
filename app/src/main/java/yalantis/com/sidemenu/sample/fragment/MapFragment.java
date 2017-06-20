package yalantis.com.sidemenu.sample.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import yalantis.com.sidemenu.sample.R;




public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Button btnstart,btnstop;
    private String type="" ;
    double longi;
    double lati;
    private GoogleMap mMap;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    int MY_PERMISSIONS_REQUEST_ACCESS_CORSARE_LOCATION=2;
    private FollowMeLocationSource followMeLocationSource;
    private boolean currentlyProcessingLocation = false;


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


      private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment)(getChildFragmentManager().findFragmentById(R.id.map))).getMap();
            // Check if we were successful in obtaining the map.

            if (mMap != null)
            {
                setUpMap();
            }

            //This is how you register the LocationSource
            //donc cette ligne tbaddel il location source illi par defaut setuserlocationenable(true)->nte3 google map
            //c pr cela tetna7a il pt bleu nte3 localisation
            // solution na3mlou boutton start traking ybaddel il location yrodha nte3 gps mouch nte3 google map
            // mMap.setLocationSource(this);
            //mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));




        }

    }

    private void setUpMap(){

        showUserLocation();

        //notre thread illi bech yjib mil parse
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Anomalie");
                query.whereGreaterThanOrEqualTo("existance", 10);
                query.fromLocalDatastore();
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject obj :objects){
                            System.out.println("okok");
                            longi= (float) obj.getParseGeoPoint("position").getLongitude();
                            lati= (float) obj.getParseGeoPoint("position").getLatitude();
                            type= obj.getString("type");
                            System.out.println(longi);

                            LatLng anomalie = new LatLng(lati, longi);

                            mMap.addMarker(new MarkerOptions().position(anomalie).title(type).icon(BitmapDescriptorFactory.fromResource(R.drawable.markermap)));
                            //il toast tal3et ta3mel crash ???!!!!
                            //Toast.makeText(getApplicationContext(), "hh", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();

        //locationManager.getLastKnownLocation()


        // LatLng sydney = new LatLng(36.7, 10.18);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker in Sydney").snippet("Thinking of finding some thing...").icon(BitmapDescriptorFactory.fromResource(R.drawable.markermap))   );
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //static marker with description
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("ahla w sahla").snippet("Thinking of finding some thing...").icon(BitmapDescriptorFactory.fromResource(R.drawable.markermap)));
    }


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // Toast.makeText(getActivity(), "welcome to test2 fragment", Toast.LENGTH_SHORT).show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Map");
        View v= inflater.inflate(R.layout.fragment_map, container, false);
        btnstart=(Button) v.findViewById(R.id.start);
        btnstop=(Button) v.findViewById(R.id.stop);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps();


                if (!currentlyProcessingLocation) {
                    currentlyProcessingLocation = true;
                    followMeLocationSource = new FollowMeLocationSource();
                }

//                 mMap.setLocationSource();
//                mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));

            }
        });


        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(), "stop traking", Toast.LENGTH_SHORT).show();
                mMap.animateCamera(CameraUpdateFactory.zoomTo(0));
                mMap.clear();
                setUpMap();
                followMeLocationSource.stopLocationUpdates();
            }
        });



        setUpMapIfNeeded();
        return  v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }




    //donc syst de premession i jdid dans Marshmallow na3mlou fih request 3al permession en tt real et non pas dans l'install de l'pp
    //mais 3al feature illi test7a9 lil permission donc on pourra gagner plus en terme de confiance avec l'utilisateur ;)
    public void showUserLocation(){



        //nchoufou permission deja majouda ou nn
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //permession mawjouda on affiche userlocation
            //Toast.makeText(getContext(), "Permission déja mawjouda", Toast.LENGTH_LONG).show();

            mMap.setMyLocationEnabled(true);
        }else{//permession mazelt mafaamech
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                //cettet méthode retourne false si ->"dont ask me againn" ou bien "permission disable mi device"
                //cette methode retourne true si ->si user reject permission w tawa 3awdet jétou on affiche un toas pr lui montrer
                //l'importance de la permission
                Toast.makeText(getContext(), "Location permission is needed to show the user locaiton", Toast.LENGTH_LONG).show();
            }
            //demander la permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }

    public void gps() {

        //nchoufou permission deja majouda ou nn
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //permession mawjouda on affiche userlocation
           // Toast.makeText(getContext(), "Permission déja mawjouda", Toast.LENGTH_LONG).show();


        }else{//permession mazelt mafaamech
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                //cettet méthode retourne false si ->"dont ask me againn" ou bien "permission disable mi device"
                //cette methode retourne true si ->si user reject permission w tawa 3awdet jétou on affiche un toas pr lui montrer
                //l'importance de la permission
                Toast.makeText(getContext(), "Location permission is needed to show the user locaiton", Toast.LENGTH_LONG).show();
            }
            //demander la permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_CORSARE_LOCATION);
        }
    }






    //cette fonctino sert a enregistrer la permission de l'utilisateur
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getContext(), "Permession denied", Toast.LENGTH_LONG).show();
            }
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_CORSARE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getContext(), "Permession denied", Toast.LENGTH_LONG).show();
            }
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }


    }

    private class FollowMeLocationSource implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, LocationListener{

        private static final String TAG = "LocationService";
        private boolean currentlyProcessingLocation = false;
        private LocationRequest locationRequest;
        private GoogleApiClient googleApiClient;
        Location lastlocation;

        private Polyline line;
        private ArrayList<LatLng> routePoints= new ArrayList<LatLng>();
        double prevLatitude,prevLongitude,latitude,longitude;
        LatLng latLng;



        public FollowMeLocationSource(){

            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS) {

                googleApiClient = new GoogleApiClient.Builder(getContext())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
            } else {
                Log.e(TAG, "unable to connect to google play services.");
            }



        }
        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected");

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(3000); // milliseconds
            locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            lastlocation=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);




        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {

                lastlocation=location;
                Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

                if (location.getAccuracy() >1.0f) {

//                    Toast.makeText(getContext(), "win méchi ;) " ,Toast.LENGTH_LONG).show();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    latLng = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                    // lien polyline http://stackoverflow.com/questions/24408419/how-to-draw-line-on-google-map-as-user-move-draw-line-automatically
                    PolylineOptions pOptions = new PolylineOptions()
                            .width(7)
                            .color(Color.RED)
                            .geodesic(true);
                    for (int z = 0; z < routePoints.size(); z++) {
                        LatLng point = routePoints.get(z);
                        pOptions.add(point);
                    }
                    line = mMap.addPolyline(pOptions);
                    routePoints.add(latLng);



                    //et on restart Accuracy to 0!!!
                    location.setAccuracy(0);

                }
            }

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            stopLocationUpdates();
            //?????
            //stopSelf();ww

        }
        private void stopLocationUpdates() {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }



    }




}
