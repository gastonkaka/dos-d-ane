package yalantis.com.sidemenu.sample.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyReceiverNotif extends BroadcastReceiver {

    private static final String MAP_ACTION = "MAP_ACTION";
    public NotificationManager myNotificationManager;
    String id;
    int existance;

    Boolean connectivity;
    ParseGeoPoint lastgeopoint=new ParseGeoPoint(0,0);
    Context c;

    public MyReceiverNotif() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        c=context;


        String action = intent.getAction();
        myNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //remove the notif
        myNotificationManager.cancel(2);

        //if user has confirm the notif
        if(action.equals(MAP_ACTION)) {


                Bundle extras = intent.getExtras();
                if (extras != null) {
                    lastgeopoint.setLatitude(extras.getDouble("latitude"));
                    lastgeopoint.setLongitude(extras.getDouble("longitude"));
                    connectivity = extras.getBoolean("connectivity");
                }

                Log.e("aze", "latitude:" + lastgeopoint.getLatitude() + "longitide:" + lastgeopoint.getLongitude());

            if (isNetworkAvailable()) {
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Anomalie");
                query.whereNear("position", lastgeopoint);
                query.setLimit(1);
                query.whereWithinKilometers("position", lastgeopoint, 0.05);
                //query.fromLocalDatastore();
         /*       query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {


                        for (ParseObject obj : objects) {
                            id = obj.getObjectId();
                            Log.e("dos d'ane mawjouda", String.valueOf(objects.size()));
                            existance = obj.getNumber("existance");
                            Log.e("existance intiale", existance.toString());


                        }

                    }

                });
                */
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (object != null) {
                            existance =(int) object.getNumber("existance");
                            existance++;
                            Log.e("new existance", existance+"");


                            ParseQuery<ParseObject> queryy = ParseQuery.getQuery("Anomalie");

                            queryy.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
                                public void done(ParseObject gameScore, ParseException e) {
                                    if (e == null) {
                                        gameScore.put("existance", existance);
                                        //gameScore.saveEventually();
                                        gameScore.saveInBackground();
                                        ParseQuery<ParseObject> queryDelete = ParseQuery.getQuery("Anomalie");
                                        queryDelete.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                for (ParseObject obj : objects) {
                                                    if (obj.getString("type")==null)
                                                        obj.deleteInBackground();
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        } else {

                            ParseObject gameScore = new ParseObject("Anomalie");
                            gameScore.put("existance", 1);
                            gameScore.put("direction", "nouvelle");
                            gameScore.put("position", lastgeopoint);
                            gameScore.put("type", "dos d'âne");
                            //gameScore.saveEventually();
                            try {
                                gameScore.saveInBackground().waitForCompletion();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                           ParseQuery<ParseObject> queryDelete = ParseQuery.getQuery("Anomalie");
                            queryDelete.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                for(ParseObject obj : objects)
                                {
                                    if (obj.getString("type")==null)
                                    obj.deleteInBackground();
                                }
                                }
                            });


                        }

                    }

                });



            }
            else
                Toast.makeText(context,"etablish ur connecion and restart the service " , Toast.LENGTH_SHORT).show();



          //if user pressed nothing
        } else {
            Log.v("shuffTest", "Pressed cancel");

        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
