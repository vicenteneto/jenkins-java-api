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

import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.JenkinsServer;
import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.authorization.FullControlOnceLoggedInAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.ProjectMatrixAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.LDAPSecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JenkinsServer.class)
public class JenkinsServerTest {

	@InjectMocks
	private JenkinsServer jenkinsServer;

	@Mock
	private JenkinsClient jenkinsClientMock;

	@Mock
	private HttpResponse<String> httpResponseStringMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		PowerMockito.whenNew(JenkinsClient.class).withAnyArguments().thenReturn(jenkinsClientMock);

		Mockito.when(jenkinsClientMock.postURLEncoded(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
	}

	@Test
	public void constructorTest() throws Exception {
		new JenkinsServer(new URI("http://localhost"));
		new JenkinsServer(new URI("http://localhost"), "username", "password");
	}

	@Test
	public void getVersionTest() throws Exception {

		String version = "0.0.0";

		Mockito.when(httpResponseStringMock.getBody()).thenReturn(version);

		Assert.assertEquals(jenkinsServer.getVersion(), version);
	}

	@Test
	public void setHudsonPrivateSecurityRealmTest() throws Exception {

		HudsonPrivateSecurityRealm securityRealm;

		securityRealm = new HudsonPrivateSecurityRealm(false);
		jenkinsServer.setSecurityRealm(securityRealm);

		securityRealm.setAllowsSignUp(true);
		jenkinsServer.setSecurityRealm(securityRealm);
	}

	@Test
	public void setLDAPSecurityRealmTest() throws Exception {

		LDAPSecurityRealm securityRealm;

		securityRealm = new LDAPSecurityRealm("server", "rootDN", "userSearchBase", "userSearch", "groupSearchBase",
				"groupSearchFilter", "groupMembershipFilter", "managerDN", "managerPassword", false);
		jenkinsServer.setSecurityRealm(securityRealm);

		securityRealm = new LDAPSecurityRealm("server", "rootDN", "userSearchBase", "userSearch", "groupSearchBase",
				"groupSearchFilter", "groupMembershipFilter", "managerDN", "managerPassword", true);
		jenkinsServer.setSecurityRealm(securityRealm);
	}

	@Test(expected = JenkinsServerException.class)
	public void setAuthorizationStrategyThrowsJenkinsServerExceptionTest() throws Exception {

		FullControlOnceLoggedInAuthorizationStrategy authorizationStrategy;

		authorizationStrategy = new FullControlOnceLoggedInAuthorizationStrategy();

		Mockito.when(httpResponseStringMock.getBody()).thenReturn("false");

		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setFullControlOnceLoggedInAuthorizationStrategyTest() throws Exception {

		FullControlOnceLoggedInAuthorizationStrategy authorizationStrategy;

		authorizationStrategy = new FullControlOnceLoggedInAuthorizationStrategy();

		Mockito.when(httpResponseStringMock.getBody()).thenReturn("");

		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setProjectMatrixAuthorizationStrategyTest() throws Exception {

		ProjectMatrixAuthorizationStrategy authorizationStrategy;

		authorizationStrategy = new ProjectMatrixAuthorizationStrategy();
		authorizationStrategy.add("sid", Permission.COMPUTER_BUILD);
		authorizationStrategy.add("sid", Permission.COMPUTER_CONFIGURE);

		Mockito.when(httpResponseStringMock.getBody()).thenReturn("");

		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setUnsecuredAuthorizationStrategyTest() throws Exception {

		UnsecuredAuthorizationStrategy authorizationStrategy;

		authorizationStrategy = new UnsecuredAuthorizationStrategy();

		Mockito.when(httpResponseStringMock.getBody()).thenReturn("");

		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setSlaveAgentPortTest() throws Exception {

		jenkinsServer.setSlaveAgentPort(0);
	}
}
