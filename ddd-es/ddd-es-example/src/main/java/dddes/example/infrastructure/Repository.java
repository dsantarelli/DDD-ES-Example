package dddes.example.infrastructure;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import dddes.core.Event;
import dddes.core.IAggregateRoot;
import dddes.core.IRepository;
import dddes.core.IEventStore;

public class Repository<T extends IAggregateRoot<ID>, ID> implements IRepository<T, ID> {

	private final IEventStore<ID> eventStore;
	private final Supplier<T> defaultAggregateRootFactory;

	public Repository(Supplier<T> defaultAggregateRootFactory, IEventStore<ID> eventStore) {
		Objects.requireNonNull(defaultAggregateRootFactory, "defaultAggregateRootFactory must be not null");
		Objects.requireNonNull(eventStore, "eventStore must be not null");
		this.defaultAggregateRootFactory = defaultAggregateRootFactory;
		this.eventStore = eventStore;
	}

	public void save(T aggregateRoot) {
		save(aggregateRoot, -1);
	}

	public void save(T aggregateRoot, int expectedVersion) {
		Objects.requireNonNull(aggregateRoot, "aggregateRoot must be not null");		
		eventStore.saveEvents(aggregateRoot.getId(), aggregateRoot.getPendingChanges(), expectedVersion);
		aggregateRoot.markPendingChangesAsCommitted();
	}

	public T getById(ID aggregateRootId) throws NoSuchElementException {
		Objects.requireNonNull(aggregateRootId, "aggregateRootId must be not null");
		Iterable<Event> events = eventStore.getEventsForAggregate(aggregateRootId);
		if (!events.iterator().hasNext())
			throw new NoSuchElementException("aggregateRoot not found: " + aggregateRootId.toString());
		
		T aggregateRoot = defaultAggregateRootFactory.get();		
		aggregateRoot.loadFromHistory(events);
		return aggregateRoot;
	}
}
