package br.com.vicenteneto.api.jenkins.tests;

import java.net.URI;
import java.util.Arrays;

import org.apache.http.HttpStatus;
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

import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.JenkinsServer;
import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.GroovyUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JenkinsServer.class, JenkinsClient.class, GroovyUtil.class })
public class JenkinsServerTest {

	private static final String NAME = "Name";
	private static final String DESCRIPTION = "Description";
	private static final String USERNAME = "Username";
	private static final String FALSE = "false";

	@InjectMocks
	private JenkinsServer jenkinsServer;

	@Mock
	private JenkinsClient jenkinsClientMock;

	@Mock
	private HttpResponse<String> httpResponseStringMock;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		PowerMockito.whenNew(JenkinsClient.class).withAnyArguments()
				.thenReturn(jenkinsClientMock);
		PowerMockito.mockStatic(GroovyUtil.class);
	}

	@Test
	public void constructorTest() throws Exception {

		new JenkinsServer(new URI("http://localhost"));
		new JenkinsServer(new URI("http://localhost"), "username", "password");
	}

	@Test
	public void getVersionTest() throws Exception {

		PowerMockito.when(GroovyUtil.executeScript(Mockito.any(JenkinsClient.class), Mockito.anyString()))
				.thenReturn("0.0.0");
		Assert.assertEquals(jenkinsServer.getVersion(), "0.0.0");
	}

	@Test
	public void setSecurityRealmTest() throws Exception {

		SecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false);
		jenkinsServer.setSecurityRealm(securityRealm);
	}

	@Test(expected = JenkinsServerException.class)
	public void setAuthorizationStrategyThrowsJenkinsServerExceptionTest() throws Exception {

		PowerMockito.when(GroovyUtil.executeScript(Mockito.any(JenkinsClient.class), Mockito.anyString()))
				.thenReturn("false");

		AuthorizationStrategy authorizationStrategy = new UnsecuredAuthorizationStrategy();
		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setAuthorizationStrategyTest() throws Exception {

		PowerMockito.when(GroovyUtil.executeScript(Mockito.any(JenkinsClient.class), Mockito.anyString()))
				.thenReturn("true");

		AuthorizationStrategy authorizationStrategy = new UnsecuredAuthorizationStrategy();
		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setSlaveAgentPortTest() throws Exception {

		jenkinsServer.setSlaveAgentPort(0);
	}

	@Test(expected = JenkinsServerException.class)
	public void getPluginByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetPluginByName(true);

		jenkinsServer.getPluginByName(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getPluginByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getPluginByName(NAME);
	}

	@Test
	public void getPluginByNameTest() throws Exception {

		mockGetPluginByName(false);

		jenkinsServer.getPluginByName(NAME);
	}

	@Test
	public void checkPluginExistsFalseTest() throws Exception {

		mockGetPluginByName(true);

		Assert.assertFalse(jenkinsServer.checkPluginExists(NAME));
	}

	@Test
	public void checkPluginExistsTrueTest() throws Exception {

		mockGetPluginByName(false);

		Assert.assertTrue(jenkinsServer.checkPluginExists(NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void installPluginByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetPluginByName(true);

		jenkinsServer.installPluginByName(NAME, true);
	}

	@Test
	public void installPluginByNameTest() throws Exception {

		mockGetPluginByName(false);

		jenkinsServer.installPluginByName(NAME, true);
	}

	@Test
	public void updateAllInstalledPluginsTest() throws Exception {

		jenkinsServer.updateAllInstalledPlugins();
	}

	@Test(expected = JenkinsServerException.class)
	public void getViewByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.getViewByName(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getViewByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getViewByName(NAME);
	}

	@Test
	public void getViewByNameTest() throws Exception {

		mockGetViewByName(false);

		ListView view = jenkinsServer.getViewByName(NAME);
		Assert.assertEquals(view.getName(), "View");
	}

	@Test
	public void checkViewExistsFalseTest() throws Exception {

		mockGetViewByName(true);

		Assert.assertFalse(jenkinsServer.checkViewExists(NAME));
	}

	@Test
	public void checkViewExistsTrueTest() throws Exception {

		mockGetViewByName(false);

		Assert.assertTrue(jenkinsServer.checkViewExists(NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void createViewExistenceTest() throws Exception {

		mockGetViewByName(false);

		jenkinsServer.createView(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void createViewErrorTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.createView(NAME);
	}

	@Test
	public void createViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"View\"}");

		jenkinsServer.createView(NAME);
	}

	@Test
	public void createViewWithDescriptionTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"View\"}");

		jenkinsServer.createView(NAME, DESCRIPTION);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteViewInexistentTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.deleteView(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteViewErrorTest() throws Exception {

		mockGetViewByName(false);

		jenkinsServer.deleteView(NAME);
	}

	@Test
	public void deleteViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock)
				.thenThrow(new JenkinsClientException(null));
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"View\"}");

		jenkinsServer.deleteView(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getJobByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.getJobByName(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getJobByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getJobByName(NAME);
	}

	@Test
	public void getJobByNameTest() throws Exception {

		mockGetJobByName(false);

		Job job = jenkinsServer.getJobByName(NAME);
		Assert.assertEquals(job.getName(), "Job");
	}

	@Test
	public void checkJobExistsFalseTest() throws Exception {

		mockGetJobByName(true);

		Assert.assertFalse(jenkinsServer.checkJobExists(NAME));
	}

	@Test
	public void checkJobExistsTrueTest() throws Exception {

		mockGetJobByName(false);

		Assert.assertTrue(jenkinsServer.checkJobExists(NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void createJobExistenceTest() throws Exception {

		mockGetJobByName(false);

		jenkinsServer.createJob(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void createJobErrorTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.createJob(NAME);
	}

	@Test
	public void createJobTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null))
				.thenReturn(httpResponseStringMock);
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"Job\"}");

		jenkinsServer.createJob(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.deleteJob(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteJobErrorTest() throws Exception {

		mockGetJobByName(false);

		jenkinsServer.deleteJob(NAME);
	}

	@Test
	public void deleteJobTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock)
				.thenThrow(new JenkinsClientException(null));
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"Job\"}");

		jenkinsServer.deleteJob(NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void addJobToViewInexistentTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.addJobToView(NAME, NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void addJobInexistentToViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock)
				.thenThrow(new JenkinsClientException(null));
		Mockito.when(httpResponseStringMock.getStatus())
				.thenReturn(HttpStatus.SC_OK);
		Mockito.when(httpResponseStringMock.getBody())
				.thenReturn("{\"name\":\"View\"}");

		jenkinsServer.addJobToView(NAME, NAME);
	}

	@Test
	public void addJobToViewTest() throws Exception {

		mockGetViewByName(false);
		mockGetJobByName(false);

		jenkinsServer.addJobToView(NAME, NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void executeJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.executeJob(NAME);
	}

	@Test
	public void executeJobTest() throws Exception {

		mockGetJobByName(false);

		int buildNumber = jenkinsServer.executeJob(NAME);
		Assert.assertEquals(1, buildNumber);
	}

	@Test(expected = JenkinsServerException.class)
	public void addUserToProjectMatrixJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.addUserToProjectMatrix(NAME, USERNAME, Arrays.asList(Permission.COMPUTER_BUILD, Permission.COMPUTER_CONFIGURE));
	}

	@Test(expected = JenkinsServerException.class)
	public void addUserToProjectMatrixNonConfiguredTest() throws Exception {

		mockGetJobByName(false);
		mockcheckAuthorizationStrategy(false);

		jenkinsServer.addUserToProjectMatrix(NAME, USERNAME, Arrays.asList(Permission.COMPUTER_BUILD));
	}

	@Test
	public void addUserToProjectMatrixTest() throws Exception {

		mockGetJobByName(false);
		mockcheckAuthorizationStrategy(true);

		jenkinsServer.addUserToProjectMatrix(NAME, USERNAME, Arrays.asList(Permission.COMPUTER_BUILD));
	}

	@Test(expected = JenkinsServerException.class)
	public void removeUserFromProjectMatrixJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.removeUserFromProjectMatrix(NAME, USERNAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void removeUserFromProjectMatrixNonConfiguredTest() throws Exception {

		mockGetJobByName(false);
		mockcheckAuthorizationStrategy(false);

		jenkinsServer.removeUserFromProjectMatrix(NAME, USERNAME);
	}

	@Test
	public void removeUserFromProjectMatrixTest() throws Exception {

		mockGetJobByName(false);
		mockcheckAuthorizationStrategy(true);

		jenkinsServer.removeUserFromProjectMatrix(NAME, USERNAME);
	}

	private void mockGetPluginByName(boolean throwsException) throws Exception {

		if (throwsException) {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenThrow(new JenkinsClientException(null));
		} else {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenReturn(httpResponseStringMock);
			Mockito.when(httpResponseStringMock.getStatus())
					.thenReturn(HttpStatus.SC_OK);
			Mockito.when(httpResponseStringMock.getBody())
					.thenReturn("{\"name\":\"junit\"}");
		}
	}

	private void mockGetViewByName(boolean throwsException) throws Exception {

		if (throwsException) {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenThrow(new JenkinsClientException(null));
		} else {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenReturn(httpResponseStringMock);
			Mockito.when(httpResponseStringMock.getStatus())
					.thenReturn(HttpStatus.SC_OK);
			Mockito.when(httpResponseStringMock.getBody())
					.thenReturn("{\"name\":\"View\"}");
		}
	}

	private void mockGetJobByName(boolean throwsException) throws Exception {

		if (throwsException) {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenThrow(new JenkinsClientException(null));
		} else {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
					.thenReturn(httpResponseStringMock);
			Mockito.when(httpResponseStringMock.getStatus())
					.thenReturn(HttpStatus.SC_OK);
			Mockito.when(httpResponseStringMock.getBody())
					.thenReturn("{\"name\":\"Job\", \"nextBuildNumber\":2}");
		}
	}

	private void mockcheckAuthorizationStrategy(boolean isConfigured) throws Exception {

		if (isConfigured) {
			PowerMockito.when(GroovyUtil.executeScript(Mockito.any(JenkinsClient.class), Mockito.anyString()))
					.thenReturn("");
		} else {
			PowerMockito.when(GroovyUtil.executeScript(Mockito.any(JenkinsClient.class), Mockito.anyString()))
					.thenReturn(FALSE);
		}
	}
}
