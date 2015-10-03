package br.com.vicenteneto.api.jenkins.tests;

import java.net.URI;

import org.junit.Assert;
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

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.JenkinsServer;
import br.com.vicenteneto.api.jenkins.client.JenkinsClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JenkinsServer.class)
public class JenkinsServerTest {
	
	@InjectMocks
	private JenkinsServer JenkinsServer;
	
	@Mock
	private JenkinsClient jenkinsClientMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		PowerMockito.whenNew(JenkinsClient.class).withAnyArguments()
				.thenReturn(jenkinsClientMock);
	}
	
	@Test
	public void constructorTest() throws Exception {
		new JenkinsServer(new URI("http://localhost"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getVersionTest() throws Exception {
		HttpResponse<String> responseMock = Mockito.mock(HttpResponse.class);
		Headers headersMock = Mockito.mock(Headers.class);
		String version = "0.0.0";
		
		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(responseMock);
		Mockito.when(responseMock.getHeaders())
				.thenReturn(headersMock);
		Mockito.when(headersMock.getFirst(Mockito.anyString()))
				.thenReturn(version);
		
		String response = JenkinsServer.getVersion();
		Assert.assertEquals(response, version);
	}
}
