package com.manga.mangastreamnotifier;

// TODO: Auto-generated Javadoc
/**
 * The Class MangaItems.
 */
public class MangaItem {

	/** The title. */
	private String title;

	/** The description. */
	private String description;

	/** The date. */
	private String date;
	
	/** The url. */
	private String url;

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Instantiates a new manga items.
	 * 
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @param date
	 *            the date
	 */
	public MangaItem(String title, String description, String date) {
		super();
		this.title = title;
		this.description = description;
		this.date = date;
	}
	
	
	/**
	 * Instantiates a new manga item.
	 */
	public MangaItem() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MangaItem [title=" + title + ", description=" + description
				+ ", date=" + date + "]";
	}

}
