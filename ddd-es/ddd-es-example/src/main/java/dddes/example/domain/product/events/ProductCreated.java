package dddes.example.domain.product.events;

import dddes.core.AggregateRootEvent;

public class ProductCreated extends AggregateRootEvent<String> {

	private final String name;

	public ProductCreated(String id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
