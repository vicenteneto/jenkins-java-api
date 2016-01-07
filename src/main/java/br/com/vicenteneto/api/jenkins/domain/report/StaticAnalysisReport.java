package br.com.vicenteneto.api.jenkins.domain.report;

import java.util.Map;

public class StaticAnalysisReport {

	private Map<String, Integer> violations;
	private int violationsCount;

	public Map<String, Integer> getViolations() {
		return violations;
	}

	public void setViolations(Map<String, Integer> violations) {
		this.violations = violations;
	}

	public int getViolationsCount() {
		return violationsCount;
	}

	public void setViolationsCount(int violationsCount) {
		this.violationsCount = violationsCount;
	}
}
