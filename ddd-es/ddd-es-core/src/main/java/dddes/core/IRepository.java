package dddes.core;

public interface IRepository<T extends IAggregateRoot<ID>, ID> {
	
	T getById(ID aggregateRootId);

	void save(T aggregateRoot);

	void save(T aggregateRoot, int expectedVersion);
}