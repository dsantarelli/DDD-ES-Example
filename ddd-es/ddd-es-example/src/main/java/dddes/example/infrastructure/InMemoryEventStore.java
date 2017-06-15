package dddes.example.infrastructure;

import java.util.Objects;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import dddes.core.Event;
import dddes.core.IEventStore;

public class InMemoryEventStore<ID> implements IEventStore<ID> {

	private final HashMap<ID, ArrayList<Event>> _store;

	public InMemoryEventStore() {
		_store = new HashMap<ID, ArrayList<Event>>();
	}

	public void saveEvents(ID aggregateId, Iterable<Event> events) {
		saveEvents(aggregateId, events, -1);
	}

	public void saveEvents(ID aggregateId, Iterable<Event> events, int expectedVersion) throws ConcurrentModificationException {

		Objects.requireNonNull(aggregateId, "aggregateId must not be null");
		Objects.requireNonNull(events, "events must not be null");

		if (!_store.containsKey(aggregateId))
			_store.put(aggregateId, new ArrayList<Event>());

		ArrayList<Event> storedEvents = _store.get(aggregateId);
		int currentVersion = storedEvents.size();

		if (expectedVersion >= 0 && currentVersion != expectedVersion)
			throw new ConcurrentModificationException(
					String.format("Expected: %s - Actual: %s", expectedVersion, currentVersion));

		for (Event event : events)
			storedEvents.add(event);
	}

	public Iterable<Event> getEventsForAggregate(ID aggregateId) throws NoSuchElementException {

		Objects.requireNonNull(aggregateId);

		if (!_store.containsKey(aggregateId))
			throw new NoSuchElementException("Aggregate not found: " + aggregateId);

		return new ArrayList<Event>(_store.get(aggregateId));
	}
}
