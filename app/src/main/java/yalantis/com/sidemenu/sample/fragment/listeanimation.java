package yalantis.com.sidemenu.sample.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.android.commons.texts.SpannableBuilder;
import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import yalantis.com.sidemenu.sample.R;
import yalantis.com.sidemenu.sample.adapters.PaintingsAdapter;


public class listeanimation extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private View mListTouchInterceptor;
    private View mDetailsLayout;
    private UnfoldableView mUnfoldableView;
    private OnFragmentInteractionListener mListener;
    ProgressDialog mProgressDialog;


    public static listeanimation newInstance(String param1, String param2) {
        listeanimation fragment = new listeanimation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public listeanimation() {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("List Anomalie");
        View v= inflater.inflate(R.layout.fragment_listeanimation, container, false);


        mListView = (ListView) v.findViewById(R.id.list_view);



        new getAnomalie().execute();

        mListTouchInterceptor = v.findViewById(R.id.touch_interceptor_view);
        mListTouchInterceptor.setClickable(false);

        mDetailsLayout = v.findViewById(R.id.details_layout);
        mDetailsLayout.setVisibility(View.INVISIBLE);

        mUnfoldableView = (UnfoldableView) v.findViewById(R.id.unfoldable_view);

        mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                mDetailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(false);
                mDetailsLayout.setVisibility(View.INVISIBLE);
            }
        });

    return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
            query.whereGreaterThanOrEqualTo("existance", 10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    //Toast.makeText(getActivity(), "Sizeeeeeeeeeeeeeeeeeeeeeeeeeeeee  " + objects.size(), Toast.LENGTH_LONG).show();
                    ParseObject.unpinAllInBackground(objects);
                    mListView.setAdapter(new PaintingsAdapter(getContext(), objects,listeanimation.this));

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mProgressDialog.dismiss();
        }

    }



    public void openDetails(View coverView, ParseObject painting) {
        ImageView image = Views.find(mDetailsLayout, R.id.details_image);
        TextView title = Views.find(mDetailsLayout, R.id.details_title);
        TextView description = Views.find(mDetailsLayout, R.id.details_text);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnfoldableView.foldBack();
            }
        });


        if (painting.getParseFile("photo")==null){


            Glide.with(this)
                    .load(R.drawable.listesansimage2)
                    .dontTransform()
                    .dontAnimate()
                    .into(image);
        }
        else {
            Glide.with(this)
                    .load(painting.getParseFile("photo").getUrl())
                    .dontTransform()
                    .dontAnimate()
                    .into(image);
        }
        title.setText(painting.getString("type"));



        mUnfoldableView.unfold(coverView, mDetailsLayout);
    }


}
