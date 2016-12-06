package com.algonquincollege.hurdleg.planets.retrofit;

import com.algonquincollege.hurdleg.planets.model.Planet;

import java.util.ArrayList;
import java.util.List;

/**
 * Collect the response from /planets
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */

public class PlanetsResponse {

    private List<Planet> planets;

    public PlanetsResponse() { super(); planets = new ArrayList<>(); }

    public List<Planet> getPlanets() { return planets; }
}
