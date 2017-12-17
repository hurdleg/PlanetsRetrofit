package com.algonquincollege.hurdleg.planets.model;

/**
 * Model a planet of our solar system.
 *
 * A PlanetPOJO has the following properties:
 *   planetId
 *   name
 *   overview
 *   image
 *   description
 *   distance from the Sun
 *   number of moons
 *
 * Compare this Java class to JSON object: https://planets.mybluemix.net/planets/2
 *
 * Pro-Tip: I used Android Studio IDE to generate the getters & setters. For details:
 *          Canvas > Modules > Resources > How to Generate Getters and Setters
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class PlanetPOJO {
    private int planetId;
    private String name;
    private String overview;
    private String image;
    private String description;
    private double distance_from_sun;
    private int number_of_moons;

    public int getPlanetId() { return planetId; }
    public void setPlanetId(int planetId) { this.planetId = planetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDistance_from_sun() { return distance_from_sun; }
    public void setDistance_from_sun(double distance_from_sun) { this.distance_from_sun = distance_from_sun; }

    public int getNumber_of_moons() { return number_of_moons;}
    public void setNumber_of_moons(int number_of_moons) { this.number_of_moons = number_of_moons; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanetPOJO that = (PlanetPOJO) o;

        return planetId == that.planetId;

    }

    @Override
    public int hashCode() {
        return planetId;
    }
}
