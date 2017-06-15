package dddes.example.domain.product;

import static org.assertj.core.api.Assertions.*;
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
	public void productShouldNotBeCreatedWithNullOrEmptyName() {
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(null));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(""));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(" "));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("   "));
	}

	@Test
	@Category(ProductTests.class)
	public void productShouldNotBeCreatedWithNullOrEmptyId() {
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(null, "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("", "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create(" ", "Name"));
		assertThatIllegalArgumentException().isThrownBy(() -> ProductFactory.create("   ", "Name"));
	}

	@Test
	@Category(ProductTests.class)
	public void productShouldNotBeRenamedWithNullOrEmptyName() {
		Product product = ProductFactory.create("Product name");
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(null));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(""));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename(" "));
		assertThatIllegalArgumentException().isThrownBy(() -> product.rename("  "));
	}

	@Test
	@Category(ProductTests.class)
	public void productShouldHaveAtLeastAnIdAndAName() {

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
	public void productShouldBeRenamed() {

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
	public void productShouldPlanBacklogItem() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem = BacklogItemFactory.create("Business model canvas", product);

		// @formatter:off
		new AggregateRootAssert<Product>(product).markPendingChangesAsCommitted()
			.call(x -> x.planBacklogItem(backlogItem))
			.thereIsExactlyOnePendingChangeOfType(ProductBacklogItemPlanned.class)
			.isTrueThat(x -> x.getPlannedBacklogItems().length == 1)
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getId() == backlogItem.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getPosition() == 0);
		// @formatter:on
	}

	@Test
	@Category(ProductTests.class)
	public void productCantPlanTheSameBacklogItem() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem = BacklogItemFactory.create("Business model canvas", product);
		product.planBacklogItem(backlogItem);
		assertThatIllegalArgumentException().isThrownBy(() -> product.planBacklogItem(backlogItem));
	}

	@Test
	@Category(ProductTests.class)
	public void productShouldReorderBacklogItems() {

		Product product = ProductFactory.create("My product");
		BacklogItem backlogItem1 = BacklogItemFactory.create("BacklogItem 1", product);
		BacklogItem backlogItem2 = BacklogItemFactory.create("BacklogItem 2", product);
		BacklogItem backlogItem3 = BacklogItemFactory.create("BacklogItem 3", product);

		// @formatter:off
		new AggregateRootAssert<Product>(product)
			.markPendingChangesAsCommitted()
			.call(x -> x.planBacklogItem(backlogItem1)).call(x -> x.planBacklogItem(backlogItem2))
			.call(x -> x.planBacklogItem(backlogItem3)).markPendingChangesAsCommitted()
	
			.isTrueThat(x -> x.getPlannedBacklogItems().length == 3)
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getId() == backlogItem1.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getPosition() == 0)
			.isTrueThat(x -> x.getPlannedBacklogItems()[1].getId() == backlogItem2.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[1].getPosition() == 1)
			.isTrueThat(x -> x.getPlannedBacklogItems()[2].getId() == backlogItem3.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[2].getPosition() == 2)
	
			.call(x -> x.reorderBacklogItems(backlogItem3.getId(), backlogItem2.getId(), backlogItem1.getId()))
			.allPendingChangesAreOfType(ProductBacklogItemPositionChanged.class).markPendingChangesAsCommitted()
	
			.isTrueThat(x -> x.getPlannedBacklogItems().length == 3)
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getId() == backlogItem3.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[0].getPosition() == 0)
			.isTrueThat(x -> x.getPlannedBacklogItems()[1].getId() == backlogItem2.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[1].getPosition() == 1)
			.isTrueThat(x -> x.getPlannedBacklogItems()[2].getId() == backlogItem1.getId())
			.isTrueThat(x -> x.getPlannedBacklogItems()[2].getPosition() == 2)
	
			.call(x -> x.reorderBacklogItems(backlogItem3.getId(), backlogItem2.getId(), backlogItem1.getId()))
			.thereAreNoPendingChanges();
		// @formatter:on
	}
}
