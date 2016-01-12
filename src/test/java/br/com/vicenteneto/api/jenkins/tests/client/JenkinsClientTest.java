package br.com.vicenteneto.api.jenkins.tests.client;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.StringUtils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JenkinsClient.class, Unirest.class, StringUtils.class })
public class JenkinsClientTest {

	private static final String HOST = "http://localhost/path";
	private static final String PATH = "/path";
	private static final String MESSAGE = "Message";
	private static final String REQUEST_BODY = "Body";

	@InjectMocks
	private JenkinsClient jenkinsClient;

	@Mock
	private GetRequest getRequestMock;

	@Mock
	private HttpRequestWithBody httpRequestWithBodyMock;

	@Mock
	private RequestBodyEntity requestBodyEntity;

	@Before
	public void setUp() throws Exception {

		URIBuilder uriBuilderMock = PowerMockito.mock(URIBuilder.class);

		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(Unirest.class);
		PowerMockito.mockStatic(StringUtils.class);
		PowerMockito.whenNew(URIBuilder.class).withAnyArguments()
				.thenReturn(uriBuilderMock);

		Mockito.when(uriBuilderMock.setPath(Mockito.anyString()))
				.thenReturn(uriBuilderMock);
		Mockito.when(uriBuilderMock.build())
				.thenReturn(new URI(HOST));

		PowerMockito.when(Unirest.get(Mockito.anyString()))
				.thenReturn(getRequestMock);
		PowerMockito.when(Unirest.post(Mockito.anyString()))
				.thenReturn(httpRequestWithBodyMock);
	}

	@Test(expected = JenkinsClientException.class)
	public void getThrowsJenkinsClientExceptionTest() throws Exception {

		mockGetRequestHeader();

		PowerMockito.when(StringUtils.hasText(Mockito.anyString()))
				.thenReturn(true);
		Mockito.when(getRequestMock.basicAuth(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(getRequestMock);
		Mockito.when(getRequestMock.asString())
				.thenThrow(new UnirestException(MESSAGE));

		jenkinsClient.get(PATH);
	}

	@Test
	public void getTest() throws Exception {

		mockGetRequestHeader();

		Mockito.when(getRequestMock.asString())
				.thenReturn(null);

		jenkinsClient.get(PATH);
	}

	@Test
	public void getDepthTest() throws Exception {

		mockGetRequestHeader();

		Mockito.when(getRequestMock.asString())
				.thenReturn(null);

		jenkinsClient.getDepth(PATH);
	}

	@Test(expected = JenkinsClientException.class)
	public void postEmptyXMLTest() throws Exception {

		mockPostHeaderAndBody();

		Mockito.when(requestBodyEntity.asString())
				.thenThrow(new UnirestException(MESSAGE));

		jenkinsClient.postXML(PATH);
	}

	@Test(expected = JenkinsClientException.class)
	public void postXMLTest() throws Exception {

		mockPostHeaderAndBody();

		Mockito.when(requestBodyEntity.asString())
				.thenThrow(new UnirestException(MESSAGE));

		jenkinsClient.postXML(PATH, REQUEST_BODY);
	}

	@Test
	public void postURLEncodedTest() throws Exception {

		mockPostHeaderAndBody();

		PowerMockito.when(StringUtils.hasText(Mockito.anyString()))
				.thenReturn(true);
		Mockito.when(httpRequestWithBodyMock.basicAuth(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(httpRequestWithBodyMock);
		Mockito.when(requestBodyEntity.asString())
				.thenReturn(null);

		jenkinsClient.postURLEncoded(PATH, REQUEST_BODY);
	}

	private void mockGetRequestHeader() {
		Mockito.when(getRequestMock.header(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(getRequestMock);
	}

	private void mockPostHeaderAndBody() {
		Mockito.when(httpRequestWithBodyMock.header(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(httpRequestWithBodyMock);
		Mockito.when(httpRequestWithBodyMock.body(Mockito.anyString()))
				.thenReturn(requestBodyEntity);
	}
}
