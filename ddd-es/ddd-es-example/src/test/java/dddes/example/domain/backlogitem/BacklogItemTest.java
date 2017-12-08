package dddes.example.domain.backlogitem;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import dddes.example.domain.AggregateRootAssert;
import dddes.example.domain.backlogitem.BacklogItem;
import dddes.example.domain.backlogitem.BacklogItemFactory;
import dddes.example.domain.product.Product;
import dddes.example.domain.product.ProductFactory;
import dddes.example.domain.backlogitem.events.*;

public class BacklogItemTest {

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_not_be_created_with_null_or_empty_name() {
	  
		Product product = ProductFactory.create("My product");
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create(null, null, product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create("", null, product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create(" ", null, product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create("   ", null, product));
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_not_be_created_with_null_or_empty_id() {
	  
		Product product = ProductFactory.create("My product");
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create(null, "Name", product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create("", "Name", product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create(" ", "Name", product));
		assertThatIllegalArgumentException().isThrownBy(() -> BacklogItemFactory.create("   ", "Name", product));
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_not_be_created_without_a_product() {
	  
		assertThatNullPointerException().isThrownBy(() -> BacklogItemFactory.create("ID", "Name", null));
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_not_be_renamed_with_null_or_empty_name() {
	  
		Product product = ProductFactory.create("Product name");
		BacklogItem backlogItem = BacklogItemFactory.create("BacklogItem", product);
		assertThatIllegalArgumentException().isThrownBy(() -> backlogItem.changeTitle(null));
		assertThatIllegalArgumentException().isThrownBy(() -> backlogItem.changeTitle(""));
		assertThatIllegalArgumentException().isThrownBy(() -> backlogItem.changeTitle(" "));
		assertThatIllegalArgumentException().isThrownBy(() -> backlogItem.changeTitle("   "));
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_have_at_least_an_id_and_a_name() {

		Product product = ProductFactory.create("My product");
		String id = UUID.randomUUID().toString();
		String title = "BacklogItem";

		// @formatter:off
		new AggregateRootAssert<BacklogItem>(BacklogItemFactory.create(id, title, product))
			.idIs(id)
			.versionIs(1)
			.thereIsExactlyOnePendingChangeOfType(BacklogItemCreated.class)
			.isTrueThat(item -> item.getTitle() == title).isTrueThat(item -> item.getProductId() == product.getId())
			.isTrueThat(item -> item.getStoryPoints() == StoryPoints.ZERO);
		// @formatter:on
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_be_renamed() {

		Product product = ProductFactory.create("My product");
		String oldName = "Old product name";
		String newName = "New product name";

		// @formatter:off
		new AggregateRootAssert<BacklogItem>(BacklogItemFactory.create(oldName, product))
			.markPendingChangesAsCommitted().call(x -> x.changeTitle(newName))
			.thereIsExactlyOnePendingChangeOfType(BacklogItemTitleChanged.class)
			.versionIs(2)
			.isTrueThat(x -> x.getTitle() == newName)
			.markPendingChangesAsCommitted()
			.thereAreNoPendingChanges()
			.call(x -> x.changeTitle(newName))
			.thereAreNoPendingChanges();
		// @formatter:on
	}

	@Test
	@Category(BacklogItemTests.class)
	public void backlogItem_should_have_storyPoints() {

		Product product = ProductFactory.create("My product");
		StoryPoints storyPoints = StoryPoints.FIVE;

		// @formatter:off
		new AggregateRootAssert<BacklogItem>(BacklogItemFactory.create("BacklogItem", product))
			.markPendingChangesAsCommitted()
			.call(x -> x.assignStoryPoints(storyPoints))
			.thereIsExactlyOnePendingChangeOfType(StoryPointsAssignedToBacklogItem.class)
			.markPendingChangesAsCommitted()
			.versionIs(2)
			.isTrueThat(x -> x.getStoryPoints() == storyPoints)					
			.call(x -> x.assignStoryPoints(storyPoints))
			.thereAreNoPendingChanges();
		// @formatter:on
	}
}
