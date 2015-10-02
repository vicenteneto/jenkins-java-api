import hudson.security.*;
import jenkins.security.*;

class BuildPermission {
    static buildNewAccessList(userOrGroup, permissions) {
      	def newPermissionsMap = [:];
      	permissions.each {
        	newPermissionsMap.put(Permission.fromId(it), userOrGroup);
        }
        return newPermissionsMap;
    }
}

def strategy = new ProjectMatrixAuthorizationStrategy();

def anonymousPermissions = [
    "hudson.model.Hudson.Read"
];
def anonymous = BuildPermission.buildNewAccessList("anonymous", anonymousPermissions);
anonymous.each { p, u -> strategy.add(p, u) };
  
def jenkinsAdminPermissions = [
    "hudson.model.Hudson.Administer",
    "hudson.model.Hudson.ConfigureUpdateCenter",
    "hudson.model.Hudson.Read",
    "hudson.model.Hudson.RunScripts",
    "hudson.model.Hudson.UploadPlugins",
    "com.cloudbees.plugins.credentials.CredentialsProvider.Create",
    "com.cloudbees.plugins.credentials.CredentialsProvider.Delete",
    "com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains",
    "com.cloudbees.plugins.credentials.CredentialsProvider.Update",
    "com.cloudbees.plugins.credentials.CredentialsProvider.View",
    "hudson.model.Computer.Build",
    "hudson.model.Computer.Configure",
    "hudson.model.Computer.Connect",
    "hudson.model.Computer.Create",
    "hudson.model.Computer.Delete",
    "hudson.model.Computer.Disconnect",
    "hudson.model.Item.Build",
    "hudson.model.Item.Cancel",
    "hudson.model.Item.Configure",
    "hudson.model.Item.Create",
    "hudson.model.Item.Delete",
    "hudson.model.Item.Discover",
    "hudson.model.Item.Read",
    "hudson.model.Item.Workspace",
    "hudson.model.Run.Delete",
    "hudson.model.Run.Update",
    "hudson.model.View.Configure",
    "hudson.model.View.Create",
    "hudson.model.View.Delete",
    "hudson.model.View.Read"
];
def admin = BuildPermission.buildNewAccessList("vicente", jenkinsAdminPermissions);
admin.each { p, u -> strategy.add(p, u) };

Jenkins.getInstance().setAuthorizationStrategy(strategy);
