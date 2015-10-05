package br.com.vicenteneto.api.jenkins;

import java.net.URI;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpStatus;

import com.mashape.unirest.http.HttpResponse;
import com.thoughtworks.xstream.XStream;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.ItemType;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;
import br.com.vicenteneto.api.jenkins.util.Constants;

public class JenkinsServer {

	private static final String SLASH = ConfigurationUtil.getConfiguration("SLASH");
	private static final String API_JSON = ConfigurationUtil.getConfiguration("API_JSON");
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
		try {
			HttpResponse<String> response = jenkinsClient.get(SLASH);
			return response.getHeaders().getFirst(ConfigurationUtil.getConfiguration("X_JENKINS"));
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> getViewByName(String name) throws JenkinsServerException {
		return getByName(ItemType.VIEW, name);
	}

	public boolean checkViewExists(String name) throws JenkinsServerException {
		return getViewByName(name).getStatus() == HttpStatus.SC_OK;
	}

	public HttpResponse<String> createView(String name) throws JenkinsServerException {
		if (checkViewExists(name)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_ALREADY_EXISTS"), name));
		}
		
		try {
			String viewXML = xStream.toXML(new ListView(name));

			return jenkinsClient.postXML(Constants.URL_CREATE_VIEW, new ImmutablePair<String, String>(NAME, name), viewXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> getJobByName(String name) throws JenkinsServerException {
		return getByName(ItemType.JOB, name);
	}

	public boolean checkJobExists(String name) throws JenkinsServerException {
		return getJobByName(name).getStatus() == HttpStatus.SC_OK;
	}

	public HttpResponse<String> createJob(String name)
			throws JenkinsServerException {
		if (checkJobExists(name)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_ALREADY_EXISTS"), name));
		}
		
		try {
			String jobXML = xStream.toXML(new Job());

			return jenkinsClient.postXML(Constants.URL_CREATE_JOB, new ImmutablePair<String, String>(NAME, name), jobXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> deleteJob(String name)
			throws JenkinsServerException {
		try {
			return jenkinsClient.postXML(SLASH + ItemType.JOB.getValue() + SLASH + name + Constants.URL_DO_DELETE);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	public HttpResponse<String> addJobToView(String viewName, String jobName) throws JenkinsServerException {
		if (checkViewExists(viewName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("VIEW_DOES_NOT_EXISTS"), viewName));
		}
		if (checkJobExists(jobName)) {
			throw new JenkinsServerException(String.format(ConfigurationUtil.getConfiguration("JOB_DOES_NOT_EXISTS"), jobName));
		}
		try {
			createJob(jobName);
			return executeScript(String.format("Jenkins.getInstance().getView('%s').add(Jenkins.getInstance().getItem('%s'));", viewName, jobName));
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

	private HttpResponse<String> getByName(ItemType type, String name) throws JenkinsServerException {
		try {
			return jenkinsClient.get(SLASH + type.getValue() + SLASH + name + API_JSON, new ImmutablePair<String, String>("tree", NAME));
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	private String createString(String... strings) {
		StringBuilder strBuilder = new StringBuilder();
		for (String str : strings) {
			strBuilder.append(str);
		}
		return strBuilder.toString();
	}
}
