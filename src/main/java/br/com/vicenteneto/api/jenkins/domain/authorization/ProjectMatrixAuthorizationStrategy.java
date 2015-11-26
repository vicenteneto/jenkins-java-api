package br.com.vicenteneto.api.jenkins.domain.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.vicenteneto.api.jenkins.domain.Permission;

public class ProjectMatrixAuthorizationStrategy implements AuthorizationStrategy {

	private final Map<String, Set<Permission>> grantedPermissions = new HashMap<String, Set<Permission>>();

	public Map<String, Set<Permission>> getGrantedPermissions() {
		return grantedPermissions;
	}

	public void add(String sid, Permission permission) {

		if (!grantedPermissions.containsKey(sid)) {
			grantedPermissions.put(sid, new HashSet<Permission>());
		}

		Set<Permission> set = grantedPermissions.get(sid);
		set.add(permission);
	}
}
