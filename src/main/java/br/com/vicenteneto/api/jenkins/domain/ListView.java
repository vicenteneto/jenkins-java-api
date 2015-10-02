package br.com.vicenteneto.api.jenkins.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.model.ListView")
public class ListView {

	private String name;

	public ListView(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
