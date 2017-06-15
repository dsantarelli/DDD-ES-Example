package dddes.core;

/** A base class for a domain event of an AggregateRoot */
public abstract class AggregateRootEvent<ID> implements Event {

	private ID aggregateRootId;
	private int aggregateRootVersion;

	protected AggregateRootEvent() { }
	protected AggregateRootEvent(ID aggregateRootId) {
		this(aggregateRootId, 0);
	}

	private AggregateRootEvent(ID aggregateRootId, int aggregateRootVersion) {
		this.aggregateRootId = aggregateRootId;
		this.aggregateRootVersion = aggregateRootVersion;
	}

	public ID getAggregateRootId() {
		return aggregateRootId;
	}

	public int getAggregateRootVersion() {
		return aggregateRootVersion;
	}

	public void setAggregateRootId(ID aggregateRootId) {
		this.aggregateRootId = aggregateRootId;
	}

	protected void setAggregateRootVersion(int aggregateRootVersion) {
		this.aggregateRootVersion = aggregateRootVersion;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [id=" + aggregateRootId + ", version=" + aggregateRootVersion + "]";
	}
}
