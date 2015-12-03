package br.com.vicenteneto.api.jenkins.util;

import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.FullControlOnceLoggedInAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.ProjectMatrixAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.LDAPSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;

public final class GroovyUtil {

	public static String generateGroovyScript(SecurityRealm security) {

		if (security instanceof HudsonPrivateSecurityRealm) {
			return generateHudsonPrivateSecurityRealm((HudsonPrivateSecurityRealm) security);
		} else { // security instanceof LDAPSecurityRealm
			return generateLDAPSecurityRealm((LDAPSecurityRealm) security);
		}
	}

	public static String generateGroovyScript(AuthorizationStrategy authorization) {

		if (authorization instanceof FullControlOnceLoggedInAuthorizationStrategy) {
			return generateFullControlOnceLoggedInAuthorizationStrategy();
		} else if (authorization instanceof UnsecuredAuthorizationStrategy) {
			return generateUnsecuredAuthorizationStrategy();
		} else { // authorization instanceof ProjectMatrixAuthorizationStrategy
			return generateProjectMatrixInAuthorizationStrategy((ProjectMatrixAuthorizationStrategy) authorization);
		}
	}

	private static String generateHudsonPrivateSecurityRealm(HudsonPrivateSecurityRealm security) {

		boolean allowsSignUp = security.isAllowsSignUp();
		return String.format(ConfigurationUtil.getConfiguration("GROOVY_DEF_HUDSON_PRIVATE_SECURITY_REALM"), allowsSignUp);
	}

	private static String generateLDAPSecurityRealm(LDAPSecurityRealm security) {

		String server = security.getServer();
		String rootDN = security.getRootDN();
		String userSearchBase = security.getUserSearchBase();
		String userSearch = security.getUserSearch();
		String groupSearchBase = security.getGroupSearchBase();
		String groupSearchFilter = security.getGroupSearchFilter();
		String groupMembershipFilter = security.getGroupMembershipFilter();
		String managerDN = security.getManagerDN();
		String managerPassword = security.getManagerPassword();
		boolean inhibitInferRootDN = security.isInhibitInferRootDN();

		return String.format(ConfigurationUtil.getConfiguration("GROOVY_DEF_LDAP_SECURITY_REALM"), server, rootDN,
				userSearchBase, userSearch, groupSearchBase, groupSearchFilter, groupMembershipFilter, managerDN,
				managerPassword, inhibitInferRootDN);
	}

	private static String generateFullControlOnceLoggedInAuthorizationStrategy() {

		return ConfigurationUtil.getConfiguration("GROOVY_DEF_FULL_CONTROL_ONCE_LOGGED_IN_AUTHORIZATION_STRATEGY");
	}

	private static String generateUnsecuredAuthorizationStrategy() {

		return ConfigurationUtil.getConfiguration("GROOVY_DEF_UNSECURED_AUTHORIZATION_STRATEGY");
	}

	private static String generateProjectMatrixInAuthorizationStrategy(
			ProjectMatrixAuthorizationStrategy authorization) {

		StringBuilder sbScript = new StringBuilder();
		sbScript.append(ConfigurationUtil.getConfiguration("GROOVY_DEF_PROJECT_MATRIX_AUTHORIZATION_STRATEGY"));

		for (String sid : authorization.getGrantedPermissions().keySet()) {
			for (Permission permission : authorization.getGrantedPermissions().get(sid)) {
				sbScript.append(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_PERMISSION_TO_AUTHORIZATION"), permission.getValue(), sid));
			}
		}

		return sbScript.toString();
	}

	private GroovyUtil() { }
}
