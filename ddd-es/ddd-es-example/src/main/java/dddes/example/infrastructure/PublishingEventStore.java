package dddes.example.infrastructure;

import java.util.Objects;
import java.util.stream.Stream;

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

	public void appendEventsToStream(ID streamId, Stream<Event> events) {
		appendEventsToStream(streamId, events, -1);
	}

	public void appendEventsToStream(ID streamId, Stream<Event> events, long expectedLastPosition) {	  	  
	  events.forEach(event -> {	   
	    eventStore.appendEventsToStream(streamId, Stream.of(event), expectedLastPosition);  
	    eventPublisher.publish(event);	    
	  });		
	}

	public Stream<Event> getStream(ID streamId) {
		return eventStore.getStream(streamId);
	}
}
