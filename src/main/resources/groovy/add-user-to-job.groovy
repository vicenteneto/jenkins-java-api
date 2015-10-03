import hudson.security.*;
import jenkins.security.*;

Jenkins.instance.getItem("JobName")
    .addProperty(new hudson.security.AuthorizationMatrixProperty())

boolean dryrun=true;

switch (Jenkins.instance.authorizationStrategy){
  case ProjectMatrixAuthorizationStrategy:
    def jobs = Jenkins.instance.items;
    jobs.each {
        if (it.name == "turmalina-build") {
            println it.name.center(80,'-');
            def authorizationMatrixProperty = it.getProperty(AuthorizationMatrixProperty.class);
            def sids = authorizationMatrixProperty?.getAllSIDs();
            for (sid in sids){
                println(sid);
            }

            authorizationMatrixProperty.add(Item.CANCEL, "rodrigo.vilar");
            it.save();
            println("");

            sids = authorizationMatrixProperty?.getAllSIDs();
            for (sid in sids){
                println(sid);
            }
        }
    }
    break;
}