package dddes.example.infrastructure;

import java.util.Objects;

import dddes.core.Event;
import dddes.core.IEventPublisher;
import dddes.core.IEventStore;

public class PublishingEventStore<ID> implements IEventStore<ID> {

	private final IEventStore<ID> eventStore;
	private final IEventPublisher eventPublisher;

	public PublishingEventStore(IEventStore<ID> eventStore, IEventPublisher eventPublisher) {
		Objects.requireNonNull(eventStore, "eventStore must be not null");
		Objects.requireNonNull(eventPublisher, "eventPublisher must be not null");
		this.eventStore = eventStore;
		this.eventPublisher = eventPublisher;
	}

	public void saveEvents(ID aggregateId, Iterable<Event> events) {
		saveEvents(aggregateId, events, -1);
	}

	public void saveEvents(ID aggregateId, Iterable<Event> events, int expectedVersion) {
		eventStore.saveEvents(aggregateId, events, expectedVersion);
		for (Event event : events)
			eventPublisher.publish(event);
	}

	public Iterable<Event> getEventsForAggregate(ID aggregateId) {
		return eventStore.getEventsForAggregate(aggregateId);
	}
}
