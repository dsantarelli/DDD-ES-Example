package dddes.example.domain.backlogitem;

import dddes.core.IEventStore;
import dddes.example.infrastructure.Repository;

public class BacklogItemRepository extends Repository<BacklogItem, String> {
	public BacklogItemRepository(IEventStore<String> eventStore) {
		super(() -> new BacklogItem(), eventStore);
	}
}