package dddes.example.domain.product.events;

import dddes.core.EntityEvent;

public class ProductBacklogItemPositionChanged extends EntityEvent<String> {

	private final int position;

	public ProductBacklogItemPositionChanged(String id, int position) {
		super(id);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
