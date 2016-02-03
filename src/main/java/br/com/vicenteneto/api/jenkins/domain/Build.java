package br.com.vicenteneto.api.jenkins.domain;

import br.com.vicenteneto.api.jenkins.domain.changeset.ChangeSet;

public class Build {

	private int number;
	private String description;
	private String displayName;
	private String result;
	private String url;
	private ChangeSet changeSet;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

	public void setChangeSet(ChangeSet changeSet) {
		this.changeSet = changeSet;
	}
}
