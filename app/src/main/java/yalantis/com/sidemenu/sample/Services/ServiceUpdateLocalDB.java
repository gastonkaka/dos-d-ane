package yalantis.com.sidemenu.sample.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ServiceUpdateLocalDB extends Service {
    public ServiceUpdateLocalDB() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(),"updtelocal",Toast.LENGTH_SHORT).show();
        if(isNetworkAvailable()==true) {
            new aaaaaaaaaa().execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }



    private class aaaaaaaaaa extends AsyncTask<String,Void,Void>
    {


        @Override
        protected Void doInBackground(String... params) {


            // Syncing Network Changes
            ParseQuery<ParseObject> networkQueryScores = ParseQuery
                    .getQuery("Anomalie");

            // Query for new results from the network.
            networkQueryScores.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> anomalies, ParseException e) {

                    Log.d("size", "list size = " + anomalies.size());

                    // Remove the previously cached results.
                    ParseObject.unpinAllInBackground("Anomalie",
                            new DeleteCallback() {
                                public void done(ParseException e) {
//                                    // Cache the new results.
                                    ParseObject.pinAllInBackground("Anoamlie",
                                            anomalies);
                                    try {
                                        ParseObject.unpinAll("Anomalie");
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                }
            });

            return null;
        }



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}
