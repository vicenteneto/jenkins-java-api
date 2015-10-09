package br.com.vicenteneto.api.jenkins.domain;

import java.util.LinkedList;
import java.util.List;

public class ListView {

	private String name;
	private String description;
	private List<Job> jobs = new LinkedList<>();

	public ListView(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
}
