package br.com.vicenteneto.api.jenkins.domain.security;

public class HudsonPrivateSecurityRealm implements SecurityRealm {

	private boolean allowsSignUp;

	public HudsonPrivateSecurityRealm(boolean allowsSignUp) {
		this.allowsSignUp = allowsSignUp;
	}

	public boolean isAllowsSignUp() {
		return allowsSignUp;
	}

	public void setAllowsSignUp(boolean allowsSignUp) {
		this.allowsSignUp = allowsSignUp;
	}
}
