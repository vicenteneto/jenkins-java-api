package br.com.vicenteneto.api.jenkins.domain;

public class Build {

	private int number;
	private String url;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
