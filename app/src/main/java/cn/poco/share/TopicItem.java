package cn.poco.share;

public class TopicItem {
    private String topic;
    private String description;
    private String disTopic;

	public TopicItem()
	{
		super();
	}
	public TopicItem(String topic)
	{
		super();
		this.topic = topic;
	}

	public TopicItem(String topic, String description,String disTopic) {
		super();
		this.topic = topic;
		this.description = description;
		this.disTopic=disTopic;
	}

	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * @return the insert
	 */
	public String getDisTopic() {
		return disTopic;
	}
	/**
	 * @param insert the insert to set
	 */
	public void setDisTopic(String insert) {
		this.disTopic = insert;
	}
	
	
	@Override
	public String toString() {
		return "TopicItem [topic=" + topic + ", description=" + description
				+ "]";
	}

}
