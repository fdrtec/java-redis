package challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

	@Autowired
	private RestaurantService service;

	@GetMapping("/findInNeighborhood")
	public NeighborhoodRedis findInNeighborhood(@RequestParam("x") Double x, @RequestParam("y") Double y) {
		return service.findInNeighborhood(x, y);
	}

}
