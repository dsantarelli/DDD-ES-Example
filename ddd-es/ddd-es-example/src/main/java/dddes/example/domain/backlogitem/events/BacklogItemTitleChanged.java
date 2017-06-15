package dddes.example.domain.backlogitem.events;

import dddes.core.AggregateRootEvent;

public class BacklogItemTitleChanged extends AggregateRootEvent<String> {

	private final String title;

	public BacklogItemTitleChanged(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}