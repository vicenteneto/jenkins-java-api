package br.com.vicenteneto.api.jenkins.domain;

public class Plugin {

	private String name;
	private String url;
	private String version;
	private boolean compatibleWithInstalledVersion;
	private String excerpt;
	private String title;
	private String wiki;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isCompatibleWithInstalledVersion() {
		return compatibleWithInstalledVersion;
	}

	public void setCompatibleWithInstalledVersion(boolean compatibleWithInstalledVersion) {
		this.compatibleWithInstalledVersion = compatibleWithInstalledVersion;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWiki() {
		return wiki;
	}

	public void setWiki(String wiki) {
		this.wiki = wiki;
	}
}
