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
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsServer {

	private static final String FALSE = ConfigurationUtil.getConfiguration("FALSE");
	
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

	public String setSecurityRealm(SecurityRealm securityRealm) throws JenkinsServerException {
		String security = securityRealm.getGroovyScript();
		String jenkinsInstance = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_INSTANCE");
		String setSecurityRealm = ConfigurationUtil.getConfiguration("GROOVY_SET_SECURITY_REALM");
		String jenkinsSave = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_SAVE");

		return executeScript(concatenateStrings(security, jenkinsInstance, setSecurityRealm, jenkinsSave));
	}

	public String setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy) throws JenkinsServerException {
		String response = executeScript(ConfigurationUtil.getConfiguration("GROOVY_IS_USE_SECURITY"));
		if (response.trim().equals(FALSE)) {
			throw new JenkinsServerException(ConfigurationUtil.getConfiguration("SECURITY_REALM_IS_NOT_CONFIGURED"));
		}
		
		String authorization = authorizationStrategy.getGroovyScript();
		String jenkinsInstance = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_INSTANCE");
		String setAuthorizationStrategy = ConfigurationUtil.getConfiguration("GROOVY_SET_AUTHORIZATION_STRATEGY");
		String jenkinsSave = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_SAVE");

		return executeScript(concatenateStrings(authorization, jenkinsInstance, setAuthorizationStrategy, jenkinsSave));
	}

	public String getVersion() throws JenkinsServerException {
		return executeScript(ConfigurationUtil.getConfiguration("GROOVY_GET_VERSION"));
	}

	public ListView getViewByName(String viewName) throws JenkinsServerException {
		try {
			String url = String.format(ConfigurationUtil.getConfiguration("URL_GET_VIEW"), viewName);
			HttpResponse<String> httpResponse = jenkinsClient.get(url);

			if (httpResponse.getStatus() == HttpStatus.SC_NOT_FOUND) {
				throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
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
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_ALREADY_EXISTS"), viewName));
		}

		executeScript(String.format(ConfigurationUtil.getConfiguration("GROOVY_CREATE_LIST_VIEW"), viewName));

		if (!checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("ERROR_CREATING_VIEW"), viewName));
		}
	}

	public Job getJobByName(String jobName) throws JenkinsServerException {
		try {
			String url = String.format(ConfigurationUtil.getConfiguration("URL_GET_JOB"), jobName);
			HttpResponse<String> httpResponse = jenkinsClient.get(url);

			if (httpResponse.getStatus() == HttpStatus.SC_NOT_FOUND) {
				throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
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
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_ALREADY_EXISTS"), jobName));
		}

		executeScript(String.format(ConfigurationUtil.getConfiguration("GROOVY_CREATE_FREE_STYLE_PROJECT"), jobName));

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
			executeScript(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_JOB_TO_VIEW"), viewName, jobName));
		} catch (JenkinsServerException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public void addUserToProjectMatrix(String jobName, String username, List<Permission> permissions) throws JenkinsServerException {
		if (!checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}
		
		String checkAuthorizationStrategy = ConfigurationUtil.getConfiguration("GROOVY_IS_AUTHORIZATION_STRATEGY_EQUALS_PROJECT_MATRIX");
		String response = executeScript(checkAuthorizationStrategy);
		if (response.trim().equals(FALSE)) {
			throw new JenkinsServerException(ConfigurationUtil.getConfiguration("AUTHORIZATION_STRATEGY_ERROR"));
		}

		try {
			String propertyName = ConfigurationUtil.getConfiguration("GROOVY_DEF_AUTHORIZATION_MATRIX_PROPERTY");
			String instance = ConfigurationUtil.getConfiguration("GROOVY_JENKINS_INSTANCE");
			String job = String.format(ConfigurationUtil.getConfiguration("GROOVY_GET_ITEM"), jobName);
			String addAuthorizationMatrixProperty = ConfigurationUtil.getConfiguration("GROOVY_ADD_AUTHORIZATION_MATRIX_PROPERTY");
			String property = ConfigurationUtil.getConfiguration("GROOVY_GET_PROPERTY");
			String jobSave = ConfigurationUtil.getConfiguration("GROOVY_JOB_SAVE");
			
			StringBuilder sbAddProperties = new StringBuilder();
			for (Permission permission : permissions) {
				sbAddProperties.append(String.format(ConfigurationUtil.getConfiguration("GROOVY_ADD_PERMISSION_TO_PROPERTY"), permission.getValue(), username));
			}

			executeScript(concatenateStrings(propertyName, instance, job, addAuthorizationMatrixProperty, property, sbAddProperties.toString(), jobSave));
		} catch (JenkinsServerException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	private String executeScript(String script) throws JenkinsServerException {
		try {
			String postScript = String.format(ConfigurationUtil.getConfiguration("SCRIPT"), script);
			HttpResponse<String> response = jenkinsClient.postURLEncoded(ConfigurationUtil.getConfiguration("URL_SCRIPT_TEXT"), postScript);
			
			if (response.getStatus() == HttpStatus.SC_FORBIDDEN) {
				throw new JenkinsServerException(ConfigurationUtil.getConfiguration("FORBIDDEN_ERROR"));
			}
			
			return response.getBody();
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	private String concatenateStrings(String... strings) {
		StringBuilder strBuilder = new StringBuilder();
		for (String str : strings) {
			strBuilder.append(str);
		}
		return strBuilder.toString();
	}
}
