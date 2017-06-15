package dddes.core;

public interface IEventStore<ID> {

	void saveEvents(ID aggregateId, Iterable<Event> events);

	void saveEvents(ID aggregateId, Iterable<Event> events, int expectedVersion);

	Iterable<Event> getEventsForAggregate(ID aggregateId);
}