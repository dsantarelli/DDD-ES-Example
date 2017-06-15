package dddes.example.domain.product;

import java.util.UUID;

public class ProductFactory {

	public static Product create(String name) {
		return create(UUID.randomUUID().toString(), name);
	}

	public static Product create(String id, String name) {
		return new Product(id, name);
	}
}
