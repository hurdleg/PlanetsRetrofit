package com.algonquincollege.hurdleg.planets;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.PlanetPOJO;
import com.algonquincollege.hurdleg.planets.retrofit.BasicAuthenticationInterceptor;
import com.algonquincollege.hurdleg.planets.retrofit.PlanetsAPI;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Download images from a web service, and display data in a ListActivity.
 *
 * @see {PlanetAdapter}
 * @see {res.layout.item_planet.xml}
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on Retrofit in "Connecting Android Apps to RESTful Web Services" with David Gassner
 * Reference: http://www.vogella.com/tutorials/Retrofit/article.html
 */
public class MainActivity extends ListActivity {

    private static       PlanetsAPI API;
    private static final Boolean    IS_LOCALHOST;

    public  static final String     BASE_URL;

    private ProgressBar   pb;

    private PlanetAdapter planetAdapter;

    static {
        IS_LOCALHOST = false;
        BASE_URL = IS_LOCALHOST ? "http://10.0.2.2:3000/" : "https://planets.mybluemix.net/";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        planetAdapter = new PlanetAdapter(this, R.layout.item_planet, new ArrayList<PlanetPOJO>() );
        getListView().setAdapter( planetAdapter );

        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlanetPOJO theSelectedPlanet = planetAdapter.getItem( position );
                Toast.makeText(MainActivity.this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // TODO #4 - replace with your username + password
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthenticationInterceptor("bond007", "password"))
                .build();

        // TODO #5 - configure retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API = retrofit.create(PlanetsAPI.class);

        // fetch the planet data, and display as list
        requestData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            if (isOnline()) {
                requestData();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_post_data) {
            if (isOnline()) {
                createPlanet();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_put_data) {
            if (isOnline()) {
                updatePlanet();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_delete_data) {
            if (isOnline()) {
                deletePlanet();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_post_data_binary) {
            if (isOnline()) {
                uploadImageFileOfPluto();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        return false;
    }

    // TODO #6 - GET /planets with Retrofit
    //       Previous code is no longer needed.
    //         - replaces AsyncTask that retrieves the data
    //         - parses the data; no longer need class parsers.PlanetJSONParser.java
    //         - handles saving the data as a strongly typed list of Planets
    private void requestData() {
        setProgressBarIndeterminateVisibility(true);

        Call<List<PlanetPOJO>> call = API.getPlanets();
        call.enqueue(new Callback<List<PlanetPOJO>>() {
            @Override
            public void onResponse(Call<List<PlanetPOJO>> call, Response<List<PlanetPOJO>> response) {
                setProgressBarIndeterminateVisibility(true);

                planetAdapter.clear();
                planetAdapter.addAll( response.body() );
            }

            @Override
            public void onFailure(Call<List<PlanetPOJO>> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #7 - POST /planets with Retrofit
    private void createPlanet() {
        PlanetPOJO planet = new PlanetPOJO();
        planet.setPlanetId( 0 );
        planet.setName( "Pluto" );
        planet.setOverview( "I miss Pluto!" );
        planet.setImage( "images/noimagefound.jpg" );
        planet.setDescription( "Pluto was stripped of planet status :(" );
        planet.setDistance_from_sun( 39.d );
        planet.setNumber_of_moons( 5 );

        Call<PlanetPOJO> call = API.createPlanet(planet);
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    planetAdapter.add(response.body());
                    Toast.makeText(MainActivity.this, "Added Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #10 - DELETE /planets/8 with Retrofit
    private void deletePlanet() {
        Call<Void> call = API.deletePlanet( 8 );
        call.enqueue( new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if ( response.isSuccessful() ) {
                    PlanetPOJO deletedPlanet = planetAdapter.getItem(8);
                    planetAdapter.remove(deletedPlanet);
                    Toast.makeText(MainActivity.this, "Deleted Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #8 - PUT /planets/8 with Retrofit
    private void updatePlanet() {
        final PlanetPOJO planet = new PlanetPOJO();
        planet.setPlanetId( 8 );
        planet.setName( "hurdleg" );
        planet.setOverview( "hurdleg" );
        planet.setImage( "images/pluto.jpeg" );
        planet.setDescription( "hurdleg" );
        planet.setDistance_from_sun( 39.5d );
        planet.setNumber_of_moons( 5 );

        Call<PlanetPOJO> call = API.updatePlanet( 8, planet );
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    // to update Pluto: a) remove it from list, b) add updated version
                    planetAdapter.remove(planet);
                    planetAdapter.add(response.body());
                    Toast.makeText(MainActivity.this, "Updated Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #9 - POST /planets/8/image with Retrofit
    private void uploadImageFileOfPluto() {
        // convert drawable image to array of bytes
        // hard-coded image from res/drawable
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.i_am_smiling);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);

        // create a request with Multipart in order to upload image
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), baos.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", "image", requestBodyFile);

        // call API
        Call<PlanetPOJO> call = API.uploadImageFileOfPluto(8, body);
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    planetAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Uploaded Image File of Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}