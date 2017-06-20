package yalantis.com.sidemenu.sample.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import yalantis.com.sidemenu.sample.R;

public class AboutUsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btn;
    Button btn1;

    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutUsFragment() {
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


        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("About Us");
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_about_us, container, false);
        btn= (Button) v.findViewById(R.id.btncontact);
        btn1= (Button) v.findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("adresse email")
                        .setMessage(("Ghassen.mejri@esprit.tn" +
                                "    Mehdi.jarraya@esprit.tn"))

                                        .setIcon(android.R.drawable.ic_popup_reminder)
                                        .show();

              //  Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
                //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               // startActivity(intent);

            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                Uri uri = Uri.parse("http://www.facebook.com/dosdanetunisie"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
        return v;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }





}
