package br.com.vicenteneto.api.jenkins.domain.report;

import java.util.List;

public class CoverageReport {

	private String name;
	private List<CoverageElement> elements;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CoverageElement> getElements() {
		return elements;
	}

	public void setElements(List<CoverageElement> elements) {
		this.elements = elements;
	}
}
