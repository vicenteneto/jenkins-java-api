package br.com.vicenteneto.api.jenkins.domain;

public enum ItemType {

	VIEW("view"), JOB("job");

	private final String value;

	private ItemType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
