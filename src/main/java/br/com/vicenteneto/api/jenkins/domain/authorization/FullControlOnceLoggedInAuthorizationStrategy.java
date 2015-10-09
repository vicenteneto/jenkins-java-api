package br.com.vicenteneto.api.jenkins.domain.authorization;

import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class FullControlOnceLoggedInAuthorizationStrategy implements AuthorizationStrategy {

	@Override
	public String getGroovyScript() {
		return ConfigurationUtil.getConfiguration("GROOVY_DEF_FULL_CONTROL_ONCE_LOGGED_IN_AUTHORIZATION_STRATEGY");
	}
}
