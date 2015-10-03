package br.com.vicenteneto.api.jenkins.domain.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.vicenteneto.api.jenkins.domain.Permission;

public class GlobalMatrixAuthorizationStrategy implements AuthorizationStrategy {

	private Map<Permission, Set<String>> grantedPermissions = new HashMap<Permission, Set<String>>();
	private final Set<String> sids = new HashSet<String>();

	public void add(Permission permission, String sid) {
		Set<String> set = grantedPermissions.get(permission);

		if (set == null) {
			grantedPermissions.put(permission, set = new HashSet<String>());
		}

		set.add(sid);
		sids.add(sid);
	}
}
