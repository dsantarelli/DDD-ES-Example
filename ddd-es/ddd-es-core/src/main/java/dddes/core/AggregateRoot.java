package dddes.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.management.ReflectionException;

public abstract class AggregateRoot<ID> implements IAggregateRoot<ID> {

  private ID id;
  private int version;
  private final HashSet<IEntity<ID>> entities;
  private final List<Event> pendingChanges;

  protected AggregateRoot() {
    this.entities = new HashSet<IEntity<ID>>();
    this.pendingChanges = new ArrayList<>();
    this.version = 0;
  }

  public ID getId() {
    return id;
  }

  protected void setId(ID id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public Stream<Event> getPendingChanges() {
    return pendingChanges.stream();
  }

  public void loadFromHistory(Stream<Event> events) {    
    events.forEach(event -> applyChange(event, false));       
  }

  public void markPendingChangesAsCommitted() {
    pendingChanges.clear();
  }

  protected void applyChange(AggregateRootEvent<ID> change) {    
    if (change.getAggregateRootId() == null) change.setAggregateRootId(id);
    change.setAggregateRootVersion(version + 1);
    applyChange(change, true);
  }

  private void applyChange(Event change, boolean isNew) {
    if (change == null) return;
    applyEvent(change);
    if (isNew) pendingChanges.add(change);
    ++version;
  }

  private void applyEvent(Event event) {    
    try {      
      Method method = this.getClass().getDeclaredMethod("on", event.getClass());
      if (method != null) {
        method.setAccessible(true);
        method.invoke(this, event);
      } 
      else throw new ReflectionException(new NullPointerException("method 'on' not found for event: " + event.getClass().getName()));

    } catch (Exception e) { throw new RuntimeException(e); }
  }
  
  protected boolean entityExists(ID id) {
    return getEntities().anyMatch(x -> x.getId().equals(id));
  }

  protected <TEntity extends IEntity<ID>> void addEntity(TEntity entity, boolean throwIfExists) throws IllegalArgumentException {
    Objects.requireNonNull(entity, "entityId must not be null");
    TEntity existing = findEntityById(entity.getId());
    if (existing != null) {      
      if (throwIfExists)  throw new IllegalArgumentException("entity has already been added: " + entity.getId());      
      else return;
    }
    entities.add(entity);
  }

  protected <TEntity extends IEntity<ID>> void removeEntity(TEntity entity) {
    Objects.requireNonNull(entity, "entity must not be null");
    removeEntity(entity.getId(), false);
  }

  protected void removeEntity(ID entityId, boolean throwIfNotExists) {    
    Objects.requireNonNull(entityId, "entityId must not be null");
    IEntity<ID> entity = findEntityById(entityId);
    if (entity != null) entities.remove(entity);
    else if (throwIfNotExists) throw new IllegalArgumentException("entity has not been found: " + entityId);
  }
  
  @SuppressWarnings("unchecked")
  protected <T extends IEntity<ID>> T findEntityById(ID id) {    
    IEntity<ID> entity = getEntities().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    return entity != null ? (T)entity : null;   
  }
  
  protected Stream<IEntity<ID>> getEntities() {    
    return entities.stream();  
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("unchecked")
    AggregateRoot<ID> that = (AggregateRoot<ID>) obj;
    return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + version;
    return result;
  }
}