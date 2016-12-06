package com.algonquincollege.hurdleg.planets.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Define each RESTful API here.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */

public interface PlanetsAPI {

    @GET("/planets")
    Call<GetPlanets> getPlanets();
}
