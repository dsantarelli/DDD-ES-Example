package dddes.example.domain.product.events;

import dddes.core.EntityEvent;

public class ProductBacklogItemPlanned extends EntityEvent<String> {

	private final int position;

	public ProductBacklogItemPlanned(String backlogItemId, int position) {
		super(backlogItemId);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
