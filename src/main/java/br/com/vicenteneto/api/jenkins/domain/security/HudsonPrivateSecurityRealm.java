package br.com.vicenteneto.api.jenkins.domain.security;

import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

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

	@Override
	public String getGroovyScript() {
		return String.format(ConfigurationUtil.getConfiguration("GROOVY_DEF_HUDSON_PRIVATE_SECURITY_REALM"), allowsSignUp);
	}
}
