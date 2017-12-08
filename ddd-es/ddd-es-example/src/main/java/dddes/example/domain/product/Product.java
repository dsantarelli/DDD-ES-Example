package dddes.example.domain.product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import dddes.core.AggregateRoot;
import dddes.example.domain.backlogitem.BacklogItem;
import dddes.example.domain.product.events.ProductBacklogItemPlanned;
import dddes.example.domain.product.events.ProductBacklogItemPositionChanged;
import dddes.example.domain.product.events.ProductCreated;
import dddes.example.domain.product.events.ProductRenamed;
import dddes.example.infrastructure.Strings;

public final class Product extends AggregateRoot<String> {

  private String name;

  Product() {}

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
    if (!this.name.equals(newName))
      applyChange(new ProductRenamed(newName));
  }

  public void planBacklogItem(BacklogItem backlogItem) {
    Objects.requireNonNull(backlogItem, "backlogItem can't be null");
    if (entityExists(backlogItem.getId())) 
      throw new IllegalArgumentException("BacklogItem \"" + backlogItem.getTitle() + "\" is already planned");
    
    applyChange(new ProductBacklogItemPlanned(backlogItem.getId(), (int) getPlannedBacklogItems().count()));
  }

  public void reorderBacklogItems(String... backlogItemIds) {
    Objects.requireNonNull(backlogItemIds);
    reorderBacklogItems(Arrays.asList(backlogItemIds));
  }

  public void reorderBacklogItems(List<String> backlogItemIds) {
    Objects.requireNonNull(backlogItemIds, "backlogItemIds must be not null");
    getPlannedBacklogItems().forEach(backlogItem -> {
      int newPosition = backlogItemIds.indexOf(backlogItem.getId());
      if (newPosition == -1) throw new IllegalArgumentException("Backlog item not found: " + backlogItem.getId());
     
      if (newPosition != backlogItem.getPosition())
        applyChange(new ProductBacklogItemPositionChanged(backlogItem.getId(), newPosition));
    });
  }

  public Stream<ProductBacklogItem> getPlannedBacklogItems() {
    return getEntities()
        .filter(x -> x instanceof ProductBacklogItem)
        .map(x -> (ProductBacklogItem)x)
        .sorted(productBacklogItemPositionComparator);
  }

  // Protected methods called via reflection by infrastructure
  
  protected void on(ProductCreated e) {
    setId(e.getAggregateRootId());
    name = e.getName();
  }

  protected void on(ProductRenamed e) {
    name = e.getName();
  }

  protected void on(ProductBacklogItemPlanned e) {
    addEntity(new ProductBacklogItem(e.getEntityId(), e.getPosition()), true);
  }

  protected void on(ProductBacklogItemPositionChanged e) {
    ProductBacklogItem item = findEntityById(e.getEntityId());
    item.setPosition(e.getPosition());
  }

  private final Comparator<ProductBacklogItem> productBacklogItemPositionComparator = new Comparator<ProductBacklogItem>() {
    @Override
    public int compare(ProductBacklogItem a, ProductBacklogItem b) {
      return Integer.compare(a.getPosition(), b.getPosition());
    }
  };
}
