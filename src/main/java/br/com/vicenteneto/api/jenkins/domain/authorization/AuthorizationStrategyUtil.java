package br.com.vicenteneto.api.jenkins.domain.authorization;

import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public final class AuthorizationStrategyUtil {

	public static String generateGroovyScript(AuthorizationStrategy authorization) {

		if (authorization instanceof FullControlOnceLoggedInAuthorizationStrategy) {
			return generateFullControlOnceLoggedInAuthorizationStrategy();
		} else if (authorization instanceof UnsecuredAuthorizationStrategy) {
			return generateUnsecuredAuthorizationStrategy();
		} else { // authorization instanceof ProjectMatrixAuthorizationStrategy
			return generateProjectMatrixInAuthorizationStrategy((ProjectMatrixAuthorizationStrategy) authorization);
		}
	}

	private static String generateFullControlOnceLoggedInAuthorizationStrategy() {

		return ConfigurationUtil.getConfiguration("GROOVY_DEF_FULL_CONTROL_ONCE_LOGGED_IN_AUTHORIZATION_STRATEGY");
	}

	private static String generateUnsecuredAuthorizationStrategy() {

		return ConfigurationUtil.getConfiguration("GROOVY_DEF_UNSECURED_AUTHORIZATION_STRATEGY");
	}

	private static String generateProjectMatrixInAuthorizationStrategy(ProjectMatrixAuthorizationStrategy authorization) {

		StringBuilder sbScript = new StringBuilder();
		sbScript.append(ConfigurationUtil.getConfiguration("GROOVY_DEF_PROJECT_MATRIX_AUTHORIZATION_STRATEGY"));

		for (String sid : authorization.getGrantedPermissions().keySet()) {
			for (Permission permission : authorization.getGrantedPermissions().get(sid)) {
				sbScript.append(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_PERMISSION_TO_AUTHORIZATION"), permission.getValue(), sid));
			}
		}

		return sbScript.toString();
	}
}
