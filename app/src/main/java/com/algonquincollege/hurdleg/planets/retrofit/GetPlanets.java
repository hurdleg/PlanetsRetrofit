package com.algonquincollege.hurdleg.planets.retrofit;

import com.algonquincollege.hurdleg.planets.model.Planet;

import java.util.ArrayList;
import java.util.List;

/**
 * Collect the response from /planets
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */

public class GetPlanets {

    public List<Planet> planets;

    public GetPlanets() { super(); planets = new ArrayList<>(); }

    public List<Planet> getPlanets() { return planets; }
}
