package dddes.example.domain.backlogitem;
import java.util.UUID;
import dddes.example.domain.product.Product;

public class BacklogItemFactory {
		
	public static BacklogItem create(String name, Product product) {		
		return create(UUID.randomUUID().toString(), name, product);
	}
	
	public static BacklogItem create(String id, String name, Product product) {		
		return new BacklogItem(id, name, product);
	}
}
