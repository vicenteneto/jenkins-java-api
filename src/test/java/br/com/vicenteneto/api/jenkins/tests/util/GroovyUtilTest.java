package br.com.vicenteneto.api.jenkins.tests.util;

import java.lang.reflect.Constructor;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.client.JenkinsClient;
import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.FullControlOnceLoggedInAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.ProjectMatrixAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.LDAPSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.util.GroovyUtil;

@RunWith(PowerMockRunner.class)
public class GroovyUtilTest {

	private static final String SCRIPT = "Script";

	@Mock
	private JenkinsClient jenkinsClient;

	@Mock
	private HttpResponse<String> httpResponseString;

	@Test
	public void instantiateTest() throws Exception {
		Class<?> cls = GroovyUtil.class;
		Constructor<?> c = cls.getDeclaredConstructors()[0];
		c.setAccessible(true);
		c.newInstance((Object[]) null);
	}

	@Test
	public void concatenateStringsTest() {
		String concatenated = GroovyUtil.concatenateStrings("Line 1\n", "Line 2");
		Assert.assertEquals(concatenated, "Line 1\nLine 2");
	}

	@Test(expected = JenkinsServerException.class)
	public void executeScriptThrowsJenkinsClientExceptionTest() throws Exception {
		Mockito.when(jenkinsClient.postURLEncoded(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null));

		GroovyUtil.executeScript(jenkinsClient, SCRIPT);
	}

	@Test(expected = JenkinsServerException.class)
	public void executeScriptForbiddenTest() throws Exception {
		Mockito.when(jenkinsClient.postURLEncoded(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(httpResponseString);
		Mockito.when(httpResponseString.getStatus())
				.thenReturn(HttpStatus.SC_FORBIDDEN);

		GroovyUtil.executeScript(jenkinsClient, SCRIPT);
	}

	@Test
	public void executeScriptTest() throws Exception {
		Mockito.when(jenkinsClient.postURLEncoded(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(httpResponseString);

		GroovyUtil.executeScript(jenkinsClient, SCRIPT);
	}

	@Test(expected = JenkinsServerException.class)
	public void generateUnknownSecurityRealmGroovyScript() throws Exception {
		GroovyUtil.generateGroovyScript(new SecurityRealm() { });
	}

	@Test
	public void generateHudsonPrivateSecurityRealmGroovyScript() throws Exception {
		HudsonPrivateSecurityRealm hudsonPrivateSR = new HudsonPrivateSecurityRealm(false);

		Assert.assertEquals(GroovyUtil.generateGroovyScript(hudsonPrivateSR),
				"def security = new hudson.security.HudsonPrivateSecurityRealm(false);\n");

		hudsonPrivateSR.setAllowsSignUp(true);
		Assert.assertEquals(GroovyUtil.generateGroovyScript(hudsonPrivateSR),
				"def security = new hudson.security.HudsonPrivateSecurityRealm(true);\n");
	}

	@Test
	public void generategenerateLDAPSecurityRealmGroovyScript() throws Exception {
		LDAPSecurityRealm ldapSR = new LDAPSecurityRealm("server", "rootDN", "userSearchBase", "userSearch",
				"groupSearchBase", "groupSearchFilter", "groupMembershipFilter", "managerDN", "managerPassword", false);

		Assert.assertEquals(GroovyUtil.generateGroovyScript(ldapSR),
				"def security = new hudson.security.LDAPSecurityRealm('server', 'rootDN', 'userSearchBase', 'userSearch', 'groupSearchBase', 'groupSearchFilter', 'groupMembershipFilter', 'managerDN', 'managerPassword', false, false, null);\n");

		ldapSR.setServer("server2");
		ldapSR.setRootDN("rootDN2");
		ldapSR.setUserSearchBase("userSearchBase2");
		ldapSR.setUserSearch("userSearch2");
		ldapSR.setGroupSearchBase("groupSearchBase2");
		ldapSR.setGroupSearchFilter("groupSearchFilter2");
		ldapSR.setGroupMembershiphFilter("groupMembershipFilter2");
		ldapSR.setManagerDN("managerDN2");
		ldapSR.setManagerPassword("managerPassword2");
		ldapSR.setInhibitInferRootDN(true);
		Assert.assertEquals(GroovyUtil.generateGroovyScript(ldapSR),
				"def security = new hudson.security.LDAPSecurityRealm('server2', 'rootDN2', 'userSearchBase2', 'userSearch2', 'groupSearchBase2', 'groupSearchFilter2', 'groupMembershipFilter2', 'managerDN2', 'managerPassword2', true, false, null);\n");
	}

	@Test(expected = JenkinsServerException.class)
	public void generateUnknownAuthorizationStrategyGroovyScript() throws Exception {
		GroovyUtil.generateGroovyScript(new AuthorizationStrategy() { });
	}

	@Test
	public void generateFullControlOnceLoggedInAuthorizationStrategyScript() throws Exception {
		FullControlOnceLoggedInAuthorizationStrategy fullControlOnceLoggedInAS = new FullControlOnceLoggedInAuthorizationStrategy();

		Assert.assertEquals(GroovyUtil.generateGroovyScript(fullControlOnceLoggedInAS),
				"def authorization = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy();\n");
	}

	@Test
	public void generateUnsecuredAuthorizationStrategyScript() throws Exception {
		UnsecuredAuthorizationStrategy unsecuredAS = new UnsecuredAuthorizationStrategy();

		Assert.assertEquals(GroovyUtil.generateGroovyScript(unsecuredAS),
				"def authorization = new hudson.security.AuthorizationStrategy.Unsecured();\n");
	}

	@Test
	public void generateProjectMatrixAuthorizationStrategyScript() throws Exception {
		ProjectMatrixAuthorizationStrategy projectMatrixAS = new ProjectMatrixAuthorizationStrategy();
		projectMatrixAS.add("admin", Permission.COMPUTER_BUILD);

		Assert.assertEquals(GroovyUtil.generateGroovyScript(projectMatrixAS),
				"def authorization = new hudson.security.ProjectMatrixAuthorizationStrategy();\nauthorization.add(hudson.security.Permission.fromId('hudson.model.Computer.Build'), 'admin');\n");
	}
}
