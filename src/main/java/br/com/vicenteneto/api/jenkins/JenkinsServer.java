package br.com.vicenteneto.api.jenkins;

import java.net.URI;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpStatus;

import com.mashape.unirest.http.HttpResponse;
import com.thoughtworks.xstream.XStream;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsServer {

	private static final String API_JSON = ConfigurationUtil.getConfiguration("API_JSON");

	private static final String VIEW = "view";
	private static final String JOB = "job";

	private JenkinsClient jenkinsClient;
	private XStream xStream;

	public JenkinsServer(URI serverURI) {
		jenkinsClient = new JenkinsClient(serverURI);
		
		xStream = new XStream();
		xStream.autodetectAnnotations(true);
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
		return getByName(VIEW, name);
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

			return jenkinsClient.post_xml("/createView",
					new ImmutablePair<String, String>("name", name), viewXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}

	public HttpResponse<String> getJobByName(String name)
			throws JenkinsServerException {
		return getByName(JOB, name);
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

			return jenkinsClient.post_xml("/createItem",
					new ImmutablePair<String, String>("name", name), jobXML);
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
	
	public HttpResponse<String> executeScript(String script)
			throws JenkinsServerException {
		try {
			return jenkinsClient.post_url_encoded("/scriptText", "script=" + script);
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

	private HttpResponse<String> getByName(String type, String name)
			throws JenkinsServerException {
		try {
			return jenkinsClient.get("/" + type + "/" + name + API_JSON, new ImmutablePair<String, String>("tree", "name"));
		} catch (JenkinsClientException exception) {
			throw new JenkinsServerException(exception);
		}
	}
}
