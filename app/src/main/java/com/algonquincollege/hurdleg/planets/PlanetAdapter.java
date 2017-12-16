package com.algonquincollege.hurdleg.planets;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.hurdleg.planets.model.PlanetPOJO;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import static com.algonquincollege.hurdleg.planets.MainActivity.BASE_URL;

/**
 * Purpose: customize the PlanetPOJO cell for each planet displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on LazyLoad in "Connecting Android Apps to RESTful Web Services" with David Gassner
 * Reference: https://www.androidtutorialpoint.com/networking/android-retrofit-2-0-tutorial-retrofit-android-example-download-image-url-display-android-device-screen/
 */
public class PlanetAdapter extends ArrayAdapter<PlanetPOJO> {

    private Context          mContext;
    private List<PlanetPOJO> mPlanetsList;

    public PlanetAdapter(Context context, int resource, List<PlanetPOJO> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mPlanetsList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.item_planet, parent, false);

        //Display planet name in the TextView widget
        final PlanetPOJO planet = mPlanetsList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(planet.getName());

        //Display planet image in the ImageView widget
        ImageView image = (ImageView) view.findViewById(R.id.imageView1);

        // force picasso to fetch the planet's image from the internet, and not use the cache
        // handles the case when the image for Pluto is updated
        Picasso.with(mContext)
                .load(BASE_URL + "planets/" + planet.getPlanetId() + "/image")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .fit()
                .into(image);

        return view;
    }
}
