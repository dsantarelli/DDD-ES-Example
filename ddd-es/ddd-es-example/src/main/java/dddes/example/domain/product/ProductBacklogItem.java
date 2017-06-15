package dddes.example.domain.product;

import dddes.core.IEntity;

public class ProductBacklogItem implements IEntity<String> {

	private final String id;
	private int position;

	public ProductBacklogItem(String id, int position) {
		this.id = id;
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public int getPosition() {
		return position;
	}

	protected void setPosition(int position) {
		this.position = position;
	}
}
