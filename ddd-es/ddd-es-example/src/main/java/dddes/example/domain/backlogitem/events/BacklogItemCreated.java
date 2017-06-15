package dddes.example.domain.backlogitem.events;

import dddes.core.AggregateRootEvent;
import dddes.example.domain.backlogitem.StoryPoints;

public class BacklogItemCreated extends AggregateRootEvent<String> {

	private final String title;
	private final String productId;
	private final StoryPoints storyPoints;

	public BacklogItemCreated(String id, String title, StoryPoints storyPoints, String productId) {
		super(id);
		this.title = title;
		this.storyPoints = storyPoints;
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public StoryPoints getStoryPoints() {
		return storyPoints;
	}

	public String getProductId() {
		return productId;
	}
}