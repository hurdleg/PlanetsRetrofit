package com.algonquincollege.hurdleg.planets;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.hurdleg.planets.model.Planet;

/**
 * Purpose: customize the Planet cell for each planet displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on LazyLoad in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class PlanetAdapter extends ArrayAdapter<Planet> {

    private Context context;
    private List<Planet> planetList;

    // TODO: cache the binary image for each planet
    private LruCache<Integer, Bitmap> imageCache;

    public PlanetAdapter(Context context, int resource, List<Planet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.planetList = objects;

        // TODO: instantiate the imageCache
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_planet, parent, false);

        //Display planet name in the TextView widget
        Planet planet = planetList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(planet.getName());

        // TODO: Display planet photo in ImageView widget
        Bitmap bitmap = imageCache.get(planet.getPlanetId());
        if (bitmap != null) {
            Log.i( "PLANETS", planet.getName() + "\tbitmap in cache");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(planet.getBitmap());
        }
        else {
            Log.i( "PLANETS", planet.getName() + "\tfetching bitmap using AsyncTask");
            PlanetAndView container = new PlanetAndView();
            container.planet = planet;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    // container for AsyncTask params
    private class PlanetAndView {
        protected Planet planet;
        protected View view;
        protected Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<PlanetAndView, Void, PlanetAndView> {

        @Override
        protected PlanetAndView doInBackground(PlanetAndView... params) {

            PlanetAndView container = params[0];
            Planet planet = container.planet;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + planet.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                planet.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                System.err.println("IMAGE: " + planet.getName() );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PlanetAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
//            result.planet.setBitmap(result.bitmap);
            imageCache.put(result.planet.getPlanetId(), result.bitmap);
        }
    }
}
