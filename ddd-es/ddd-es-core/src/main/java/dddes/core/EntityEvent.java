package dddes.core;

public abstract class EntityEvent<ID> extends AggregateRootEvent<ID> {

	private final ID entityId;

	protected EntityEvent(ID entityId) {
		this.entityId = entityId;
	}

	public ID getEntityId() {
		return entityId;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [id=" + entityId + ", " + "aggregateId=" + getAggregateRootId() + ", aggregateVersion=" + getAggregateRootVersion() + "]";
	}
}
