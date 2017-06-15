package dddes.example.domain.product.events;

import dddes.core.AggregateRootEvent;

public class ProductRenamed extends AggregateRootEvent<String> {

	private final String name;

	public ProductRenamed(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}