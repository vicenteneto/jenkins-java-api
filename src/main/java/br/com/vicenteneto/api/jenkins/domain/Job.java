package br.com.vicenteneto.api.jenkins.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("project")
public class Job {

	@XStreamOmitField
	private String name;

	public Job(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
