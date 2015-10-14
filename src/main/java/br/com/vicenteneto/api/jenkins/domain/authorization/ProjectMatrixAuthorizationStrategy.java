package br.com.vicenteneto.api.jenkins.domain.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class ProjectMatrixAuthorizationStrategy implements AuthorizationStrategy {

	private final Map<String, Set<Permission>> grantedPermissions = new HashMap<String, Set<Permission>>();

	public void add(String sid, Permission permission) {
		if (!grantedPermissions.containsKey(sid)) {
			grantedPermissions.put(sid, new HashSet<Permission>());
		}
		
		Set<Permission> set = grantedPermissions.get(sid);
		set.add(permission);
	}

	@Override
	public String getGroovyScript() {
		StringBuilder sbScript = new StringBuilder();
		sbScript.append(ConfigurationUtil.getConfiguration("GROOVY_DEF_PROJECT_MATRIX_AUTHORIZATION_STRATEGY"));
		
		for (String sid : grantedPermissions.keySet()) {
			for (Permission permission : grantedPermissions.get(sid)) {
				sbScript.append(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_PERMISSION_TO_AUTHORIZATION"), permission.getValue(), sid));
			}
		}
		
		return sbScript.toString();
	}
}
