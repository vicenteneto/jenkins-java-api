package br.com.vicenteneto.api.jenkins.domain;

import java.util.List;

public class Job {

	private String description;
	private String name;
	private boolean buildable;
	private List<Build> builds;
	private Build lastBuild;
	private Build lastCompletedBuild;
	private Build lastFailedBuild;
	private Build lastStableBuild;
	private Build lastSuccessfulBuild;

	public Job(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBuildable() {
		return buildable;
	}

	public void setBuildable(boolean buildable) {
		this.buildable = buildable;
	}

	public List<Build> getBuilds() {
		return builds;
	}

	public void setBuilds(List<Build> builds) {
		this.builds = builds;
	}

	public Build getLastBuild() {
		return lastBuild;
	}

	public void setLastBuild(Build lastBuild) {
		this.lastBuild = lastBuild;
	}

	public Build getLastCompletedBuild() {
		return lastCompletedBuild;
	}

	public void setLastCompletedBuild(Build lastCompletedBuild) {
		this.lastCompletedBuild = lastCompletedBuild;
	}

	public Build getLastFailedBuild() {
		return lastFailedBuild;
	}

	public void setLastFailedBuild(Build lastFailedBuild) {
		this.lastFailedBuild = lastFailedBuild;
	}

	public Build getLastStableBuild() {
		return lastStableBuild;
	}

	public void setLastStableBuild(Build lastStableBuild) {
		this.lastStableBuild = lastStableBuild;
	}

	public Build getLastSuccessfulBuild() {
		return lastSuccessfulBuild;
	}

	public void setLastSuccessfulBuild(Build lastSuccessfulBuild) {
		this.lastSuccessfulBuild = lastSuccessfulBuild;
	}
}
