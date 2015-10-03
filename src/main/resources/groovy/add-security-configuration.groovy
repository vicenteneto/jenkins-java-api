import hudson.security.*;
import jenkins.security.*;

def instance = Jenkins.getInstance();
def strategy = instance.getAuthorizationStrategy();

strategy.add("hudson.model.Hudson.Administer", "admin");
