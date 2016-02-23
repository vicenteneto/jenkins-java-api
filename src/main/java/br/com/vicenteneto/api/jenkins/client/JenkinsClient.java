package br.com.vicenteneto.api.jenkins.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

public class JenkinsClient {

	private static final String EMPTY_STRING = "";
	private static final String DEPTH_PARAM = "depth";
	private static final String DEPTH_VALUE = "100";

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

	public HttpResponse<String> getDepth(String path) throws JenkinsClientException {
		URIBuilder uri = createURI(path);
		uri.addParameter(DEPTH_PARAM, DEPTH_VALUE);

		return get(uri);
	}

	public HttpResponse<String> getDepthByURL(String url) throws JenkinsClientException {
		try {
			URIBuilder uri = new URIBuilder(url);
			uri.addParameter(DEPTH_PARAM, DEPTH_VALUE);

			return get(uri);
		} catch (URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
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
			GetRequest getRequest = Unirest.get(uriBuilder.build().toString());

			if (StringUtils.hasText(username)) {
				getRequest.basicAuth(username, password);
			}

			return getRequest.header(ConfigurationUtil.getConfiguration("CONTENT_TYPE"), MediaType.APPLICATION_XML_VALUE)
					.asString();
		} catch (UnirestException | URISyntaxException exception) {
			throw new JenkinsClientException(exception);
		}
	}

	private HttpResponse<String> post(URIBuilder uriBuilder, String requestBody, String mediaType)
			throws JenkinsClientException {
		try {
			HttpRequestWithBody post = Unirest.post(uriBuilder.build().toString());

			if (StringUtils.hasText(username)) {
				post.basicAuth(username, password);
			}

			return post.header(ConfigurationUtil.getConfiguration("CONTENT_TYPE"), mediaType)
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
