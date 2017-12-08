package dddes.example.domain.product;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import dddes.example.domain.AggregateRootAssert;
import dddes.example.domain.backlogitem.BacklogItem;
import dddes.example.domain.backlogitem.BacklogItemFactory;
import dddes.example.domain.product.events.ProductBacklogItemPlanned;
import dddes.example.domain.product.events.ProductBacklogItemPositionChanged;
import dddes.example.domain.product.events.ProductCreated;
import dddes.example.domain.product.events.ProductRenamed;

public class ProductTest {

	@Test
	@Category(ProductTests.class)
	public void product_should_not_be_created_with_null_or_empty_name() {
	  
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(null));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(""));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(" "));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("   "));
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_not_be_created_with_null_or_empty_id() {
	  
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(null, "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("", "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(" ", "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("   ", "Name"));
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_not_be_renamed_with_null_or_empty_name() {
	  
		Product product = ProductFactory.create("Product name");
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(null));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(""));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(" "));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename("  "));
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_have_at_least_an_id_and_a_name() {

		String id = UUID.randomUUID().toString();
		String name = "Product name";

		// @formatter:off
		new AggregateRootAssert<Product>(ProductFactory.create(id, name))
			.idIs(id)
			.versionIs(1)
			.thereIsExactlyOnePendingChangeOfType(ProductCreated.class)
			.isTrueThat(product -> product.getName() == name);
		// @formatter:on
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_be_renamed() {

		String oldName = "Old product name";
		String newName = "New product name";

		// @formatter:off
		new AggregateRootAssert<Product>(ProductFactory.create(oldName))
		  .markPendingChangesAsCommitted()
		  .call(x -> x.rename(newName))
		  .thereIsExactlyOnePendingChangeOfType(ProductRenamed.class)
		  .versionIs(2)
		  .isTrueThat(x -> x.getName() == newName)
		  .markPendingChangesAsCommitted()
		  .thereAreNoPendingChanges()
		  .call(x -> x.rename(newName))
		  .thereAreNoPendingChanges();
		// @formatter:on
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_plan_backlog_item() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem = BacklogItemFactory.create("Business model canvas", product);

		// @formatter:off
		new AggregateRootAssert<Product>(product)
		  .markPendingChangesAsCommitted()
			.call(x -> x.planBacklogItem(backlogItem))
			.thereIsExactlyOnePendingChangeOfType(ProductBacklogItemPlanned.class)
			.isTrueThat(x -> x.getPlannedBacklogItems().count() == 1)
			.isTrueThat(x -> x.getPlannedBacklogItems().findFirst().get().getId() == backlogItem.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems().findFirst().get().getPosition() == 0);
		// @formatter:on
	}

	@Test
	@Category(ProductTests.class)
	public void product_cant_plan_the_same_backlog_item() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem = BacklogItemFactory.create("Business model canvas", product);
		product.planBacklogItem(backlogItem);
		assertThatIllegalArgumentException().isThrownBy(() -> product.planBacklogItem(backlogItem));
	}

	@Test
	@Category(ProductTests.class)
	public void product_should_reorder_backlog_items() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem1 = BacklogItemFactory.create("BacklogItem 1", product);
		BacklogItem backlogItem2 = BacklogItemFactory.create("BacklogItem 2", product);
		BacklogItem backlogItem3 = BacklogItemFactory.create("BacklogItem 3", product);
			
		// @formatter:off
		new AggregateRootAssert<Product>(product)
			.markPendingChangesAsCommitted()
			.call(x -> x.planBacklogItem(backlogItem1))
			.call(x -> x.planBacklogItem(backlogItem2))
			.call(x -> x.planBacklogItem(backlogItem3))			
			.isTrueThat(x -> x.getPlannedBacklogItems().count() == 3)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 0).getId() == backlogItem1.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 0).getPosition() == 0)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 1).getId() == backlogItem2.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 1).getPosition() == 1)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 2).getId() == backlogItem3.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 2).getPosition() == 2)
			.markPendingChangesAsCommitted()
	
			.call(x -> x.reorderBacklogItems(backlogItem3.getId(), backlogItem2.getId(), backlogItem1.getId()))
			.allPendingChangesAreOfType(ProductBacklogItemPositionChanged.class)
			.markPendingChangesAsCommitted()
	
			.isTrueThat(x -> x.getPlannedBacklogItems().count() == 3)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 0).getId() == backlogItem3.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 0).getPosition() == 0)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 1).getId() == backlogItem2.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 1).getPosition() == 1)
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 2).getId() == backlogItem1.getId())
			.isTrueThat(x -> getProductBacklogItemAtIndex(x, 2).getPosition() == 2)
	
			.call(x -> x.reorderBacklogItems(backlogItem3.getId(), backlogItem2.getId(), backlogItem1.getId()))			
			.thereAreNoPendingChanges();
		// @formatter:on
	}
	
	private static ProductBacklogItem getProductBacklogItemAtIndex(Product product, int index) {
	  return product.getPlannedBacklogItems().skip(index).limit(1).findFirst().orElse(null);
	}
}
