package br.com.vicenteneto.api.jenkins.domain;

public enum Permission {

	HUDSON_ADMINISTER("hudson.model.Hudson.Administer"),
	HUDSON_CONFIGURE_UPDATE_CENTER("hudson.model.Hudson.ConfigureUpdateCenter"),
	HUDSON_READ("hudson.model.Hudson.Read"),
	HUDSON_RUN_SCRIPTS("hudson.model.Hudson.RunScripts"),
	HUDSON_UPLOAD_PLUGINS("hudson.model.Hudson.UploadPlugins"),
	CREDENTIALS_CREATE("com.cloudbees.plugins.credentials.CredentialsProvider.Create"),
	CREDENTIALS_DELETE("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"),
	CREDENTIALS_MANAGE_DOMAINS("com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains"),
	CREDENTIALS_UPDATE("com.cloudbees.plugins.credentials.CredentialsProvider.Update"),
	CREDENTIALS_VIEW("com.cloudbees.plugins.credentials.CredentialsProvider.View"),
	COMPUTER_BUILD("hudson.model.Computer.Build"),
	COMPUTER_CONFIGURE("hudson.model.Computer.Configure"),
	COMPUTER_CONNECT("hudson.model.Computer.Connect"),
	COMPUTER_CREATE("hudson.model.Computer.Create"),
	COMPUTER_DELETE("hudson.model.Computer.Delete"),
	COMPUTER_DISCONNECT("hudson.model.Computer.Disconnect"),
	ITEM_BUILD("hudson.model.Item.Build"),
	ITEM_CANCEL("hudson.model.Item.Cancel"),
	ITEM_CONFIGURE("hudson.model.Item.Configure"),
	ITEM_CREATE("hudson.model.Item.Create"),
	ITEM_DELETE("hudson.model.Item.Delete"),
	ITEM_DISCOVER("hudson.model.Item.Discover"),
	ITEM_READ("hudson.model.Item.Read"),
	ITEM_WORKSPACE("hudson.model.Item.Workspace"),
	RUN_DELETE("hudson.model.Run.Delete"),
	RUN_UPDATE("hudson.model.Run.Update"),
	VIEW_CONFIGURE("hudson.model.View.Configure"),
	VIEW_CREATE("hudson.model.View.Create"),
	VIEW_DELETE("hudson.model.View.Delete"),
	VIEW_READ("hudson.model.View.Read"),
	SCM_TAG("hudson.scm.SCM.Tag");

	private String value;

	private Permission(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
