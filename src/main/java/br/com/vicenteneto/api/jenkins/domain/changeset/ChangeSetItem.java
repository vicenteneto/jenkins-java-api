package br.com.vicenteneto.api.jenkins.domain.changeset;

import java.util.List;

public class ChangeSetItem {

	private String commitId;
	private String msg;
	private Author author;
	private List<String> affectedPaths;

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public List<String> getAffectedPaths() {
		return affectedPaths;
	}

	public void setAffectedPaths(List<String> affectedPaths) {
		this.affectedPaths = affectedPaths;
	}
}
