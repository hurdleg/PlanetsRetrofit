package mad9132.planets.retrofit;

import java.util.List;

import mad9132.planets.model.PlanetPOJO;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Define each RESTful API here.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */

// TODO #2 - Define each RESTful API
public interface PlanetsAPI {

    @GET("/planets")
    Call<List<PlanetPOJO>> getPlanets();

    @POST("/planets")
    Call<PlanetPOJO> createPlanet(@Body PlanetPOJO newPlanet );

    @DELETE("/planets/{planetId}")
    Call<Void> deletePlanet( @Path("planetId") int planetId );

    @PUT("/planets/{planetId}")
    Call<PlanetPOJO> updatePlanet(@Path("planetId") int planetId, @Body PlanetPOJO planet );

    @Multipart
    @POST("/planets/{planetId}/image")
    Call<PlanetPOJO> uploadImageFileOfPluto(@Path("planetId") int planetId, @Part MultipartBody.Part image);
}
