package dddes.example.domain.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import dddes.core.AggregateRoot;
import dddes.core.IEntity;
import dddes.example.domain.backlogitem.BacklogItem;
import dddes.example.domain.product.events.*;
import dddes.example.infrastructure.Strings;

public class Product extends AggregateRoot<String> {

	private String name;

	Product() {
	}

	public Product(String id, String name) {
		Strings.requireNonNullOrWhitespace(id, "id");
		Strings.requireNonNullOrWhitespace(name, "name");
		applyChange(new ProductCreated(id, name.trim()));
	}

	public String getName() {
		return name;
	}

	public void rename(String newName) {
		Strings.requireNonNullOrWhitespace(newName, "newName");
		if (this.name != newName.trim())
			applyChange(new ProductRenamed(newName.trim()));
	}

	public void planBacklogItem(BacklogItem backlogItem) {
		Objects.requireNonNull(backlogItem, "backlogItem can't be null");

		if (entityExists(backlogItem.getId()))
			throw new IllegalArgumentException("BacklogItem \"" + backlogItem.getTitle() + "\" is already planned");

		applyChange(new ProductBacklogItemPlanned(backlogItem.getId(), getPlannedBacklogItems().length));
	}

	public void reorderBacklogItems(List<String> backlogItemIds) {
		Objects.requireNonNull(backlogItemIds, "backlogItemIds must be not null");
		for (ProductBacklogItem backlogItem : getPlannedBacklogItems()) {
			int newPosition = backlogItemIds.indexOf(backlogItem.getId());
			if (newPosition == -1)
				throw new IllegalArgumentException("Backlog item not found: " + backlogItem.getId());
			if (newPosition != backlogItem.getPosition())
				applyChange(new ProductBacklogItemPositionChanged(backlogItem.getId(), newPosition));
		}
	}

	public void reorderBacklogItems(String... backlogItemIds) {
		Objects.requireNonNull(backlogItemIds);
		reorderBacklogItems(Arrays.asList(backlogItemIds));
	}

	public ProductBacklogItem[] getPlannedBacklogItems() {
		ArrayList<ProductBacklogItem> backlogItems = new ArrayList<>();
		for (IEntity<String> entity : findEntities(e -> e instanceof ProductBacklogItem))
			backlogItems.add((ProductBacklogItem) entity);

		backlogItems.sort(ProductBacklogItemPositionComparator);
		return backlogItems.toArray(new ProductBacklogItem[backlogItems.size()]);
	}

	protected void on(ProductCreated e) {
		setId(e.getAggregateRootId());
		this.name = e.getName();
	}

	protected void on(ProductRenamed e) {
		this.name = e.getName();
	}

	protected void on(ProductBacklogItemPlanned e) {
		addEntity(new ProductBacklogItem(e.getEntityId(), e.getPosition()), true);
	}

	protected void on(ProductBacklogItemPositionChanged e) {
		ProductBacklogItem item = findEntityById(e.getEntityId());
		item.setPosition(e.getPosition());
	}

	private final Comparator<ProductBacklogItem> ProductBacklogItemPositionComparator = new Comparator<ProductBacklogItem>() {
		@Override
		public int compare(ProductBacklogItem a, ProductBacklogItem b) {
			return Integer.compare(a.getPosition(), b.getPosition());
		}
	};
}
