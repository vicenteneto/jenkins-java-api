package br.com.vicenteneto.api.jenkins;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.Plugin;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.GroovyUtil;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsServer {

	private static final String FALSE = "false";

	private JenkinsClient jenkinsClient;
	private Gson gson;

	private JenkinsServer() {
		gson = new Gson();
	}

	public JenkinsServer(URI serverURI) {
		this();
		jenkinsClient = new JenkinsClient(serverURI);
	}

	public JenkinsServer(URI serverURI, String username, String password) {
		this();
		jenkinsClient = new JenkinsClient(serverURI, username, password);
	}

	public String getVersion() throws JenkinsServerException {
		return GroovyUtil.executeScript(jenkinsClient, ConfigurationUtil.getConfiguration("GROOVY_GET_VERSION"));
	}

	public void setSecurityRealm(SecurityRealm securityRealm) throws JenkinsServerException {

		String security = GroovyUtil.generateGroovyScript(securityRealm);
		String setSecurityRealm = ConfigurationUtil.getConfiguration("GROOVY_SET_SECURITY_REALM");
		String jenkinsSave = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_SAVE");

		GroovyUtil.executeScript(jenkinsClient, GroovyUtil.concatenateStrings(security, setSecurityRealm, jenkinsSave));
	}

	public void setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy) throws JenkinsServerException {

		String response = GroovyUtil.executeScript(jenkinsClient,
				ConfigurationUtil.getConfiguration("GROOVY_IS_USE_SECURITY"));
		if (response.trim().equals(FALSE)) {
			throw new JenkinsServerException(ConfigurationUtil.getConfiguration("SECURITY_REALM_IS_NOT_CONFIGURED"));
		}

		String authorization = GroovyUtil.generateGroovyScript(authorizationStrategy);
		String setAuthorizationStrategy = ConfigurationUtil.getConfiguration("GROOVY_SET_AUTHORIZATION_STRATEGY");
		String jenkinsSave = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_SAVE");

		GroovyUtil.executeScript(jenkinsClient,
				GroovyUtil.concatenateStrings(authorization, setAuthorizationStrategy, jenkinsSave));
	}

	// Port - 0 to indicate random available TCP port. -1 to disable this
	// service.
	public void setSlaveAgentPort(int port) throws JenkinsServerException {

		String setSlaveAgentPort = String.format(ConfigurationUtil.getConfiguration("GROOVY_SET_SLAVE_AGENT_PORT"),
				port);
		String jenkinsSave = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_SAVE");

		GroovyUtil.executeScript(jenkinsClient, GroovyUtil.concatenateStrings(setSlaveAgentPort, jenkinsSave));
	}

	public Plugin getPluginByName(String pluginName) throws JenkinsServerException {

		try {
			String url = String.format(ConfigurationUtil.getConfiguration("URL_GET_PLUGIN"), pluginName);
			HttpResponse<String> httpResponse = jenkinsClient.get(url);

			if (httpResponse.getStatus() == HttpStatus.SC_NOT_FOUND) {
				throw new JenkinsServerException(
						String.format(ConfigurationUtil.getConfiguration("PLUGIN_NOT_FOUND"), pluginName));
			}

			return gson.fromJson(httpResponse.getBody(), Plugin.class);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public boolean checkPluginExists(String pluginName) {

		try {
			getPluginByName(pluginName);
			return true;
		} catch (JenkinsServerException exception) {
			return false;
		}
	}

	public void installPluginByName(String pluginName, boolean dynamicLoad) throws JenkinsServerException {

		if (!checkPluginExists(pluginName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("PLUGIN_NOT_FOUND"), pluginName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_DEPLOY_PLUGIN"), pluginName, dynamicLoad));
	}

	public void updateAllInstalledPlugins(boolean dynamicLoad) throws JenkinsServerException {

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_UPDATE_PLUGINS"), dynamicLoad));
	}

	public ListView getViewByName(String viewName) throws JenkinsServerException {

		try {
			String url = String.format(ConfigurationUtil.getConfiguration("URL_GET_VIEW"), viewName);
			HttpResponse<String> httpResponse = jenkinsClient.get(url);

			if (httpResponse.getStatus() == HttpStatus.SC_NOT_FOUND) {
				throw new JenkinsServerException(
						String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
			}

			return gson.fromJson(httpResponse.getBody(), ListView.class);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public boolean checkViewExists(String viewName) {

		try {
			getViewByName(viewName);
			return true;
		} catch (JenkinsServerException e) {
			return false;
		}
	}

	public void createView(String viewName) throws JenkinsServerException {

		if (checkViewExists(viewName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("VIEW_ALREADY_EXISTS"), viewName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_CREATE_LIST_VIEW"), viewName));

		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("ERROR_CREATING_VIEW"), viewName));
		}
	}

	public void createView(String viewName, String description) throws JenkinsServerException {

		createView(viewName);

		String view = String.format(ConfigurationUtil.getConfiguration("GROOVY_GET_VIEW"), viewName);
		String setViewDescription = String.format(ConfigurationUtil.getConfiguration("GROOVY_SET_VIEW_DESCRIPTION"),
				description);
		String viewSave = ConfigurationUtil.getConfiguration("GROOVY_VIEW_SAVE");

		GroovyUtil.executeScript(jenkinsClient, GroovyUtil.concatenateStrings(view, setViewDescription, viewSave));
	}

	public void deleteView(String viewName) throws JenkinsServerException {

		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_DELETE_VIEW"), viewName));

		if (checkViewExists(viewName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("ERROR_DELETING_VIEW"), viewName));
		}
	}

	public Job getJobByName(String jobName) throws JenkinsServerException {

		try {
			String url = String.format(ConfigurationUtil.getConfiguration("URL_GET_JOB"), jobName);
			HttpResponse<String> httpResponse = jenkinsClient.get(url);

			if (httpResponse.getStatus() == HttpStatus.SC_NOT_FOUND) {
				throw new JenkinsServerException(
						String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
			}

			return gson.fromJson(httpResponse.getBody(), Job.class);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public boolean checkJobExists(String jobName) {

		try {
			getJobByName(jobName);
			return true;
		} catch (JenkinsServerException e) {
			return false;
		}
	}

	public void createJob(String jobName) throws JenkinsServerException {

		if (checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_ALREADY_EXISTS"), jobName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_CREATE_FREE_STYLE_PROJECT"), jobName));

		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("ERROR_CREATING_JOB"), jobName));
		}
	}

	public void deleteJob(String jobName) throws JenkinsServerException {

		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_DELETE_ITEM"), jobName));

		if (checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("ERROR_DELETING_JOB"), jobName));
		}
	}

	public void addJobToView(String viewName, String jobName) throws JenkinsServerException {

		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
		}
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_JOB_TO_VIEW"), viewName, jobName));
	}

	public void executeJob(String jobName) throws JenkinsServerException {
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		GroovyUtil.executeScript(jenkinsClient,
				String.format(ConfigurationUtil.getConfiguration("GROOVY_RUN_JOB"), jobName));
	}

	public void addUserToProjectMatrix(String jobName, String username, List<Permission> permissions)
			throws JenkinsServerException {

		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		String checkAuthorizationStrategy = ConfigurationUtil
				.getConfiguration("GROOVY_IS_AUTHORIZATION_STRATEGY_EQUALS_PROJECT_MATRIX");
		String response = GroovyUtil.executeScript(jenkinsClient, checkAuthorizationStrategy);
		if (response.trim().equals(FALSE)) {
			throw new JenkinsServerException(ConfigurationUtil.getConfiguration("AUTHORIZATION_STRATEGY_ERROR"));
		}

		String propertyName = ConfigurationUtil.getConfiguration("GROOVY_DEF_AUTHORIZATION_MATRIX_PROPERTY");
		String job = String.format(ConfigurationUtil.getConfiguration("GROOVY_GET_ITEM"), jobName);
		String addAuthorizationMatrixProperty = ConfigurationUtil
				.getConfiguration("GROOVY_ADD_AUTHORIZATION_MATRIX_PROPERTY");
		String property = ConfigurationUtil.getConfiguration("GROOVY_GET_PROPERTY");
		String jobSave = ConfigurationUtil.getConfiguration("GROOVY_JOB_SAVE");

		StringBuilder sbAddProperties = new StringBuilder();
		for (Permission permission : permissions) {
			sbAddProperties
					.append(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_PERMISSION_TO_PROPERTY"),
							permission.getValue(), username));
		}

		GroovyUtil.executeScript(jenkinsClient, GroovyUtil.concatenateStrings(propertyName, job,
				addAuthorizationMatrixProperty, property, sbAddProperties.toString(), jobSave));
	}

	public void removeUserFromProjectMatrix(String jobName, String username) throws JenkinsServerException {

		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(
					String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}

		String checkAuthorizationStrategy = ConfigurationUtil
				.getConfiguration("GROOVY_IS_AUTHORIZATION_STRATEGY_EQUALS_PROJECT_MATRIX");
		String response = GroovyUtil.executeScript(jenkinsClient, checkAuthorizationStrategy);
		if (response.trim().equals(FALSE)) {
			throw new JenkinsServerException(ConfigurationUtil.getConfiguration("AUTHORIZATION_STRATEGY_ERROR"));
		}

		String propertyName = ConfigurationUtil.getConfiguration("GROOVY_DEF_AUTHORIZATION_MATRIX_PROPERTY");
		String job = String.format(ConfigurationUtil.getConfiguration("GROOVY_GET_ITEM"), jobName);
		String addAuthorizationMatrixProperty = ConfigurationUtil
				.getConfiguration("GROOVY_ADD_AUTHORIZATION_MATRIX_PROPERTY");
		String property = ConfigurationUtil.getConfiguration("GROOVY_GET_PROPERTY");
		String removeUserPermissions = String
				.format(ConfigurationUtil.getConfiguration("GROOVY_REMOVE_USER_FROM_GRANTED_PERMISSIONS"), username);
		String jobSave = ConfigurationUtil.getConfiguration("GROOVY_JOB_SAVE");

		GroovyUtil.executeScript(jenkinsClient, GroovyUtil.concatenateStrings(propertyName, job,
				addAuthorizationMatrixProperty, property, removeUserPermissions, jobSave));
	}
}
