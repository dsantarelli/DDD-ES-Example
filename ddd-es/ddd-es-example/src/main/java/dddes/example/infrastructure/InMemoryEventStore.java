package dddes.example.infrastructure;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import dddes.core.Event;
import dddes.core.IEventStore;

public class InMemoryEventStore<ID> implements IEventStore<ID> {

	private final HashMap<ID, ArrayList<Event>> store;

	public InMemoryEventStore() {
		this.store = new HashMap<ID, ArrayList<Event>>();
	}

	public void appendEventsToStream(ID streamId, Stream<Event> events) {
		appendEventsToStream(streamId, events, -1);
	}

	public void appendEventsToStream(ID streamId, Stream<Event> events, long expectedLastPosition) throws ConcurrentModificationException {
	  
		Objects.requireNonNull(streamId, "streamId must not be null");
		Objects.requireNonNull(events, "events must not be null");

		if (!store.containsKey(streamId))
			store.put(streamId, new ArrayList<Event>());

		ArrayList<Event> stream = store.get(streamId);		
		int currentLastPosition = stream.size();
		
		if (expectedLastPosition >= 0 && currentLastPosition != expectedLastPosition)
			throw new ConcurrentModificationException(String.format("Expected: %s - Actual: %s", expectedLastPosition, currentLastPosition));
		
		events.forEach(event -> stream.add(event));				
	}

	public Stream<Event> getStream(ID streamId) throws NoSuchElementException {
	  
		Objects.requireNonNull(streamId);

		if (!store.containsKey(streamId))
			throw new NoSuchElementException("Aggregate not found: " + streamId);

		return store.get(streamId).stream();
	}
}