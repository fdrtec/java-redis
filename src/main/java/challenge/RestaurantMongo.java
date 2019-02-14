package challenge;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Classe para mapear o restaurante no MongoDB
 *
 */

@Document(collection = "restaurant")
public class RestaurantMongo {

    @Id
    private String id;
    private GeoJsonPoint location;
    private String name;

    public RestaurantMongo(String id, GeoJsonPoint location, String name) {
        this.id = id;
        this.location = location;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestaurantMongo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }
}
