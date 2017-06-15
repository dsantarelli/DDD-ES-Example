package dddes.core;

public interface IAggregateRoot<ID> {
	
	/** @return the AggregateRoot Id */
	ID getId();

	/** @return the AggregateRoot Version */
	int getVersion();

	/**
	 * Loads the AggregateRoot by iterating an historical sequence of events
	 * 
	 * @param history:
	 *            a sequence of events
	 */
	void loadFromHistory(Iterable<Event> history);

	/** @return the current pending changes of the AggregateRoot */
	Iterable<Event> getPendingChanges();

	/**
	 * Marks the current pending changes as committed (e.g. saved in a event store)
	 */
	void markPendingChangesAsCommitted();
}
