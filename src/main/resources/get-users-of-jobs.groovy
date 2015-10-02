import hudson.security.*;
import jenkins.security.*;

def jobs = Jenkins.instance.items;
jobs.each {
	println it.name;
	def authorizationMatrixProperty = it.getProperty(AuthorizationMatrixProperty.class)
	def sids = authorizationMatrixProperty?.getAllSIDs().plus('anonymous')
	for (sid in sids){
		println(sid);
	}
}
