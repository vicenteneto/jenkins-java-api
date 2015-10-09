package br.com.vicenteneto.api.jenkins.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.MediaType;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsClient {

	private static final String EMPTY_STRING = "";

	private URI serverURI;
	private String username;
	private String password;

	public JenkinsClient(URI serverURI) {
		this.serverURI = serverURI;
	}

	public JenkinsClient(URI serverURI, String username, String password) {
		this(serverURI);
		this.username = username;
		this.password = password;
	}

	public HttpResponse<String> get(String path) throws JenkinsClientException {
		return get(createURI(path));
	}

	public HttpResponse<String> postXML(String path) throws JenkinsClientException {
		return post(createURI(path), EMPTY_STRING, MediaType.APPLICATION_XML_VALUE);
	}

	public HttpResponse<String> postXML(String path, String requestBody) throws JenkinsClientException {
		return post(createURI(path), requestBody, MediaType.APPLICATION_XML_VALUE);
	}

	public HttpResponse<String> postURLEncoded(String path, String requestBody) throws JenkinsClientException {
		return post(createURI(path), requestBody, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	}

	private HttpResponse<String> get(URIBuilder uriBuilder) throws JenkinsClientException {
		try {
			return Unirest.get(uriBuilder.build().toString())
					.basicAuth(username, password)
					.header(ConfigurationUtil.getConfiguration("CONTENT_TYPE"), MediaType.APPLICATION_XML_VALUE)
					.asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	private HttpResponse<String> post(URIBuilder uriBuilder, String requestBody, String mediaType) throws JenkinsClientException {
		try {
			return Unirest.post(uriBuilder.build().toString())
					.basicAuth(username, password)
					.header(ConfigurationUtil.getConfiguration("CONTENT_TYPE"), mediaType)
					.body(requestBody)
					.asString();
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
}
