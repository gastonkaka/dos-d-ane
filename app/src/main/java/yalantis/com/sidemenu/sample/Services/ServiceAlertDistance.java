package yalantis.com.sidemenu.sample.Services;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import yalantis.com.sidemenu.sample.R;

//houni on a utiliser l'api de google i jdida de localistion pk ? -> tekelch barcha ressources ;)
public class ServiceAlertDistance extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //cette variable va etre envoyer au breadcast MyReceiverNotif pr ne pas ajouter ou modifier une dos d'ane avec l'lago que lorrsqu'on est connécté
    Boolean connectivity=false;
    //last location will be update on the method onLocationChanged and let us add new anomalie when the algo will detect it
    Location lastlocation;
    private static final String MAP_ACTION = "MAP_ACTION";
    private static final String NO_ACTION = "NO_ACTION";
    private SensorManager sensorManager;
    private long lastUpdate;
    SensorEventListener accL;
    Sensor accSensor;
    TextView ax, ay, az;
    int RECENT_COUNT=10;
    //private static final String TAG = "Algorithme";
    int nb=0;
    int ptr = 0;
    //n'ublie pas de recéclarer le tab recent
    vec_d3[] recent = new vec_d3[RECENT_COUNT];
    double variation;
    double DOUBLE_EMPTY = 0;
    vec_d3 smooth = new vec_d3(DOUBLE_EMPTY, 0, 0);
    // intialiser millouwel fi android be
    double var_variation =DOUBLE_EMPTY;
    double var_variation2 = DOUBLE_EMPTY;
    double var_variation3 = DOUBLE_EMPTY;
    double longi,lati;
    HashMap<Double, Double> map = new HashMap<Double, Double>();

    private static final String TAG = "LocationService";
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private int result;
    TextToSpeech tts;

    //ne9sa Location +var statique


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //on peut faire un condition pr s'assurer que le service n'est pas déja vid16:05
        //ca dépends de notre broadcast receiver
        //notre thread illi bech yjib mil parse
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(isNetworkAvailable()==true){
                    //get from parse
                    connectivity=true;
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Anomalie");
                    query.whereGreaterThanOrEqualTo("existance", 10);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            for (ParseObject obj : objects) {
                                longi = obj.getParseGeoPoint("position").getLongitude();
                                lati = obj.getParseGeoPoint("position").getLatitude();
                                map.put(lati, longi);

                                //LatLng anomalie = new LatLng(lati, longi);
                            }
                        }
                    });
                }
                else {
                    //get from local datastore
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Anomalie");
                    query.fromLocalDatastore();
                    query.whereGreaterThanOrEqualTo("existance", 10);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            for (ParseObject obj : objects) {
                                longi = obj.getParseGeoPoint("position").getLongitude();
                                lati = obj.getParseGeoPoint("position").getLatitude();
                                map.put(lati, longi);

                                //LatLng anomalie = new LatLng(lati, longi);
                            }
                        }
                    });
                }
            }
        }).start();



        Iterator<Double> keySetIterator = map.keySet().iterator();
        while(keySetIterator.hasNext()){
            Double key = keySetIterator.next();
            System.out.println("key: " + key + " value: " + map.get(key));
        }

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        /** 3 - Demander un capteur de type Accelerometre **/
        Log.d(TAG,"service algo");
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accL = new accListener();
        /** 5 enregistrer les écouteurs pour les capteurs  et d'acceleration**/
        // ici on peut agir sur le delai
        sensorManager.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_UI);




        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    result=tts.setLanguage(Locale.UK);

                }
                else{
                    Toast.makeText(getApplicationContext(),"not supported",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
        //return START_STICKY; ??????? ou bien super.onstart ??
    }

    private void startTracking(){
        Log.d(TAG, "startTracking");


        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
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
    public void onDestroy() {
        Toast.makeText(this,"service destroyed",Toast.LENGTH_SHORT).show();
        stopLocationUpdates();
        sensorManager.unregisterListener(accL);
        accSensor=null;


        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            lastlocation=location;
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            //Toast.makeText(getApplicationContext(), "win méchi ;) "+ + location.getLatitude() + ", " + location.getLongitude() ,Toast.LENGTH_LONG).show();
            // we have our desired accuracy of 500 meters
            // chaque 500 métre on vérifie on lance la fct distance
            if (location.getAccuracy() >1.0f) {


                alertDistanceFromSpeedBumps(location);


                //et on restart Accuracy to 0!!!
                location.setAccuracy(0);



            }
        }

    }




    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lastlocation= LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
        stopSelf();

    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void alertDistanceFromSpeedBumps(Location location){

        Iterator<Double> keySetIterator = map.keySet().iterator();
        while(keySetIterator.hasNext()){
            Double key = keySetIterator.next();
            Location anomalie = new Location("");
            anomalie.setLatitude(key);
            anomalie.setLongitude(map.get(key));
            if(location.distanceTo(anomalie)<500){

                tts("Speed Bump in less than 500 meters");

            }

        }

    }


    private class accListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {

            Log.d(TAG,"methode sensor changed");
            getAccelerometer(event);

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;
        // Mouvement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        DOUBLE_EMPTY = 0;
        smooth = new vec_d3(DOUBLE_EMPTY, 0, 0);

        {

            if (smooth.x == DOUBLE_EMPTY) {
                // Toast.makeText(this, "everything is 0", Toast.LENGTH_SHORT).show();
                smooth.x = x;
                smooth.y = y;
                smooth.z = z;
                // return;

            }


            Log.d(TAG, "nbupdate sensor" + Integer.toString(nb));
            nb++;


            //definir un fonction mathématique -> définir une méthode :p
            //resultat out ?
            x= (float) SMOOTH_IP(smooth.x, x, 0.9);
            y= (float) SMOOTH_IP(smooth.y, y, 0.9);
            z= (float) SMOOTH_IP(smooth.z, z, 0.9);

        }
        // keep track of last k smoother acceleration values
        // on va remplir 10 smoth un tab recent[10]
        //chaque update de l'accelerométre cette méthode va étre applé
        //cette méthode retourne le tab de smooth quant il est remplie
        //si cette methode n'a pas envore remplie le tab no5rjou mi algo
        {

            boolean gotEnoughData = false;
            recent[ptr] = smooth;
            ptr++;

            if (ptr == RECENT_COUNT) {
                ptr = 0;
                gotEnoughData = true;
//                Toast.makeText(this, "on a remplie 10 smooth", Toast.LENGTH_SHORT).show();
            }
            //if  gotEnoughData == false
            if (!gotEnoughData) {
                // donc ce return ki ne7ih yecrichi car y9ollek  min.x = Math.min(min.x, recent[i].x); -> recent de i null
                // hoouwa lézmou yetna7a car y5arej mil fonction nte3 updatesensor il kbira
                // solution étape nte3 variation ne doit ce lancer que ci on a remplit 10 smooth dans dans le tab de smooth recent[i]
                return;
            }
        }


        Log.d(TAG, " bloubla ");
        variation=variation();


        //actualiser la tab de 10 smooth
        vec_d3[] recent = new vec_d3[RECENT_COUNT];

        Log.d(TAG, "variation" + Double.toString(variation));
        //smooth it definie mil fou9
//        double var_smoothed = DOUBLE_EMPTY;
        {
            if (var_variation == DOUBLE_EMPTY) {
                var_variation = variation;
                return;
            }
//            ////on var_smothed fih il variation(de 10 smoth) et 0.9 ?????
//            //Adjusting the RECENT_COUNT or the smoothing coefficient 0.9 should enable you to reduce the sensitivity to high-frequency bumps.
//
//            //conversion peut étre ta3mel prob !
            var_variation=SMOOTH_IP(var_variation, (float) variation, 0.9);

            var_variation=variation;
        }

        // see if it's just passed a peak
        //on a defenie varSmoothed_last et varSmoother_preLast kolwa7da fiha il variation(de 10 smoth)
        //par default var_smoothed=varSmoother_preLast = varSmoothed_last;

        {
            //deffiniton wallet mil fou9
            //double varSmoothed_last = DOUBLE_EMPTY;
            if (var_variation2 == DOUBLE_EMPTY) {
                var_variation2=var_variation;
                //ce return va changer la var_variation intiale du coups var_variaion2 <> de var_variation2
                return;
            }
            //Ddefinie mil fou9
            //double varSmoother_preLast = DOUBLE_EMPTY;
            if (var_variation3 == DOUBLE_EMPTY) {
                var_variation3 = var_variation2;
                var_variation2 = var_variation;
                //return va permettre de chager la valeur var_variation !!!!!!!!!!!!!!!!
                return;
            }


            double THRESHOLD_IMPLUSE =18;
            //pr les chausse degradee -> #define THRESHOLD_IMPULSE .15
            //Adjust the THRESHOLD_IMPULSE will probably affect the amplitude cut-off.


            //this solution is good when you tap phone against your palm

            if ((var_variation2 > var_variation3) &&
                    (var_variation2 > var_variation) && (var_variation2 > THRESHOLD_IMPLUSE)) {



                sendAction2notification();
                //Log.e(TAG, "55555555555555555555555555555555555555555555555555555555555555555555");

                //Toast.makeText(this, "anomalie detected with algo", Toast.LENGTH_SHORT).show();

            }
            else{
//                    Log.d(TAG, "varSmoothed_last"+Double.toString(var_variation2));
//                    Log.d(TAG, "varSmoother_preLast"+Double.toString(var_variation3));
//                    Log.d(TAG, "var_smoothed"+Double.toString(var_variation));

            }

            var_variation3 = var_variation2;
            var_variation2 = var_variation;
        }


    }

    public double SMOOTH_IP(double x, double x_new, double fac){
        return fac * x + (1. - fac) * x_new;
    }

    public double  variation() {

        {

            vec_d3 min=smooth;
            vec_d3 max=smooth;


            Log.d(TAG, "min initiale" + Double.toString(min.x));

            //ne5dhou il min w max pr tt le tab reccent[i]
            for (int i = 0; i < RECENT_COUNT; i++) {
                min.x = Math.min(min.x, recent[i].x);
                min.y = Math.min(min.y, recent[i].y);
                min.z = Math.min(min.z, recent[i].z);
            }
            Log.d(TAG, " minfinale" + Double.toString(min.x));
            double rimti=min.x;
            double minY=min.y;
            double minZ=min.z;


            for (int i = 0; i < RECENT_COUNT; i++) {

                max.x = Math.max(max.x, recent[i].x);
                max.y = Math.max(max.y, recent[i].y);
                max.z = Math.max(max.z, recent[i].z);
            }
            Log.d(TAG, " maxfinale" + Double.toString(max.x));
            double nhebek=max.x;
            double maxY=max.y;
            double maxZ=max.z;


            vec_d3 V = new vec_d3(rimti - nhebek,
                    maxY - minY, maxZ - minZ);
            Log.d(TAG, "x,y,z pour  V"+Double.toString(V.x));
            Log.d(TAG, "x,y,z pour  V"+Double.toString(V.y));
            Log.d(TAG, "x,y,z pour  V"+Double.toString(V.z));

            //sqrt[(moy accelx)*(moy accelx)+(moy accely)*(moy accely)+(moy accel z)*(moy accel z)]
            variation = Math.sqrt(
                    V.x * V.x +
                            V.y * V.y +
                            V.z * V.z
            );

        }

        return variation;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void tts(String chaine){

        if(result==TextToSpeech.LANG_NOT_SUPPORTED||result==TextToSpeech.LANG_MISSING_DATA){
            Toast.makeText(getApplicationContext(),"Text to speach not supported",Toast.LENGTH_SHORT).show();

        }
        else {

            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
            am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
            tts.speak(chaine, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


    public void sendAction2notification(){

        tts("Speed Bump Detected");

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            Toast.makeText(getApplicationContext(), "GPS is disabled!", Toast.LENGTH_LONG).show();
        else
        {

            // intent  ajout
            Intent yesReceive = new Intent();
            yesReceive.putExtra("latitude",lastlocation.getLatitude());
            yesReceive.putExtra("longitude", lastlocation.getLongitude());
            yesReceive.putExtra("connectivity",connectivity);
            yesReceive.setAction(MAP_ACTION);
            PendingIntent pendingIntentmap = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);


            //No intent
            Intent noReceive = new Intent();
            noReceive.setAction(NO_ACTION);
            PendingIntent pendingIntentcancel = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.markermap)
                            .setContentTitle("Speed Bump Detected")
                            .setContentText("Was it really a speed bump ?").setAutoCancel(true);
            mBuilder.addAction(R.drawable.ic_action_map,"Anomalie",pendingIntentmap);
            mBuilder.addAction(R.drawable.ic_action_cancel, "Nothing", pendingIntentcancel);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            int mId=2;
            mNotificationManager.notify(mId, mBuilder.build());

        }}





    //check network connection method
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }







}
