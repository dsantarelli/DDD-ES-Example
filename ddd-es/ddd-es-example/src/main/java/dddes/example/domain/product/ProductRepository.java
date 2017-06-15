package dddes.example.domain.product;

import dddes.core.IEventStore;
import dddes.example.infrastructure.Repository;

public class ProductRepository extends Repository<Product, String> {
	public ProductRepository(IEventStore<String> eventStore) {
		super(() -> new Product(), eventStore);
	}
}
