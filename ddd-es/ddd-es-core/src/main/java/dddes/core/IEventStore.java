package dddes.core;

import java.util.stream.Stream;

public interface IEventStore<ID> {

	void appendEventsToStream(ID streamId, Stream<Event> events);
	void appendEventsToStream(ID streamId, Stream<Event> events, long expectedLastPosition);

	Stream<Event> getStream(ID streamId);
}