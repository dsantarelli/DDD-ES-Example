package dddes.example.infrastructure;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import dddes.core.IEventStore;
import dddes.example.domain.backlogitem.BacklogItem;
import dddes.example.domain.backlogitem.BacklogItemFactory;
import dddes.example.domain.backlogitem.BacklogItemRepository;
import dddes.example.domain.backlogitem.StoryPoints;
import dddes.example.domain.product.Product;
import dddes.example.domain.product.ProductFactory;
import dddes.example.domain.product.ProductRepository;

public class RepositoryTest {

	private static IEventStore<String> eventStore;
	private static ProductRepository productRepository;
	private static BacklogItemRepository backlogItemRepository;

	@BeforeClass
	public static void setUp() throws Exception {
	  
		eventStore = new PublishingEventStore<String>(new InMemoryEventStore<String>(), new ConsoleEventPublisher());
		productRepository = new ProductRepository(eventStore);
		backlogItemRepository = new BacklogItemRepository(eventStore);
	}

	@Test
	@Category(InfrastructureTests.class)
	public void repositories_cant_load_not_existing_aggregates() {
	  
		assertThatThrownBy(() -> productRepository.getById(null));
		assertThatThrownBy(() -> productRepository.getById(UUID.randomUUID().toString()));
		assertThatThrownBy(() -> backlogItemRepository.getById(null));
		assertThatThrownBy(() -> backlogItemRepository.getById(UUID.randomUUID().toString()));
	}

	@Test
	@Category(InfrastructureTests.class)
	public void repositories_should_save_aggregates() {
	  
		Product product = ProductFactory.create("Product");
		assertThat(product.getPendingChanges()).size().isGreaterThan(0);
		productRepository.save(product);
		assertThat(product.getPendingChanges()).size().isZero();
	}

	@Test
	@Category(InfrastructureTests.class)
	public void repositories_should_save_aggregates_with_expected_version() {
	  
		Product product1 = ProductFactory.create("Product");
		product1.rename("Product 1");
		productRepository.save(product1);
		Product product2 = productRepository.getById(product1.getId());
		int expectedVersion = product2.getVersion();
		product2.rename("Product 2");
		productRepository.save(product2, expectedVersion);
	}

	@Test
	@Category(InfrastructureTests.class)
	public void repositories_should_not_save_aggregates_with_unexpected_version() {
	  
		Product product1 = ProductFactory.create("Product");		
		product1.rename("Product 1");
		productRepository.save(product1);
		Product product2 = productRepository.getById(product1.getId());
		int expectedVersion = product2.getVersion();
		product2.rename("Product 2");		
		assertThatThrownBy(() -> productRepository.save(product2, expectedVersion + 1));
		assertThatThrownBy(() -> productRepository.save(product2, expectedVersion - 1));
	}

	@Test
	@Category(InfrastructureTests.class)
	public void repositories_should_load_existing_aggregates() {
	  
		Product product1 = ProductFactory.create("Product");
		productRepository.save(product1);
		Product product2 = productRepository.getById(product1.getId());
		assertThat(product2).isNotNull().isEqualTo(product1);
	}
	
	@Test
	@Category(InfrastructureTests.class)
	public void different_repositories_should_save_and_load_different_aggregates() {
	  
		Product product1 = ProductFactory.create("Product");
		BacklogItem backlogItem1 = BacklogItemFactory.create("BacklogItem 1", product1);
		BacklogItem backlogItem2 = BacklogItemFactory.create("BacklogItem 2", product1);
		backlogItem1.assignStoryPoints(StoryPoints.FIVE);
		backlogItem2.assignStoryPoints(StoryPoints.EIGHT);
		product1.planBacklogItem(backlogItem1);
		product1.planBacklogItem(backlogItem2);
		product1.reorderBacklogItems(backlogItem2.getId(), backlogItem1.getId());
		
		// Save aggregates
		productRepository.save(product1);
		backlogItemRepository.save(backlogItem1);
		backlogItemRepository.save(backlogItem2);
		
		// Assertions
		Product product2 = productRepository.getById(product1.getId());
		assertThat(product2).isEqualTo(product1);
		assertThat(product2.getName()).isEqualTo(product1.getName());
		assertThat(product2.getPlannedBacklogItems().count()).isEqualTo(product1.getPlannedBacklogItems().count());
		
		BacklogItem backlogItem3 = backlogItemRepository.getById(backlogItem1.getId());
		assertThat(backlogItem3).isEqualTo(backlogItem1);
		assertThat(backlogItem3.getTitle()).isEqualTo(backlogItem1.getTitle());
		assertThat(backlogItem3.getStoryPoints()).isEqualTo(backlogItem1.getStoryPoints());
		assertThat(backlogItem3.getProductId()).isEqualTo(backlogItem1.getProductId());
	}
}
