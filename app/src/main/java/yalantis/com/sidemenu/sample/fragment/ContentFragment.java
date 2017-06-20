package yalantis.com.sidemenu.sample.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.q42.android.scrollingimageview.ScrollingImageView;
import com.squareup.picasso.Picasso;

import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.sample.R;
import yalantis.com.sidemenu.sample.Services.ServiceAlertDistance;
import yalantis.com.sidemenu.sample.Services.ServiceUpdateLocalDB;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragment extends Fragment implements ScreenShotable {
    public static final String CLOSE = "Close";
    public static final String BUILDING = "Building";
    public static final String BOOK = "Book";
    public static final String PAINT = "Paint";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";
    public static final String MOVIE = "Movie";

    private View containerView;
    protected ImageView mImageView;
    protected int res;
    private Bitmap bitmap;
    Switch mySwitch;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    int MY_PERMISSIONS_REQUEST_ACCESS_CORSARE_LOCATION=2;
    Boolean permessionallwed=false;
    ScrollingImageView scrollingBackground;
    boolean animation=false;
    private ProgressDialog mProgressDialog;
    private TextView type;
    private TextView exisatance;
    private ImageView img;


    public static ContentFragment newInstance(int resId) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Integer.class.getName(), resId);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getArguments().getInt(Integer.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Home");
        type = (TextView) rootView.findViewById(R.id.tvType);
        exisatance = (TextView) rootView.findViewById(R.id.tvExistance);
        img=(ImageView) rootView.findViewById(R.id.img);

        new getAnomalie().execute();

        final ScrollingImageView scrollingBackground = (ScrollingImageView) rootView.findViewById(R.id.scrolling_background);
        scrollingBackground.stop();


        permessionFineLocation();

        //about the switch
        mySwitch = (Switch) rootView.findViewById(R.id.mySwitch);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (permessionallwed==true) {
                    if (isChecked) {
                        if (isNetworkAvailable() == true) {
                            Intent i2 = new Intent(getContext(), ServiceUpdateLocalDB.class);
                            getActivity().startService(i2);
                        }
                        Intent i = new Intent(getContext(), ServiceAlertDistance.class);
                        getActivity().startService(i);
                        //Toast.makeText(getContext(), "All services started", Toast.LENGTH_LONG).show();

                        if(animation==false){
                            scrollingBackground.start();
                        }
                        else{
                            scrollingBackground.stop();
                        }

                    } else {

                        Intent i = new Intent(getContext(), ServiceAlertDistance.class);
                        getActivity().stopService(i);
                        //Toast.makeText(getContext(), "All services stopped", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    permessionFineLocation();
                }
            }
        });


        return rootView;
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }



    //check network connection method
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //a propos les permissions
    //donc syst de premession i jdid dans Marshmallow na3mlou fih request 3al permession en tt real et non pas dans l'install de l'pp
    //mais 3al feature illi test7a9 lil permission donc on pourra gagner plus en terme de confiance avec l'utilisateur ;)
    @TargetApi(Build.VERSION_CODES.M)
    public void permessionFineLocation(){

        //nchoufou permission deja majouda ou nn
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //permession mawjouda on affiche userlocation
//                Toast.makeText(getApplicationContext(), "Permission déja mawjouda", Toast.LENGTH_LONG).show();
            permessionallwed=true;
//                mMap.setMyLocationEnabled(true);
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


    //cette fonctino sert a enregistrer la permission de l'utilisateur
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                permessionallwed = true;
//                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getContext(), "Permession denied", Toast.LENGTH_LONG).show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_CORSARE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permessionallwed = true;
//                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getContext(), "Permession denied", Toast.LENGTH_LONG).show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private class getAnomalie extends AsyncTask<String,Void,Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Liste Anomalies");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Anomalie");
            query.fromLocalDatastore();
            query.orderByDescending("updatedAt");
            query.whereGreaterThanOrEqualTo("existance", 10);
            query.getFirstInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        Log.d("anomalie", "The getFirst request failed.");

                    } else {
                        // got the most recently modified object... do something with it here

                       // Toast.makeText(getContext(),"type"+object.getString("type"),Toast.LENGTH_LONG).show();
                        String typee="Type: ";
                        type.setText(typee+object.getString("type"));
                        String existancee="Existance: ";
                      exisatance.setText(existancee+object.getNumber("existance").toString());

                        //Toast.makeText(getContext(),"Imagegg "+holder.tr.getParseFile("photo").getUrl(),Toast.LENGTH_LONG).show();
                        //test si il anomalie sans photo
                        if (object.getParseFile("photo")==null){

                            img.setImageResource(R.drawable.listesansimage);
                        }
                        else {
                            Picasso.with(getContext()).load(object.getParseFile("photo").getUrl()).into(img);
                        }

                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mProgressDialog.dismiss();
        }

    }


    }

