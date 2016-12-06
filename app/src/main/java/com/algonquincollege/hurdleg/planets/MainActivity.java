package com.algonquincollege.hurdleg.planets;

import android.app.ListActivity;
import android.content.Context;
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

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.retrofit.PlanetsAPI;
import com.algonquincollege.hurdleg.planets.retrofit.PlanetsResponse;

import java.util.List;

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
 */
public class MainActivity extends ListActivity {

    // URLs to my RESTful API Service hosted on my Bluemix account.
    public static final String BASE_URL = "https://planets-hurdleg.mybluemix.net";
    public static final String IMAGES_BASE_URL = "https://planets-hurdleg.mybluemix.net/";

    private ProgressBar pb;

    private List<Planet> planetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Planet theSelectedPlanet = planetList.get( position );
                Toast.makeText(MainActivity.this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();
            }
        });
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
        return false;
    }

    // TODO: Retrofit
    //       Previous code is no longer needed.
    //         - replaces AsyncTask that retrieves the data
    //         - parses the data; no longer need class parsers.PlanetJSONParser.java
    //         - handles saving the data as a strongly typed list of Planets
    private void requestData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlanetsAPI api = retrofit.create(PlanetsAPI.class);

        Call<PlanetsResponse> call = api.getPlanets();
        call.enqueue(new Callback<PlanetsResponse>() {
            @Override
            public void onResponse(Call<PlanetsResponse> call, Response<PlanetsResponse> response) {
                planetList = response.body().getPlanets();
                updateDisplay();
            }

            @Override
            public void onFailure(Call<PlanetsResponse> call, Throwable t) {
                Log.e( "RETROFIT", "Retrofit Error: " + t.toString() );
                Toast.makeText(MainActivity.this, "Retrofit Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        PlanetAdapter adapter = new PlanetAdapter(this, R.layout.item_planet, planetList);
        setListAdapter(adapter);
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