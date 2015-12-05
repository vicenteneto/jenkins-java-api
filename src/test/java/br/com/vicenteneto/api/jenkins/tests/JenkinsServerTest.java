package br.com.vicenteneto.api.jenkins.tests;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.com.vicenteneto.api.jenkins.JenkinsServer;
import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.GroovyUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GroovyUtil.class)
public class JenkinsServerTest {

	@InjectMocks
	private JenkinsServer jenkinsServer;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

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

	@Test
	public void updateAllInstalledPluginsTest() throws Exception {

		jenkinsServer.updateAllInstalledPlugins(true);
	}
}
