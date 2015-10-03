package br.com.vicenteneto.api.jenkins.domain.security;

public class LDAPSecurityRealm implements SecurityRealm {

	private String server;
	private String rootDN;
	private String userSearchFilter;
	private String managerDN;
	private String managerPasswordSecret;

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

	public String getUserSearchFilter() {
		return userSearchFilter;
	}

	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}

	public String getManagerDN() {
		return managerDN;
	}

	public void setManagerDN(String managerDN) {
		this.managerDN = managerDN;
	}

	public String getManagerPasswordSecret() {
		return managerPasswordSecret;
	}

	public void setManagerPasswordSecret(String managerPasswordSecret) {
		this.managerPasswordSecret = managerPasswordSecret;
	}
}
