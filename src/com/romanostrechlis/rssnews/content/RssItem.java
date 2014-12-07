package com.romanostrechlis.rssnews.content;

/**
 * Represents a single item from the xml file.
 * 
 * <p>There is nothing interesting about this class, 
 * except that the {@link RssFeed.list} is consisted of 
 * RssItem(s).
 * 
 * <h3>Constructors</h3>
 * <ul>
 * <li>{@link RssItem()}</li>
 * <li>{@link RssItem(String, String, String, String, String)}</li>
 * </ul>
 * 
 * @author Romanos Trechlis
 */
public class RssItem {
	private String id;
	private String title;
	private String description;
	private String link;
	private String parent;

	/**
	 * Constructors
	 */
	public RssItem() {}

	public RssItem(String id, String title, String description, String link, String parent) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.link = link;
		this.parent = parent;
	}
	
	public void setTitle(String title){ this.title = title; }
	public void setDescription(String description){ this.description = description; }
	public void setLink(String link){ this.link = link; }
	public void setId(String id) { this.id = id; }
	public void setParent(String parent) { this.parent = parent; }
		
	public String getTitle(){ return title; }
	public String getDescription(){ return description; }
	public String getLink(){ return link; }
	public String getId() { return id; }
	public String getParent() { return parent; }
}
