package br.com.vicenteneto.api.jenkins.domain.security;

public class LDAPSecurityRealm implements SecurityRealm {

	private String server;
	private String rootDN;
	private String userSearchBase;
	private String userSearch;
	private String groupSearchBase;
	private String managerDN;
	private String managerPassword;
	private boolean inhibitInferRootDN;

	public LDAPSecurityRealm(String server, String rootDN, String userSearchBase, String userSearch,
			String groupSearchBase, String managerDN, String managerPassword, boolean inhibitInferRootDN) {
		this.server = server;
		this.rootDN = rootDN;
		this.userSearchBase = userSearchBase;
		this.userSearch = userSearch;
		this.groupSearchBase = groupSearchBase;
		this.managerDN = managerDN;
		this.managerPassword = managerPassword;
		this.inhibitInferRootDN = inhibitInferRootDN;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getRootDN() {
		return rootDN;
	}

	public void setRootDN(String rootDN) {
		this.rootDN = rootDN;
	}

	public String getUserSearchBase() {
		return userSearchBase;
	}

	public void setUserSearchBase(String userSearchBase) {
		this.userSearchBase = userSearchBase;
	}

	public String getUserSearch() {
		return userSearch;
	}

	public void setUserSearch(String userSearch) {
		this.userSearch = userSearch;
	}

	public String getGroupSearchBase() {
		return groupSearchBase;
	}

	public void setGroupSearchBase(String groupSearchBase) {
		this.groupSearchBase = groupSearchBase;
	}

	public String getManagerDN() {
		return managerDN;
	}

	public void setManagerDN(String managerDN) {
		this.managerDN = managerDN;
	}

	public String getManagerPassword() {
		return managerPassword;
	}

	public void setManagerPassword(String managerPassword) {
		this.managerPassword = managerPassword;
	}

	public boolean getInhibitInferRootDN() {
		return inhibitInferRootDN;
	}

	public void setInhibitInferRootDN(boolean inhibitInferRootDN) {
		this.inhibitInferRootDN = inhibitInferRootDN;
	}

	@Override
	public String getGroovyScript() {
		return String.format("def security = new LDAPSecurityRealm('%s', '%s', '%s', '%s', '%s', '%s', '%s', %b);",
				server, rootDN, userSearchBase, userSearch, groupSearchBase, managerDN, managerPassword, inhibitInferRootDN);
	}
}
