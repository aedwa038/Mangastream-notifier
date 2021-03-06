package com.manga.mangastreamnotifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class MangaItems. Stores chapter information for the latest chapters on the feed 
 */
public class MangaItem implements Comparable<MangaItem> {

	/** The title. */
	private String title;

	/** The description. */
	private String description;

	/** The date. */
	private Date date;

	/** The url. */
	private String url;
	
	
	/** The id. */
	private int id;


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Instantiates a new manga item.
	 *
	 * @param title the title
	 * @param description the description
	 * @param date the date
	 * @param url the url
	 * @param id the id
	 */
	public MangaItem(String title, String description, Date date, String url, int id) {
		super();
		this.title = title;
		this.description = description;
		this.date = date;
		this.url = url;
		this.id = id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

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
	 * @param url
	 *            the url to set
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
		String pubDate = "";
		if (date != null)
		{
			pubDate = new SimpleDateFormat("EEE, d MMM yyyy").format(date);
		}
		return pubDate;
	}
	
	/**
	 * Gets the pub date.
	 *
	 * @return the pub date
	 */
	public Date getpubDate()
	{
		return date;
	}
	
	
	public void setpubDate(Date date)
	{
		this.date = date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		if (date != null)
		{
			try {
				this.date = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z")
						.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
		setDate(date);
	}

	/**
	 * Instantiates a new manga item.
	 *
	 * @param title the title
	 * @param description the description
	 * @param url the url
	 * @param date the date
	 */
	public MangaItem(String title, String description, String url, String date) {
		super();
		this.title = title;
		this.description = description;
		setDate(date);
		this.url = url;
	}

	/**
	 * Instantiates a new manga item.
	 */
	public MangaItem() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MangaItem [title=" + title + ":: description=" + description
				+ ":: date=" + date.toString() + ":: url=" + url + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MangaItem another) {
		if (getpubDate() == null || another.getpubDate() == null)
		      return 0;
		    return getpubDate().compareTo(another.getpubDate());
		  }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MangaItem)) {
			return false;
		}
		MangaItem other = (MangaItem) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

}
