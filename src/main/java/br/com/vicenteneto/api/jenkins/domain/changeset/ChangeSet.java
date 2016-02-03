package br.com.vicenteneto.api.jenkins.domain.changeset;

import java.util.List;

public class ChangeSet {

	private String kind;
	private List<ChangeSetItem> items;

	public List<ChangeSetItem> getItems() {
		return items;
	}

	public void setItems(List<ChangeSetItem> itens) {
		this.items = itens;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
