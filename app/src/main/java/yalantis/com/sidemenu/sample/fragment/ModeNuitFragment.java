package yalantis.com.sidemenu.sample.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yalantis.com.sidemenu.sample.R;
import yalantis.com.sidemenu.sample.Services.ServiceAlertDistance;

public class ModeNuitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    // TODO: Rename and change types and number of parameters
    public static ModeNuitFragment newInstance(String param1, String param2) {
        ModeNuitFragment fragment = new ModeNuitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ModeNuitFragment() {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Night Mode");
        View v= inflater.inflate(R.layout.fragment_mode_nuit, container, false);
//        if
//            ( getActivity().getSystemService(ServiceAlertDistance.class).getNuit()){
//            v.setBackground();
//
//        }

        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }





}
