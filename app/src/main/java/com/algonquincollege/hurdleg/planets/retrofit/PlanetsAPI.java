package com.algonquincollege.hurdleg.planets.retrofit;

import com.algonquincollege.hurdleg.planets.model.Planet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Define each RESTful API here.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */

public interface PlanetsAPI {

    @GET("/planets")
    Call<List<Planet>> getPlanets();
}
