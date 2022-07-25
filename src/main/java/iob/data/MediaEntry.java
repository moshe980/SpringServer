package iob.data;

public class MediaEntry {
	private String id;
	private String name;
	private String director;
	private String mainCast;
	private String genre;
	private String description;
	private int rating;
	private String language;
	private String origin;
	private boolean isWatched;
	

	public MediaEntry() {}


	public MediaEntry(String id, String name, String director, String mainCast, String genre, String description,
			int rating, String language, String origin, boolean isWatched) {
		super();
		this.id = id;
		this.name = name;
		this.director = director;
		this.mainCast = mainCast;
		this.genre = genre;
		this.description = description;
		this.rating = rating;
		this.language = language;
		this.origin = origin;
		this.isWatched = isWatched;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDirector() {
		return director;
	}


	public void setDirector(String director) {
		this.director = director;
	}


	public String getMainCast() {
		return mainCast;
	}


	public void setMainCast(String mainCast) {
		this.mainCast = mainCast;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getRating() {
		return rating;
	}


	public void setRating(int rating) {
		this.rating = rating;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getOrigin() {
		return origin;
	}


	public void setOrigin(String origin) {
		this.origin = origin;
	}


	public boolean isWatched() {
		return isWatched;
	}


	public void setWatched(boolean isWatched) {
		this.isWatched = isWatched;
	}

	
	
	
}
