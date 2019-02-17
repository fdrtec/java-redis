package challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

	@Autowired private NeighborhoodRedisRepository neighborhoodRedisRepository;
	@Autowired private RestaurantsMongoRepository restaurantsMongoRepository;
	@Autowired private MongoTemplate mongoTemplate;


	@Override
	public NeighborhoodRedis findInNeighborhood(double x, double y) {

		NeighborhoodMongo neighborhoodMongo = getNeighborhoodByUserLocation(x, y);

		Optional<NeighborhoodRedis> neighborhoodRedisAlreadyExists =
				neighborhoodRedisRepository.findById(neighborhoodMongo.getId());

		if (neighborhoodRedisAlreadyExists.isPresent())
			return neighborhoodRedisAlreadyExists.get();

		List<RestaurantMongo> restaurantsMongo = restaurantsMongoRepository
				.findAllByLocationWithin(neighborhoodMongo.getGeometry());

		NeighborhoodRedis neighborhoodRedis = getNeighborhoodRedis(neighborhoodMongo, restaurantsMongo);
		neighborhoodRedisRepository.save(neighborhoodRedis);

		return neighborhoodRedis;
	}

	private NeighborhoodRedis getNeighborhoodRedis(NeighborhoodMongo neighborhoodMongo, List<RestaurantMongo> restaurantsMongo) {
		List<RestaurantRedis> restaurantRedis = getRestaurantRedis(restaurantsMongo);

		NeighborhoodRedis neighborhoodRedis = new NeighborhoodRedis();
		neighborhoodRedis.setId(neighborhoodMongo.getId());
		neighborhoodRedis.setName(neighborhoodMongo.getName());
		neighborhoodRedis.setRestaurants(restaurantRedis);

		return neighborhoodRedis;
	}

	private List<RestaurantRedis> getRestaurantRedis(List<RestaurantMongo> restaurantsMongo) {
		List<RestaurantRedis> restaurants = new ArrayList<>();

		for(RestaurantMongo restaurantMongo: restaurantsMongo) {
			RestaurantRedis restaurantRedis = new RestaurantRedis();
			restaurantRedis.setId(restaurantMongo.getId());
			restaurantRedis.setName(restaurantMongo.getName());
			restaurantRedis.setX(restaurantMongo.getLocation().getX());
			restaurantRedis.setY(restaurantMongo.getLocation().getY());

			restaurants.add(restaurantRedis);
		}
		return restaurants;
	}

	private NeighborhoodMongo getNeighborhoodByUserLocation(double x, double y) {
		GeoJsonPoint userLocation = new GeoJsonPoint(x, y);
		return mongoTemplate.findOne(new Query(Criteria.where("geometry").intersects(userLocation)), NeighborhoodMongo.class);
	}
}
