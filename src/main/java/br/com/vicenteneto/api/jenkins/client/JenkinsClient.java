package br.com.vicenteneto.api.jenkins.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.MediaType;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsClient {

	private URI serverURI;
	private String username;
	private String password;

	public JenkinsClient(URI serverURI) {
		this.serverURI = serverURI;
	}

	public JenkinsClient(URI serverURI, String username, String password) {
		this.serverURI = serverURI;
		this.username = username;
		this.password = password;
	}

	public HttpResponse<String> get(String path) throws JenkinsClientException {
		return get(createURI(path));
	}

	public HttpResponse<String> get(String path, ImmutablePair<String, String> parameter)
			throws JenkinsClientException {
		return get(createURI(path, parameter));
	}

	public HttpResponse<String> postXML(String path) throws JenkinsClientException {
		return post(createURI(path), "");
	}

	public HttpResponse<String> postXML(String path, String requestBody) throws JenkinsClientException {
		return post(createURI(path), requestBody);
	}

	public HttpResponse<String> postXML(String path, ImmutablePair<String, String> parameter, String requestBody)
			throws JenkinsClientException {
		return post(createURI(path, parameter), requestBody);
	}

	private HttpResponse<String> get(URIBuilder uriBuilder) throws JenkinsClientException {
		try {
			return Unirest.get(uriBuilder.build().toString())
					.headers(createHeaders(MediaType.APPLICATION_XML_VALUE))
					.asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	private HttpResponse<String> post(URIBuilder uriBuilder, String requestBody) throws JenkinsClientException {
		try {
			return Unirest.post(uriBuilder.build().toString())
					.headers(createHeaders(MediaType.APPLICATION_XML_VALUE))
					.body(requestBody)
					.asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	public HttpResponse<String> postURLEncoded(String path, String requestBody) throws JenkinsClientException {
		try {
			return Unirest.post(createURI(path).build().toString())
					.headers(createHeaders(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
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

	private URIBuilder createURI(String path, ImmutablePair<String, String> parameter) {
		URIBuilder uriBuilder = createURI(path);
		uriBuilder.addParameter(parameter.getKey(), parameter.getValue());

		return uriBuilder;
	}

	private Map<String, String> createHeaders(String contentTypeValue) {
		String contentType = ConfigurationUtil.getConfiguration("CONTENT_TYPE");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(contentType, contentTypeValue);
		
		if (StringUtils.isNotEmpty(username)) {
			String authorization = ConfigurationUtil.getConfiguration("AUTHORIZATION");
			String authValue = "Basic " + new String(Base64.encodeBase64(String.format("%s:%s", username, password).getBytes()));
			
			headers.put(authorization, authValue);
		}

		return headers;
	}
}
