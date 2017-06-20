package yalantis.com.sidemenu.sample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import yalantis.com.sidemenu.sample.R;
import yalantis.com.sidemenu.sample.fragment.listeanimation;

import com.alexvasilkov.android.commons.adapters.ItemsAdapter;
import com.alexvasilkov.android.commons.utils.Views;
import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.List;

public class PaintingsAdapter extends ItemsAdapter<ParseObject> implements View.OnClickListener {


    private final listeanimation fragment;
    private LayoutInflater inflater;
    List<ParseObject> dodane;
    Context context;
    public PaintingsAdapter(Context context,List<ParseObject> dodane,listeanimation fragment) {
        super(context);
        this.context=context;
        this.dodane= dodane;
        this.fragment=fragment;
        // ??
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       setItemsList(dodane);
    }

    @Override
    protected View createView(ParseObject item, int pos, ViewGroup parent, LayoutInflater inflater) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listeanimation, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.img = Views.find(view, R.id.list_item_image);
        vh.img.setOnClickListener(this);
        vh.type = Views.find(view, R.id.list_item_title);
        view.setTag(vh);

        return view;
    }

    @Override
    protected void bindView(ParseObject item, int pos, View convertView) {

        ViewHolder vh = (ViewHolder) convertView.getTag();


        //Toast.makeText(getContext(),"Imagegg "+holder.tr.getParseFile("photo").getUrl(),Toast.LENGTH_LONG).show();
        //test si il anomalie sans photo
        if (item.getParseFile("photo")==null){


            Glide.with(convertView.getContext())
                    .load(R.drawable.listesansimage2)
                    .dontTransform()
                    .dontAnimate()
                    .into(vh.img);
        }
        else {
            vh.img.setTag(R.id.list_item_image, item);
            Glide.with(convertView.getContext())
                    .load(item.getParseFile("photo").getUrl())
                    .dontTransform()
                    .dontAnimate()
                    .into(vh.img);
        }


        //Toast.makeText(getContext(),"type "+item.getString("type"),Toast.LENGTH_LONG).show();


        vh.type.setText(item.getString("type"));
    }

    @Override
    public void onClick(View view) {

        ParseObject item = (ParseObject) view.getTag(R.id.list_item_image);
        fragment.openDetails(view, item);
    }


    private static class ViewHolder {
        TextView type;
        TextView exisatance;
        ParseObject tr;
        ImageView img;

    }

}
