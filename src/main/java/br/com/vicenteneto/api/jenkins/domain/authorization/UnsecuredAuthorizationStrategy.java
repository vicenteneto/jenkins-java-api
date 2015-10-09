package br.com.vicenteneto.api.jenkins.domain.authorization;

import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class UnsecuredAuthorizationStrategy implements AuthorizationStrategy {

	@Override
	public String getGroovyScript() {
		return ConfigurationUtil.getConfiguration("GROOVY_DEF_UNSECURED_AUTHORIZATION_STRATEGY");
	}
}
