package br.com.vicenteneto.api.jenkins.domain.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.vicenteneto.api.jenkins.domain.Permission;

public class ProjectMatrixAuthorizationStrategy implements AuthorizationStrategy {

	private final Map<String, Set<Permission>> grantedPermissions = new HashMap<String, Set<Permission>>();
	private List<String> sids = new LinkedList<String>();

	public void add(String sid, Permission permission) {
		if (!grantedPermissions.containsKey(sid)) {
			grantedPermissions.put(sid, new HashSet<Permission>());
			sids.add(sid);
		}
		
		Set<Permission> set = grantedPermissions.get(sid);
		set.add(permission);
	}

	@Override
	public String getGroovyScript() {
		String script = "class BuildPermission {\n"
					+ "static buildNewAccessList(userOrGroup, permissions) {\n"
						+ "def newPermissionsMap = [:];\n"
						+ "permissions.each {\n"
							+ "newPermissionsMap.put(Permission.fromId(it), userOrGroup);\n"
						+ "}\n"
						+ "return newPermissionsMap;\n"
					+ "}\n"
				+ "}\n";

		script += "def authorization = new ProjectMatrixAuthorizationStrategy();\n";
		
		for (String sid : sids) {
			String defName = sid.replace("-", "_");
			String userPermission = "def " + defName + "Permissions = [\n";
			
			for (Permission permission : grantedPermissions.get(sid)) {
				userPermission += "'" + permission.getValue() + "',\n";
			}
			
			userPermission += "];\n";
			userPermission += "def " + defName + " = BuildPermission.buildNewAccessList('" + sid + "', " + defName + "Permissions);\n";
			userPermission += defName + ".each { p, u -> authorization.add(p, u) };\n";
			script += userPermission;
		}
		
		return script;
	}
}
