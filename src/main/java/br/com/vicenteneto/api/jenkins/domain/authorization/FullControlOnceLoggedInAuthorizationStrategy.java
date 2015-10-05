package br.com.vicenteneto.api.jenkins.domain.authorization;

public class FullControlOnceLoggedInAuthorizationStrategy implements AuthorizationStrategy {

	@Override
	public String getGroovyScript() {
		return "def strategy = new FullControlOnceLoggedInAuthorizationStrategy();";
	}
}
