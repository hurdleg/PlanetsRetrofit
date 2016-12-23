package com.algonquincollege.hurdleg.planets;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.retrofit.PlanetsAPI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.algonquincollege.hurdleg.planets.MainActivity.BASE_URL;

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
 * Reference: https://www.androidtutorialpoint.com/networking/android-retrofit-2-0-tutorial-retrofit-android-example-download-image-url-display-android-device-screen/
 */
public class PlanetAdapter extends ArrayAdapter<Planet> {

    private Context context;
    private List<Planet> planetList;

    private LruCache<Integer, Bitmap> imageCache;

    public PlanetAdapter(Context context, int resource, List<Planet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.planetList = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.item_planet, parent, false);

        //Display planet name in the TextView widget
        final Planet planet = planetList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(planet.getName());

        Bitmap bitmap = imageCache.get(planet.getPlanetId());
        if (bitmap != null) {
            Log.i( "PLANETS", planet.getName() + "\tbitmap in cache");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(bitmap);
        }
        else {
            //TODO: Retrofit to get the planet's image
            Log.i( "PLANETS", planet.getName() + "\tfetching bitmap using Retrofit");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PlanetsAPI service = retrofit.create(PlanetsAPI.class);

            Call<ResponseBody> call = service.getPlanetImage(planet.getPlanetId());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    planet.setBitmap(bitmap);
                    ImageView image = (ImageView) view.findViewById(R.id.imageView1);
                    image.setImageBitmap(bitmap);
                    imageCache.put(planet.getPlanetId(), bitmap);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("onFailure", t.getLocalizedMessage());
                }
            });
        }

        return view;
    }
}
