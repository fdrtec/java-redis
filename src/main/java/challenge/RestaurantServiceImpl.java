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

		//Encontrar bairro no mongo
		GeoJsonPoint userLocation = new GeoJsonPoint(x, y);
		NeighborhoodMongo neighborhoodMongo =
				mongoTemplate.findOne(new Query(Criteria.where("geometry").intersects(userLocation)), NeighborhoodMongo.class);

		//se etiver salvo no redis neighborhood:id_bairro
		Optional<NeighborhoodRedis> neighborhoodRedisAlreadyExists =
				neighborhoodRedisRepository.findById(neighborhoodMongo.getId());

		//retornar esse objeto
		if (neighborhoodRedisAlreadyExists.isPresent())
			return neighborhoodRedisAlreadyExists.get();

		//senao, encontrar todos os restaurantes do bairro
		List<RestaurantMongo> restaurantsMongo = restaurantsMongoRepository
				.findAllByLocationWithin(neighborhoodMongo.getGeometry());

		List<RestaurantRedis> restaurantRedis = new ArrayList<>();
		for(RestaurantMongo restaurantMongo: restaurantsMongo) {

			RestaurantRedis restaurantRedis1 = new RestaurantRedis();
			restaurantRedis1.setId(restaurantMongo.getId());
			restaurantRedis1.setName(restaurantMongo.getName());
			restaurantRedis1.setX(restaurantMongo.getLocation().getX());
			restaurantRedis1.setY(restaurantMongo.getLocation().getY());

			restaurantRedis.add(restaurantRedis1);
		}

		NeighborhoodRedis novo = new NeighborhoodRedis();
		novo.setId(neighborhoodMongo.getId());
		novo.setName(neighborhoodMongo.getName());
		novo.setRestaurants(restaurantRedis);

		neighborhoodRedisRepository.save(novo);

		return novo;
	}
}
