package yalantis.com.sidemenu.sample.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import yalantis.com.sidemenu.sample.R;

public class FragmentSignaler extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final int CAMERA_REQUEST = 10;
    private ImageView imga;
    private CheckBox ch1, ch2;
    ImageView imgg;
    Button btn ;
    Button signaler;
    String type;



    public static FragmentSignaler newInstance(String param1, String param2) {
        FragmentSignaler fragment = new FragmentSignaler();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSignaler() {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Take a Photo");
        View v= inflater.inflate(R.layout.fragment_fragment_signaler, container, false);
        imga = (ImageView) v.findViewById(R.id.img);
        signaler= (Button) v.findViewById(R.id.signaelr);
        btn= (Button) v.findViewById(R.id.buttonTakePhoto);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);


            }
        });

        ch1 = (CheckBox) v.findViewById(R.id.dos);
        ch2 = (CheckBox) v.findViewById(R.id.chausse);


        signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!ch1.isChecked()) && (!ch2.isChecked())) {
                    Toast.makeText(getContext(), "Would you please select the type !", Toast.LENGTH_LONG).show();
                }

                else if((ch1.isChecked()) && (ch2.isChecked())){
                    Toast.makeText(getContext(), "Would you please select one type !", Toast.LENGTH_LONG).show();
                }
                else if(ch1.isChecked()){
                        type="dos d'âne";
                        onCheckboxClicked();
                }

                else
                {
                        type="chaussée dégradée";
                        onCheckboxClicked();
                }
                }

        });


        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == CAMERA_REQUEST){
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                imga.setImageBitmap(cameraImage);

            }

        }
    }


    public void onCheckboxClicked(){

            ParseObject gameScore = new ParseObject("Anomalie");
            gameScore.put("existance", 0);
            gameScore.put("type", type);
        ParseGeoPoint point = new ParseGeoPoint(40.0, -30.0);
            gameScore.put("position", point);
       /*     Bitmap image = R.id.class(cam) ;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            gameScore.put("photo", data);*/
            gameScore.saveInBackground();
            Toast.makeText(getContext(), "vous avez signaler une dos d'ane avec succés", Toast.LENGTH_LONG).show();



    }






}
