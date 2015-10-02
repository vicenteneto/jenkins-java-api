package br.com.vicenteneto.api.jenkins.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.client.utils.URIBuilder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsClient {

	private URI serverURI;

	public JenkinsClient(URI serverURI) {
		this.serverURI = serverURI;
	}

	public HttpResponse<String> get(String path)
			throws JenkinsClientException {
		return get(createURI(path));
	}

	public HttpResponse<String> get(String path, ImmutablePair<String, String> parameter)
			throws JenkinsClientException {
		return get(createURI(path, parameter));
	}

	public HttpResponse<String> post_xml(String path, String requestBody)
			throws JenkinsClientException {
		return post(createURI(path), requestBody);
	}

	public HttpResponse<String> post_xml(String path, ImmutablePair<String, String> parameter, String requestBody)
			throws JenkinsClientException {
		return post(createURI(path, parameter), requestBody);
	}

	private HttpResponse<String> get(URIBuilder uriBuilder)
			throws JenkinsClientException {
		try {
			return Unirest.get(uriBuilder.build().toString())
					.headers(getHeaders())
					.asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	private HttpResponse<String> post(URIBuilder uriBuilder, String requestBody)
			throws JenkinsClientException {
		try {
			return Unirest.post(uriBuilder.build().toString())
					.headers(getHeaders())
					.body(requestBody).asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	private URIBuilder createDefaultURI() {
		return new URIBuilder(serverURI);
	}

	private URIBuilder createURI(String path) {
		return createDefaultURI().setPath(path);
	}

	private URIBuilder createURI(String path, ImmutablePair<String, String> parameter) {
		URIBuilder uriBuilder = createURI(path);
		uriBuilder.addParameter(parameter.getKey(), parameter.getValue());

		return uriBuilder;
	}
	
	private Map<String, String> getHeaders() {
		String contentType = ConfigurationUtil.getConfiguration("CONTENT_TYPE");
		String applicationXML = ConfigurationUtil.getConfiguration("APPLICATION_XML");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(contentType, applicationXML);
		
		return headers;
	}
}
