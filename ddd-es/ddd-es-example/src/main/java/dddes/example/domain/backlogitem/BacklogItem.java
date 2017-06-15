package dddes.example.domain.backlogitem;

import java.util.Objects;
import dddes.core.AggregateRoot;
import dddes.example.domain.backlogitem.events.*;
import dddes.example.domain.product.Product;
import dddes.example.infrastructure.Strings;

public class BacklogItem extends AggregateRoot<String> {

	private String title;
	private StoryPoints storyPoints;
	private String productId;

	BacklogItem() {}
	public BacklogItem(String id, String title, Product product) {
		this(id, title, StoryPoints.ZERO, product);
	}

	public BacklogItem(String id, String title, StoryPoints storyPoints, Product product) {
		Strings.requireNonNullOrWhitespace(id, "id");
		Strings.requireNonNullOrWhitespace(title, "title");
		Objects.requireNonNull(product, "product must be not null");
		applyChange(new BacklogItemCreated(id, title.trim(), storyPoints, product.getId()));
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

	public void changeTitle(String title) {
		Strings.requireNonNullOrWhitespace(title, "title");
		if (this.title != title.trim())
			applyChange(new BacklogItemTitleChanged(title));
	}

	public void assignStoryPoints(StoryPoints storyPoints) {
		if (this.storyPoints.getValue() != storyPoints.getValue())
			applyChange(new StoryPointsAssignedToBacklogItem(storyPoints));
	}

	@SuppressWarnings("unused")
	private void on(BacklogItemCreated e) {
		setId(e.getAggregateRootId());
		this.title = e.getTitle();
		this.storyPoints = e.getStoryPoints();
		this.productId = e.getProductId();
	}

	@SuppressWarnings("unused")
	private void on(BacklogItemTitleChanged e) {
		this.title = e.getTitle();
	}

	@SuppressWarnings("unused")
	private void on(StoryPointsAssignedToBacklogItem e) {
		this.storyPoints = e.getStoryPoints();
	}
}
