def instance = Jenkins.getInstance();

instance.getItems(hudson.model.Project).each {
	project -> println(project.displayName);
}
