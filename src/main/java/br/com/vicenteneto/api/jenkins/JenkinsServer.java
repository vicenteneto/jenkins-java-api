package br.com.vicenteneto.api.jenkins;

import java.net.URI;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.mashape.unirest.http.HttpResponse;
import com.thoughtworks.xstream.XStream;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;
import br.com.vicenteneto.api.jenkins.util.Constants;

public class JenkinsServer {

	private static final String NAME = ConfigurationUtil.getConfiguration("NAME");

	private JenkinsClient jenkinsClient;
	private XStream xStream;
	
	private JenkinsServer() {
		xStream = new XStream();
		xStream.autodetectAnnotations(true);
	}

	public JenkinsServer(URI serverURI) {
		this();
		jenkinsClient = new JenkinsClient(serverURI);
	}

	public JenkinsServer(URI serverURI, String username, String password) {
		this();
		jenkinsClient = new JenkinsClient(serverURI, username, password);
	}
	
	public HttpResponse<String> setSecurityRealm(SecurityRealm securityRealm) throws JenkinsServerException {
		String importHudsonSecurity = ConfigurationUtil.getConfiguration("IMPORT_HUDSON_SECURITY");
		String security = securityRealm.getGroovyScript();
		String jenkinsInstance = ConfigurationUtil.getConfiguration("JENKINS_INSTANCE");
		String setSecurityRealm = ConfigurationUtil.getConfiguration("JENKINS_SET_SECURITY_REALM");
		String jenkinsSave = ConfigurationUtil.getConfiguration("JENKINS_SAVE");
		
		return executeScript(createString(importHudsonSecurity, security, jenkinsInstance, setSecurityRealm, jenkinsSave));
	}

	public HttpResponse<String> setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy) throws JenkinsServerException {
		String importHudsonSecurity = ConfigurationUtil.getConfiguration("IMPORT_HUDSON_SECURITY");
		String authorization = authorizationStrategy.getGroovyScript();
		String jenkinsInstance = ConfigurationUtil.getConfiguration("JENKINS_INSTANCE");
		String setAuthorizationStrategy = ConfigurationUtil.getConfiguration("JENKINS_SET_AUTHORIZATION_STRATEGY");
		String jenkinsSave = ConfigurationUtil.getConfiguration("JENKINS_SAVE");
		
		return executeScript(createString(importHudsonSecurity, authorization, jenkinsInstance, setAuthorizationStrategy, jenkinsSave));
	}

	public String getVersion() throws JenkinsServerException {
		return executeScript("println(Jenkins.instance.version)").getBody();
	}

	public ListView getViewByName(String viewName) throws JenkinsServerException {
		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
		}
		
		return new ListView(viewName);
	}

	public boolean checkViewExists(String viewName) throws JenkinsServerException {
		String response = executeScript(String.format("println(Jenkins.instance.getView('%s') != null)", viewName)).getBody();
		return Boolean.parseBoolean(response.trim());
	}

	public void createView(String viewName) throws JenkinsServerException {
		if (checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_ALREADY_EXISTS"), viewName));
		}
		
		executeScript(String.format("Jenkins.instance.addView(new ListView('%s'))", viewName));

		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("ERROR_CREATING_VIEW"), viewName));
		}
	}

	public Job getJobByName(String jobName) throws JenkinsServerException {
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}
		
		return new Job(jobName);
	}

	public boolean checkJobExists(String jobName) throws JenkinsServerException {
		String response = executeScript(String.format("println(Jenkins.instance.getItem('%s') != null)", jobName)).getBody();
		return Boolean.parseBoolean(response.trim());
	}

	public void createJob(String jobName) throws JenkinsServerException {
		if (checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_ALREADY_EXISTS"), jobName));
		}
		
		try {
			String jobXML = xStream.toXML(new Job(jobName));
			jenkinsClient.postXML(Constants.URL_CREATE_JOB, new ImmutablePair<String, String>(NAME, jobName), jobXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}

		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("ERROR_CREATING_JOB"), jobName));
		}
	}
	
	public void addJobToView(String viewName, String jobName) throws JenkinsServerException {
		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
		}
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}
		
		try {
			executeScript(String.format("Jenkins.instance.getView('%s').add(Jenkins.instance.getItem('%s'))", viewName, jobName));
		} catch(JenkinsServerException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	public void addUserToProjectMatrix(String jobName, String username) throws JenkinsServerException {
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		try {
			String importHudsonSecurity = ConfigurationUtil.getConfiguration("IMPORT_HUDSON_SECURITY");
			String getUser = String.format(ConfigurationUtil.getConfiguration("JENKINS_GET_USER"), username);
			String defAuthorizationMatrixProperty = String.format(ConfigurationUtil.getConfiguration("JENKINS_DEF_PROPERTY_NAME"), ConfigurationUtil.getConfiguration("JENKINS_AUTHORIZATION_MATRIX_PROPERTY"));
			String jenkinsInstance = ConfigurationUtil.getConfiguration("JENKINS_INSTANCE");
			String job = String.format(ConfigurationUtil.getConfiguration("JENKINS_GET_ITEM"), jobName);
			String ifPropertyEqualsNull = ConfigurationUtil.getConfiguration("JENKINS_IF_PROPERTY_NULL");
			String addAuthorizationMatrixProperty = ConfigurationUtil.getConfiguration("JENKINS_ADD_AUTHORIZATION_MATRIX_PROPERTY");
			String closeSlash = ConfigurationUtil.getConfiguration("CLOSE_SLASH");
			String authorizationMatrixProperty = ConfigurationUtil.getConfiguration("JENKINS_GET_AUTHORIZATION_MATRIX_PROPERTY");
			String addPropertyBuild = String.format(ConfigurationUtil.getConfiguration("JENKINS_ADD_PROPERTY_ITEM_BUILD"), username);
			String addPropertyDiscover = String.format(ConfigurationUtil.getConfiguration("JENKINS_ADD_PROPERTY_ITEM_DISCOVER"), username);
			String addPropertyRead = String.format(ConfigurationUtil.getConfiguration("JENKINS_ADD_PROPERTY_ITEM_READ"), username);
			String jobSave = ConfigurationUtil.getConfiguration("JOB_SAVE");
			
			executeScript(createString(importHudsonSecurity, getUser, defAuthorizationMatrixProperty, jenkinsInstance, job, ifPropertyEqualsNull,
					addAuthorizationMatrixProperty, closeSlash, authorizationMatrixProperty, addPropertyBuild, addPropertyDiscover, addPropertyRead, jobSave));
		} catch(JenkinsServerException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	public HttpResponse<String> executeScript(String script) throws JenkinsServerException {
		try {
			return jenkinsClient.postURLEncoded(Constants.URL_SCRIPT_TEXT, ConfigurationUtil.getConfiguration("SCRIPT") + script);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	private String createString(String... strings) {
		StringBuilder strBuilder = new StringBuilder();
		for (String str : strings) {
			strBuilder.append(str).append("\n");
		}
		return strBuilder.toString();
	}
}
