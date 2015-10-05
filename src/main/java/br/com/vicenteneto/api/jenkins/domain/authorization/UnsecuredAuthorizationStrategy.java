package br.com.vicenteneto.api.jenkins.domain.authorization;

public class UnsecuredAuthorizationStrategy implements AuthorizationStrategy {

	@Override
	public String getGroovyScript() {
		return "def authorization = new AuthorizationStrategy.Unsecured();";
	}
}
