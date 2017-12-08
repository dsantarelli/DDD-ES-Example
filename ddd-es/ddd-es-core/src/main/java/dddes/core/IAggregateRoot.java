package dddes.core;

import java.util.stream.Stream;

public interface IAggregateRoot<ID> {
 
  ID getId();

  int getVersion();

  /**
   * Loads the AggregateRoot by iterating an historical sequence of events
   * 
   * @param history: a sequence of events
   */
  void loadFromHistory(Stream<Event> history);

  /** @return the current pending changes of the AggregateRoot */
  Stream<Event> getPendingChanges();

  /** Marks the current pending changes as committed (e.g. saved in a event store) */
  void markPendingChangesAsCommitted();
}
