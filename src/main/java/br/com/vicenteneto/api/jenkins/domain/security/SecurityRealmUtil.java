package br.com.vicenteneto.api.jenkins.domain.security;

import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public final class SecurityRealmUtil {

	public static String generateGroovyScript(SecurityRealm security) {

		if (security instanceof HudsonPrivateSecurityRealm) {
			return generateHudsonPrivateSecurityRealm((HudsonPrivateSecurityRealm) security);
		} else { // security instanceof LDAPSecurityRealm
			return generateLDAPSecurityRealm((LDAPSecurityRealm) security);
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
}
