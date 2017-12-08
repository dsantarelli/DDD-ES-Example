package dddes.example.infrastructure;
import java.text.SimpleDateFormat;
import java.util.Date;

import dddes.core.Event;
import dddes.core.IEventPublisher;

public class ConsoleEventPublisher implements IEventPublisher {	
  
	public void publish(Event event) { 	  
	  
	  System.out.println(String.format("[%s] %s> published: %s", 
			  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date()), 
			  ConsoleEventPublisher.class.getSimpleName(),
			  event.toString()));
	}
}
