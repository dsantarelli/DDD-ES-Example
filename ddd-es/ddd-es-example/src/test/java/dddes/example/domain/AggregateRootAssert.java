package dddes.example.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.assertj.core.api.AbstractAssert;
import dddes.core.AggregateRoot;
import dddes.core.Event;

public class AggregateRootAssert<T extends AggregateRoot<?>>
		extends AbstractAssert<AggregateRootAssert<T>, AggregateRoot<?>> {

	public AggregateRootAssert(T actual) {
		super(actual, AggregateRootAssert.class);
	}

	@SuppressWarnings("unchecked")
	public AggregateRootAssert<T> call(Consumer<T> consumer) {
		consumer.accept((T) actual);
		return this;
	}

	@SuppressWarnings("unchecked")
	public AggregateRootAssert<T> wrongCall(Consumer<T> consumer) {
		assertThatIllegalArgumentException().isThrownBy(() -> consumer.accept((T) actual));
		return this;
	}

	@SuppressWarnings("unchecked")
	public AggregateRootAssert<T> isTrueThat(Predicate<T> predicate) {
		assertThat(predicate).accepts((T) actual);
		return this;
	}

	public AggregateRootAssert<T> markPendingChangesAsCommitted() {
		actual.markPendingChangesAsCommitted();
		return this;
	}

	public AggregateRootAssert<T> idIs(String id) {
		assertThat(actual.getId()).isEqualTo(id);
		return this;
	}

	public AggregateRootAssert<T> versionIs(int version) {
		assertThat(actual.getVersion()).isEqualTo(version);
		return this;
	}

	public AggregateRootAssert<T> thereArePendingChanges() {
		assertThat(actual.getPendingChanges()).isNotEmpty();
		return this;
	}

	public AggregateRootAssert<T> thereAreNoPendingChanges() {
		assertThat(actual.getPendingChanges()).isEmpty();
		return this;
	}

	public AggregateRootAssert<T> totalPendingChangesIs(int count) {
		assertThat(actual.getPendingChanges()).size().isEqualTo(count);
		return this;
	}

	public AggregateRootAssert<T> totalPendingChangesIs(int count, Predicate<? super Event> predicate) {
		assertThat(actual.getPendingChanges()).filteredOn(predicate).size().isEqualTo(count);
		return this;
	}

	public AggregateRootAssert<T> thereIsExactlyOnePendingChangeOfType(Type type) {
		totalPendingChangesIs(1);
		assertThat(actual.getPendingChanges()).filteredOn(e -> e.getClass() == type).size().isOne();
		return this;
	}

	public AggregateRootAssert<T> thereIsOnePendingChangeOfType(Type type) {
		assertThat(actual.getPendingChanges()).filteredOn(e -> e.getClass() == type).size().isOne();
		return this;
	}

	public AggregateRootAssert<T> allPendingChangesAreOfType(Type type) {
		assertThat(actual.getPendingChanges()).allMatch(e -> e.getClass() == type);
		return this;
	}

	public AggregateRootAssert<T> withPendingChanges(Consumer<Iterable<Event>> consumer) {
		consumer.accept(actual.getPendingChanges());
		return this;
	}
}
