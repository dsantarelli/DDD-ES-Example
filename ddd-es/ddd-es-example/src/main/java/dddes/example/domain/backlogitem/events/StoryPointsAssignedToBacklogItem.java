package dddes.example.domain.backlogitem.events;

import dddes.core.AggregateRootEvent;
import dddes.example.domain.backlogitem.StoryPoints;

public class StoryPointsAssignedToBacklogItem extends AggregateRootEvent<String> {

	private final StoryPoints storyPoints;

	public StoryPointsAssignedToBacklogItem(StoryPoints storyPoints) {
		this.storyPoints = storyPoints;
	}

	public StoryPoints getStoryPoints() {
		return storyPoints;
	}
}
