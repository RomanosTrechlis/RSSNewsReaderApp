package com.romanostrechlis.rssnews.content;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**  
 * Represents an RSS link.	
 * 
 * <p>It has String attributes id, name, url, category, 
 * content and holds a {@link Collection} and a {@link List}
 * of {@link RssItem}s.
 * 
 * <h3>Constructors</h3>
 * <ul>
 * <li>{@link RssFeed(String, String, String, String)}: 
 * Used to add new RSS feeds to application.</li>
 * <li>{@link RssFeed(String, String, String, Boolean, String, Boolean, String, int)}</li>
 * </ul>
 * 
 * <h3>Methods</h3>
 * <ul>
 * <li>{@link #parseXML()}</li>
 * <li>{@link #getValue(String, Element)}</li>
 * </ul>
 * 
 * @author Romanos Trechlis
 * @version 0.1v Noe 18, 2014.
 */
public class RssFeed {
	private String id;
	private String name;
	private String url;
	private String category;
	private String content;
	private Boolean enabled = true;
	private List<RssItem> list = new ArrayList<RssItem>();
	private Boolean newContent = false;
	
	private int hashCode = 0;
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param name
	 * @param url
	 * @param content
	 */
	public RssFeed(String id, String name, String url, String category) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.category = category;
	}

	/**
	 * Constructor.
	 */
	public RssFeed() {
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param name
	 * @param url
	 * @param enabled
	 * @param category
	 * @param newContent
	 * @param content
	 * @param hashCode
	 */
	public RssFeed(String id, String name, String url, Boolean enabled, String category, Boolean newContent, String content,
			int hashCode) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.enabled = enabled;
		this.category = category;
		this.newContent = newContent;
		this.content = content;
		this.hashCode = hashCode;
	}

	// Setters & Getters
	public void setId(String id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setUrl(String url) { this.url = url; }
	public void setCategory(String category) { this.category = category; }
	public void setNewContent(Boolean newContent) { this.newContent = newContent; }
	// public void setNewContent(Boolean newContent) { this.newContent = newContent; }
	public void setHashCode(int hashCode) { this.hashCode = hashCode; }
	public void setEnabled(Boolean enabled) { this.enabled = enabled; }
	public void setList(List<RssItem> list) { this.list = list; }
	/** 
	 * Method for setting rss raw content while flagging the existence of new rss items.
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		if (getHashCode() == 0) {
			setNewContent(false);
			setHashCode(content.hashCode());
			this.content = content;
		}
		else if (getHashCode() == content.hashCode()) {
			this.content = content;
			setNewContent(false);
			
		} else {
			this.content = content;
			setNewContent(true);
			setHashCode(content.hashCode()); // set new hash value
		}	
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public String getUrl() { return url; }
	public String getCategory() { return category; }
	public String getContent() { return content; }
	public int getHashCode() { return hashCode; }
	public Boolean getNewContent() { return newContent; }
	public Boolean getEnabled() { return enabled; }
	public List<RssItem> getList() { return list; }

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Method called to parse a string from attribute content of Item class.
	 * 
	 * <p> Adds the resulting RssFeed into a List<RssFeed>
	 * 
	 * @throws ParserConfigurationException, SAXException, IOException
	 */
	public void parseXML() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document dom = null;
		try {
			//Using factory get an instance of document builder
			db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
			InputSource is = new InputSource(new StringReader(getContent()));
			dom = db.parse(is);
		}catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		//get a nodelist of elements
		dom.getDocumentElement().normalize();
		NodeList nodes = dom.getElementsByTagName("item");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			RssItem rf = new RssItem();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				rf.setTitle(getValue("title", element));
				rf.setDescription(getValue("description", element));
				rf.setLink(getValue("link", element));
				rf.setParent(this.getId());
				list.add(rf);
			}
		}
	}

	/**
	 * Helper method to {@link #parseXML()}
	 * 
	 * @param tag
	 * @param element
	 * @return value between tags.
	 */
	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}