package mad9132.planets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

import mad9132.planets.model.PlanetPOJO;
import mad9132.planets.retrofit.BasicAuthenticationInterceptor;
import mad9132.planets.retrofit.PlanetsAPI;
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
 * CRUD app for the Planets:
 *   Create a new planet (Pluto) on the web service
 *   Read/Retrieve - get collection (array) of planets from the web service
 *   Update planet Pluto on the web service
 *   Delete planet Pluto on the web service
 *   Upload image of Pluto to web service
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: Chapter 7. Manage RESTful APIs with Retrofit 2
 *            "Android App Development: RESTful Web Services" with David Gassner
 */
public class MainActivity extends Activity {

    private static       PlanetsAPI API;
    private static final Boolean    IS_LOCALHOST;
    public  static final String     BASE_URL;
    private static final String     TAG = "CRUD-RETROFIT";

    static {
        IS_LOCALHOST = false;
        BASE_URL = IS_LOCALHOST ? "http://10.0.2.2:3000" : "https://planets.mybluemix.net";
    }

    private PlanetAdapter mPlanetAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView + Adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.rvPlanets);
        mRecyclerView.setHasFixedSize( true );
        mPlanetAdapter = new PlanetAdapter( this );
        mRecyclerView.setAdapter( mPlanetAdapter );

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

        if (item.getItemId() == R.id.action_post_form) {
            if (isOnline()) {
                createPlanetAndUploadImage();
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
        Call<List<PlanetPOJO>> call = API.getPlanets();
        call.enqueue(new Callback<List<PlanetPOJO>>() {
            @Override
            public void onResponse(Call<List<PlanetPOJO>> call, Response<List<PlanetPOJO>> response) {
                mPlanetAdapter.setPlanets( response.body() );
                Log.i( TAG, "response to GET /planets :: " + response.body().toString() );
                Toast.makeText(MainActivity.this, "Fetched " + response.body().size() + " planets"
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<PlanetPOJO>> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #7 - POST /planets with Retrofit
    private void createPlanet() {
        if (mPlanetAdapter.getItemCount() >= 9) {
            Toast.makeText(MainActivity.this, "Pluto already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        PlanetPOJO planet = new PlanetPOJO();
        planet.setPlanetId( 0 );
        planet.setName( "Pluto" );
        planet.setOverview( "I miss Pluto!" );
        planet.setImage( "images/noimagefound.jpg" );
        planet.setDescription( "Pluto was stripped of planet status :(" );
        planet.setDistanceFromSun( 39.d );
        planet.setNumberOfMoons( 5 );

        Call<PlanetPOJO> call = API.createPlanet(planet);
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    mPlanetAdapter.addPlanet(response.body());
                    Log.i( TAG, "response to POST /planets :: " + response.body().toString() );
                    Toast.makeText(MainActivity.this, "Added Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #10 - DELETE /planets/8 with Retrofit
    private void deletePlanet() {
        if (mPlanetAdapter.getItemCount() <= 8 ) {
            Toast.makeText(MainActivity.this, "You need to create Pluto", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = API.deletePlanet( 8 );
        call.enqueue( new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if ( response.isSuccessful() ) {
                    PlanetPOJO deletedPlanet = mPlanetAdapter.getPlanetAt(8);
                    mPlanetAdapter.removePlanet(deletedPlanet);
                    Log.i( TAG, "response to DELETE /planets/8 :: " + response.message() );
                    Toast.makeText(MainActivity.this, "Deleted Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #8 - PUT /planets/8 with Retrofit
    private void updatePlanet() {
        if (mPlanetAdapter.getItemCount() <= 8 ) {
            Toast.makeText(MainActivity.this, "You need to create Pluto", Toast.LENGTH_SHORT).show();
            return;
        }

        final PlanetPOJO planet = new PlanetPOJO();
        planet.setPlanetId( 8 );
        planet.setName( "hurdleg" );
        planet.setOverview( "hurdleg" );
        planet.setImage( "images/pluto.jpeg" );
        planet.setDescription( "hurdleg" );
        planet.setDistanceFromSun( 39.5d );
        planet.setNumberOfMoons( 5 );

        Call<PlanetPOJO> call = API.updatePlanet( 8, planet );
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    // to update Pluto: a) remove it from list, b) addPlanet updated version
                    mPlanetAdapter.removePlanet(planet);
                    mPlanetAdapter.addPlanet(response.body());
                    Log.i( TAG, "response to PUT /planets/8 :: " + response.body().toString() );
                    Toast.makeText(MainActivity.this, "Updated Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #9 - POST /planets/8/image with Retrofit
    private void uploadImageFileOfPluto() {
        if (mPlanetAdapter.getItemCount() <= 8 ) {
            Toast.makeText(MainActivity.this, "You need to create Pluto", Toast.LENGTH_SHORT).show();
            return;
        }

        // convert drawable image to array of bytes
        // hard-coded image from res/drawable
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.i_am_smiling);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);

        // create a request with Multipart in order to upload image
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), baos.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "i_am_smiling.jpg", requestBodyFile);

        // call API
        Call<PlanetPOJO> call = API.uploadImageFileOfPluto(8, body);
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    mPlanetAdapter.notifyDataSetChanged();
                    Log.i( TAG, "response to POST /planets/8/image :: " + response.body().toString() );
                    Toast.makeText(MainActivity.this, "Uploaded Image File of Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO #11 - POST /planets/form with Retrofit (multipart form)
    private void createPlanetAndUploadImage() {
        if (mPlanetAdapter.getItemCount() >= 9) {
            Toast.makeText(MainActivity.this, "Pluto already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        // convert drawable image to array of bytes
        // hard-coded image from res/drawable
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.i_am_smiling);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);

        // create a request with Multipart in order to upload image
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), baos.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "i_am_smiling.jpg", requestBodyFile);

        PlanetPOJO planet = new PlanetPOJO();
        planet.setName( "pluto (form)" );
        planet.setOverview( "I miss Pluto!" );
        planet.setDescription( "Pluto was stripped of planet status :(" );
        planet.setDistanceFromSun( 39.d );
        planet.setNumberOfMoons( 5 );

        RequestBody nameRB = RequestBody.create(MediaType.parse("text/plain"), planet.getName());
        RequestBody overviewRB = RequestBody.create(MediaType.parse("text/plain"), planet.getOverview());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), planet.getDescription());
        RequestBody numberOfMoons = RequestBody.create(MediaType.parse("text/plain"), planet.getNumberOfMoons() + "");
        RequestBody distanceFromSun = RequestBody.create(MediaType.parse("text/plain"), planet.getDistanceFromSun() + "");

        Call<PlanetPOJO> call = API.createPlanetAndUploadImage(nameRB, overviewRB, description, numberOfMoons, distanceFromSun, body);
        call.enqueue( new Callback<PlanetPOJO>() {
            @Override
            public void onResponse(Call<PlanetPOJO> call, Response<PlanetPOJO> response) {
                if ( response.isSuccessful() ) {
                    mPlanetAdapter.addPlanet(response.body());
                    Log.i( TAG, "response to POST /planets :: " + response.body().toString() );
                    Toast.makeText(MainActivity.this, "Added Pluto", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanetPOJO> call, Throwable t) {
                Log.e( TAG, "Retrofit Error: " + t.getLocalizedMessage() );
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