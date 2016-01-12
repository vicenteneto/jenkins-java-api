package br.com.vicenteneto.api.jenkins.tests.data;

import java.util.LinkedList;

import br.com.vicenteneto.api.jenkins.domain.Build;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.Plugin;

public class TestsData {

	public static String PLUGIN_NAME = "junit";
	public static String PLUGIN_URL = "http://updates.jenkins-ci.org/download/plugins/junit/1.10/junit.hpi";
	public static String PLUGIN_VERSION = "1.10";
	public static boolean PLUGIN_COMPATIBLE_WITH_INSTALLED_VERSION = true;
	public static String PLUGIN_EXCERPT = "Allows JUnit-format test results to be published.";
	public static String PLUGIN_TITLE = "JUnit Plugin";
	public static String PLUGIN_WIKI = "https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin";

	public static String VIEW_NAME = "View";
	public static String VIEW_DESCRIPTION = "Description";

	public static String JOB_NAME = "Job";
	public static String JOB_DESCRIPTION = "Description";
	public static boolean JOB_BUILDABLE = true;
	public static int JOB_NEXT_BUILD_NUMBER = 2;

	public static String USERNAME = "Username";

	public static Plugin createPlugin() {
		Plugin plugin = new Plugin();
		plugin.setName(PLUGIN_NAME);
		plugin.setUrl(PLUGIN_URL);
		plugin.setVersion(PLUGIN_VERSION);
		plugin.setCompatibleWithInstalledVersion(PLUGIN_COMPATIBLE_WITH_INSTALLED_VERSION);
		plugin.setExcerpt(PLUGIN_EXCERPT);
		plugin.setTitle(PLUGIN_TITLE);
		plugin.setWiki(PLUGIN_WIKI);

		return plugin;
	}

	public static ListView createListView() {
		ListView listView = new ListView(VIEW_NAME);
		listView.setDescription(VIEW_DESCRIPTION);
		listView.setJobs(new LinkedList<Job>());

		return listView;
	}

	public static Job createJob() {
		Job job = new Job(JOB_NAME);
		job.setDescription(JOB_DESCRIPTION);
		job.setBuildable(JOB_BUILDABLE);
		job.setBuilds(new LinkedList<Build>());
		job.setNextBuildNumber(JOB_NEXT_BUILD_NUMBER);

		return job;
	}
}
