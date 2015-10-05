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

	private static final String API_JSON = ConfigurationUtil.getConfiguration("API_JSON");

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
		String importHudsonSecurity = "import hudson.security.*;";
		String security = securityRealm.getGroovyScript();
		String script = String.format("%s%ndef instance = Jenkins.getInstance();%ninstance.setSecurityRealm(%s);%ninstance.save();", importHudsonSecurity, security);
		return executeScript(script);
	}
	
	public HttpResponse<String> setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy) throws JenkinsServerException {
		String importHudsonSecurity = "import hudson.security.*;";
		String strategy = authorizationStrategy.getGroovyScript();
		String script = String.format("%s%n%s%ndef instance = Jenkins.getInstance();%ninstance.setAuthorizationStrategy(strategy);%ninstance.save();", importHudsonSecurity, strategy);
		return executeScript(script);
	}

	public String getVersion() throws JenkinsServerException {
		try {
			HttpResponse<String> response = jenkinsClient.get("/");
			return response.getHeaders().getFirst(ConfigurationUtil.getConfiguration("X_JENKINS"));
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> getViewByName(String name)
			throws JenkinsServerException {
		return getByName(ItemType.VIEW, name);
	}

	public boolean checkViewExists(String name)
			throws JenkinsServerException {
		return checkResponseStatus(getViewByName(name).getStatus(), HttpStatus.SC_OK);
	}

	public HttpResponse<String> createView(String name)
			throws JenkinsServerException {
		if (checkViewExists(name)) {
			throw new JenkinsServerException(
					String.format("View %s already exists", name));
		}
		
		try {
			String viewXML = xStream.toXML(new ListView(name));

			return jenkinsClient.postXML(Constants.URL_CREATE_VIEW,
					new ImmutablePair<String, String>("name", name), viewXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> getJobByName(String name)
			throws JenkinsServerException {
		return getByName(ItemType.JOB, name);
	}

	public boolean checkJobExists(String name)
			throws JenkinsServerException {
		return checkResponseStatus(getJobByName(name).getStatus(), HttpStatus.SC_OK);
	}

	public HttpResponse<String> createJob(String name)
			throws JenkinsServerException {
		if (checkJobExists(name)) {
			throw new JenkinsServerException(
					String.format("Job %s already exists", name));
		}
		
		try {
			String jobXML = xStream.toXML(new Job());

			return jenkinsClient.postXML(Constants.URL_CREATE_JOB,
					new ImmutablePair<String, String>("name", name), jobXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> deleteJob(String name)
			throws JenkinsServerException {
		try {
			return jenkinsClient.postXML("/job/" + name + Constants.URL_DO_DELETE);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	public HttpResponse<String> executeScript(String script)
			throws JenkinsServerException {
		try {
			return jenkinsClient.postURLEncoded(Constants.URL_SCRIPT_TEXT, "script=" + script);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	private boolean checkResponseStatus(int response, int status) {
		if (response == status) {
			return true;
		}
		
		return false;
	}

	private HttpResponse<String> getByName(ItemType type, String name)
			throws JenkinsServerException {
		try {
			return jenkinsClient.get("/" + type.getValue() + "/" + name + API_JSON, new ImmutablePair<String, String>("tree", "name"));
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
}
