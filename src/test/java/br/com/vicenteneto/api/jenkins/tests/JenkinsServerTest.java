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
import br.com.vicenteneto.api.jenkins.domain.CoverageType;
import br.com.vicenteneto.api.jenkins.domain.Job;
import br.com.vicenteneto.api.jenkins.domain.ListView;
import br.com.vicenteneto.api.jenkins.domain.Permission;
import br.com.vicenteneto.api.jenkins.domain.Plugin;
import br.com.vicenteneto.api.jenkins.domain.authorization.AuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.authorization.UnsecuredAuthorizationStrategy;
import br.com.vicenteneto.api.jenkins.domain.report.CoverageElement;
import br.com.vicenteneto.api.jenkins.domain.report.CoverageReport;
import br.com.vicenteneto.api.jenkins.domain.report.StaticAnalysisReport;
import br.com.vicenteneto.api.jenkins.domain.report.TestResultsReport;
import br.com.vicenteneto.api.jenkins.domain.security.HudsonPrivateSecurityRealm;
import br.com.vicenteneto.api.jenkins.domain.security.SecurityRealm;
import br.com.vicenteneto.api.jenkins.exception.JenkinsClientException;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;
import br.com.vicenteneto.api.jenkins.tests.data.TestsData;
import br.com.vicenteneto.api.jenkins.util.GroovyUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JenkinsServer.class, JenkinsClient.class, GroovyUtil.class })
public class JenkinsServerTest {

	private static final String URL = "http://localhost";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String VERSION = "0.0.0";
	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private static final String VIEW_JSON = "{\"name\": \"View\", \"description\" : \"Description\", \"jobs\": []}";
	private static final String JOB_JSON = "{\"name\": \"Job\", \"description\": \"Description\", \"buildable\": true, \"nextBuildNumber\": 2, \"builds\": []}";
	private static final String PLUGIN_JSON = "{\"name\": \"junit\", \"url\": \"http://updates.jenkins-ci.org/download/plugins/junit/1.10/junit.hpi\", \"version\": \"1.10\", \"compatibleWithInstalledVersion\": true, \"excerpt\": \"Allows JUnit-format test results to be published.\", \"title\": \"JUnit Plugin\", \"wiki\": \"https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin\"}";
	private static final String COVERAGE_REPORT_JSON = "{\"results\": {\"elements\": [{\"denominator\": 37.0, \"name\": \"Packages\", \"numerator\": 27.0, \"ratio\": 72.97298}, {\"denominator\": 176.0, \"name\": \"Files\", \"numerator\": 123.0, \"ratio\": 69.88636}, {\"denominator\": 182.0, \"name\": \"Classes\", \"numerator\": 126.0, \"ratio\": 69.23077}], \"name\" : \"Cobertura Coverage Report\"}}";
	private static final String TEST_RESULTS_REPORT_JSON = "{\"duration\": 23.822, \"empty\": false, \"failCount\": 2, \"passCount\": 355, \"skipCount\" : 0}";
	private static final String STATIC_ANALYSIS_REPORT_JSON = "{\"violations\": {\"junit\": 10}, \"violationsCount\": 10}";
	private static final int BUILD_NUMBER = 1;

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

		new JenkinsServer(new URI(URL));
		new JenkinsServer(new URI(URL), USERNAME, PASSWORD);
	}

	@Test
	public void getVersionTest() throws Exception {

		mockExecuteScript(VERSION);

		Assert.assertEquals(jenkinsServer.getVersion(), VERSION);
	}

	@Test
	public void setSecurityRealmTest() throws Exception {

		SecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false);
		jenkinsServer.setSecurityRealm(securityRealm);
	}

	@Test(expected = JenkinsServerException.class)
	public void setAuthorizationStrategyThrowsJenkinsServerExceptionTest() throws Exception {

		mockExecuteScript(FALSE);

		AuthorizationStrategy authorizationStrategy = new UnsecuredAuthorizationStrategy();
		jenkinsServer.setAuthorizationStrategy(authorizationStrategy);
	}

	@Test
	public void setAuthorizationStrategyTest() throws Exception {

		mockExecuteScript(TRUE);

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

		jenkinsServer.getPluginByName(TestsData.PLUGIN_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getPluginByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		mockGet(false);
		mockGetStatus(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getPluginByName(TestsData.PLUGIN_NAME);
	}

	@Test
	public void getPluginByNameTest() throws Exception {

		mockGetPluginByName(false);

		Plugin plugin = jenkinsServer.getPluginByName(TestsData.PLUGIN_NAME);

		Assert.assertEquals(plugin.getName(), TestsData.PLUGIN_NAME);
		Assert.assertEquals(plugin.getUrl(), TestsData.PLUGIN_URL);
		Assert.assertEquals(plugin.getVersion(), TestsData.PLUGIN_VERSION);
		Assert.assertEquals(plugin.isCompatibleWithInstalledVersion(), TestsData.PLUGIN_COMPATIBLE_WITH_INSTALLED_VERSION);
		Assert.assertEquals(plugin.getExcerpt(), TestsData.PLUGIN_EXCERPT);
		Assert.assertEquals(plugin.getTitle(), TestsData.PLUGIN_TITLE);
		Assert.assertEquals(plugin.getWiki(), TestsData.PLUGIN_WIKI);
	}

	@Test
	public void checkPluginExistsFalseTest() throws Exception {

		mockGetPluginByName(true);

		Assert.assertFalse(jenkinsServer.checkPluginExists(TestsData.PLUGIN_NAME));
	}

	@Test
	public void checkPluginExistsTrueTest() throws Exception {

		mockGetPluginByName(false);

		Assert.assertTrue(jenkinsServer.checkPluginExists(TestsData.PLUGIN_NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void installPluginByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetPluginByName(true);

		jenkinsServer.installPluginByName(TestsData.PLUGIN_NAME, true);
	}

	@Test
	public void installPluginByNameTest() throws Exception {

		mockGetPluginByName(false);

		jenkinsServer.installPluginByName(TestsData.PLUGIN_NAME, true);
	}

	@Test
	public void updateAllInstalledPluginsTest() throws Exception {

		jenkinsServer.updateAllInstalledPlugins();
	}

	@Test(expected = JenkinsServerException.class)
	public void getViewByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.getViewByName(TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getViewByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		mockGet(false);
		mockGetStatus(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getViewByName(TestsData.VIEW_NAME);
	}

	@Test
	public void getViewByNameTest() throws Exception {

		mockGetViewByName(false);

		ListView listView = jenkinsServer.getViewByName(TestsData.VIEW_NAME);

		Assert.assertEquals(listView.getName(), TestsData.VIEW_NAME);
		Assert.assertEquals(listView.getDescription(), TestsData.VIEW_DESCRIPTION);
		Assert.assertEquals(listView.getJobs().size(), 0);
	}

	@Test
	public void checkViewExistsFalseTest() throws Exception {

		mockGetViewByName(true);

		Assert.assertFalse(jenkinsServer.checkViewExists(TestsData.VIEW_NAME));
	}

	@Test
	public void checkViewExistsTrueTest() throws Exception {

		mockGetViewByName(false);

		Assert.assertTrue(jenkinsServer.checkViewExists(TestsData.VIEW_NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void createViewExistenceTest() throws Exception {

		mockGetViewByName(false);

		jenkinsServer.createView(TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void createViewErrorTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.createView(TestsData.VIEW_NAME);
	}

	@Test
	public void createViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenThrow(new JenkinsClientException(null))
			.thenReturn(httpResponseStringMock);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(VIEW_JSON);

		jenkinsServer.createView(TestsData.VIEW_NAME);
	}

	@Test
	public void createViewWithDescriptionTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenThrow(new JenkinsClientException(null))
			.thenReturn(httpResponseStringMock);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(VIEW_JSON);

		jenkinsServer.createView(TestsData.VIEW_NAME, TestsData.VIEW_DESCRIPTION);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteViewInexistentTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.deleteView(TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteViewErrorTest() throws Exception {

		mockGetViewByName(false);

		jenkinsServer.deleteView(TestsData.VIEW_NAME);
	}

	@Test
	public void deleteViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenReturn(httpResponseStringMock)
			.thenThrow(new JenkinsClientException(null));
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(VIEW_JSON);

		jenkinsServer.deleteView(TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getJobByNameThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.getJobByName(TestsData.JOB_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getJobByNameNotFoundThrowsJenkinsServerExceptionTest() throws Exception {

		mockGet(false);
		mockGetStatus(HttpStatus.SC_NOT_FOUND);

		jenkinsServer.getJobByName(TestsData.JOB_NAME);
	}

	@Test
	public void getJobByNameTest() throws Exception {

		mockGetJobByName(false);

		Job job = jenkinsServer.getJobByName(TestsData.JOB_NAME);

		Assert.assertEquals(job.getName(), TestsData.JOB_NAME);
		Assert.assertEquals(job.getDescription(), TestsData.JOB_DESCRIPTION);
		Assert.assertEquals(job.isBuildable(), TestsData.JOB_BUILDABLE);
		Assert.assertEquals(job.getBuilds().size(), 0);
		Assert.assertEquals(job.getNextBuildNumber(), TestsData.JOB_NEXT_BUILD_NUMBER);
	}

	@Test
	public void checkJobExistsFalseTest() throws Exception {

		mockGetJobByName(true);

		Assert.assertFalse(jenkinsServer.checkJobExists(TestsData.JOB_NAME));
	}

	@Test
	public void checkJobExistsTrueTest() throws Exception {

		mockGetJobByName(false);

		Assert.assertTrue(jenkinsServer.checkJobExists(TestsData.JOB_NAME));
	}

	@Test(expected = JenkinsServerException.class)
	public void createJobExistenceTest() throws Exception {

		mockGetJobByName(false);

		jenkinsServer.createJob(TestsData.JOB_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void createJobErrorTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.createJob(TestsData.JOB_NAME);
	}

	@Test
	public void createJobTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenThrow(new JenkinsClientException(null))
			.thenReturn(httpResponseStringMock);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(JOB_JSON);

		jenkinsServer.createJob(TestsData.JOB_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.deleteJob(TestsData.JOB_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void deleteJobErrorTest() throws Exception {

		mockGetJobByName(false);

		jenkinsServer.deleteJob(TestsData.JOB_NAME);
	}

	@Test
	public void deleteJobTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenReturn(httpResponseStringMock)
			.thenThrow(new JenkinsClientException(null));
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(JOB_JSON);

		jenkinsServer.deleteJob(TestsData.JOB_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void addJobToViewInexistentTest() throws Exception {

		mockGetViewByName(true);

		jenkinsServer.addJobToView(TestsData.JOB_NAME, TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void addJobInexistentToViewTest() throws Exception {

		Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
			.thenReturn(httpResponseStringMock)
			.thenThrow(new JenkinsClientException(null));
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(VIEW_JSON);

		jenkinsServer.addJobToView(TestsData.JOB_NAME, TestsData.VIEW_NAME);
	}

	@Test
	public void addJobToViewTest() throws Exception {

		mockGetViewByName(false);
		mockGetJobByName(false);

		jenkinsServer.addJobToView(TestsData.JOB_NAME, TestsData.VIEW_NAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void executeJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.executeJob(TestsData.JOB_NAME);
	}

	@Test
	public void executeJobTest() throws Exception {

		mockGetJobByName(false);

		int buildNumber = jenkinsServer.executeJob(TestsData.JOB_NAME);
		Assert.assertEquals(2, buildNumber);
	}

	@Test(expected = JenkinsServerException.class)
	public void addUserToProjectMatrixJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.addUserToProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME, Arrays.asList(Permission.COMPUTER_BUILD, Permission.COMPUTER_CONFIGURE));
	}

	@Test(expected = JenkinsServerException.class)
	public void addUserToProjectMatrixNonConfiguredTest() throws Exception {

		mockGetJobByName(false);
		mockCheckAuthorizationStrategy(false);

		jenkinsServer.addUserToProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME, Arrays.asList(Permission.COMPUTER_BUILD));
	}

	@Test
	public void addUserToProjectMatrixTest() throws Exception {

		mockGetJobByName(false);
		mockCheckAuthorizationStrategy(true);

		jenkinsServer.addUserToProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME, Arrays.asList(Permission.COMPUTER_BUILD));
	}

	@Test(expected = JenkinsServerException.class)
	public void removeUserFromProjectMatrixJobInexistentTest() throws Exception {

		mockGetJobByName(true);

		jenkinsServer.removeUserFromProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void removeUserFromProjectMatrixNonConfiguredTest() throws Exception {

		mockGetJobByName(false);
		mockCheckAuthorizationStrategy(false);

		jenkinsServer.removeUserFromProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME);
	}

	@Test
	public void removeUserFromProjectMatrixTest() throws Exception {

		mockGetJobByName(false);
		mockCheckAuthorizationStrategy(true);

		jenkinsServer.removeUserFromProjectMatrix(TestsData.JOB_NAME, TestsData.USERNAME);
	}

	@Test(expected = JenkinsServerException.class)
	public void getCoverageReportElementJobDoesNotExistsTest() throws Exception {

		mockGetJobByName(true);

		CoverageReport coverageReport = jenkinsServer.getCoverageReport(TestsData.JOB_NAME, BUILD_NUMBER);

		Assert.assertEquals(coverageReport.getName(), "Cobertura Coverage Report");
	}

	@Test(expected = JenkinsServerException.class)
	public void getCoverageReportElementThrowsJenkinsServerExceptionTest() throws Exception {

		mockGetJobByName(false);
		mockGetDepth(true);

		jenkinsServer.getCoverageReportElement(TestsData.JOB_NAME, BUILD_NUMBER, CoverageType.PACKAGES);
	}

	@Test(expected = JenkinsServerException.class)
	public void getCoverageReportElementPluginNotConfiguredTest() throws Exception {

		mockGet(false);
		mockGetDepth(false);
		Mockito.when(httpResponseStringMock.getStatus())
			.thenReturn(HttpStatus.SC_OK)
			.thenReturn(HttpStatus.SC_NOT_FOUND);
		mockGetBody(JOB_JSON);

		jenkinsServer.getCoverageReportElement(TestsData.JOB_NAME, BUILD_NUMBER, CoverageType.LINES);
	}

	@Test(expected = JenkinsServerException.class)
	public void getCoverageReportElementWithoutElementsTest() throws Exception {

		mockGetJobByName(false);
		mockGetDepth(false);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(COVERAGE_REPORT_JSON);

		jenkinsServer.getCoverageReportElement(TestsData.JOB_NAME, BUILD_NUMBER, CoverageType.CONDITIONALS);
	}

	@Test
	public void getCoverageReportElementTest() throws Exception {

		mockGetJobByName(false);
		mockGetDepth(false);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(COVERAGE_REPORT_JSON);

		CoverageElement coverageReportElement = jenkinsServer.getCoverageReportElement(TestsData.JOB_NAME, BUILD_NUMBER, CoverageType.FILES);

		Assert.assertEquals(coverageReportElement.getName(), "Files");
		Assert.assertEquals(coverageReportElement.getDenominator(), 176);
		Assert.assertEquals(coverageReportElement.getNumerator(), 123);
		Assert.assertEquals(coverageReportElement.getRatio(), 69.88636, 0.00001);
	}

	@Test
	public void getTestResultsReportTest() throws Exception {

		mockGetJobByName(false);
		mockGetDepth(false);
		mockGetStatus(HttpStatus.SC_OK);
		mockGetBody(TEST_RESULTS_REPORT_JSON);

		TestResultsReport testResultsReport = jenkinsServer.getTestResultsReport(TestsData.JOB_NAME, BUILD_NUMBER);

		Assert.assertEquals(testResultsReport.getDuration(), 23.822, 0.001);
		Assert.assertFalse(testResultsReport.isEmpty());
		Assert.assertEquals(testResultsReport.getFailCount(), 2);
		Assert.assertEquals(testResultsReport.getPassCount(), 355);
		Assert.assertEquals(testResultsReport.getSkipCount(), 0);
	}

	@Test
	public void getStaticAnalysisReportTest() throws Exception {

		mockGetJobByName(false);
		mockGetDepth(false);
		mockGetStatus(HttpStatus.SC_OK);
		mockExecuteScript(STATIC_ANALYSIS_REPORT_JSON);

		StaticAnalysisReport staticAnalysisReport = jenkinsServer.getStaticAnalysisReport(TestsData.JOB_NAME, BUILD_NUMBER);

		Assert.assertEquals(staticAnalysisReport.getViolations().size(), 1);
		Assert.assertEquals(staticAnalysisReport.getViolationsCount(), 10);
	}

	private void mockExecuteScript(String response) throws Exception {

		Mockito.when(GroovyUtil.executeScript(
					Mockito.any(JenkinsClient.class),
					Mockito.anyString()))
			.thenReturn(response);
	}

	private void mockGet(boolean throwsException) throws Exception {

		if (throwsException) {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null));
		} else {
			Mockito.when(jenkinsClientMock.get(Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
		}
	}

	private void mockGetBody(String response) {

		Mockito.when(httpResponseStringMock.getBody())
			.thenReturn(response);
	}

	private void mockGetPluginByName(boolean throwsException) throws Exception {

		if (throwsException) {
			mockGet(true);
		} else {
			mockGet(false);
			mockGetStatus(HttpStatus.SC_OK);
			mockGetBody(PLUGIN_JSON);
		}
	}

	private void mockGetStatus(int status) {

		Mockito.when(httpResponseStringMock.getStatus())
			.thenReturn(status);
	}

	private void mockGetViewByName(boolean throwsException) throws Exception {

		if (throwsException) {
			mockGet(true);
		} else {
			mockGet(false);
			mockGetStatus(HttpStatus.SC_OK);
			mockGetBody(VIEW_JSON);
		}
	}

	private void mockGetJobByName(boolean throwsException) throws Exception {

		if (throwsException) {
			mockGet(true);
		} else {
			mockGet(false);
			mockGetStatus(HttpStatus.SC_OK);
			mockGetBody(JOB_JSON);
		}
	}

	private void mockCheckAuthorizationStrategy(boolean isConfigured) throws Exception {

		if (isConfigured) {
			mockExecuteScript("");
		} else {
			mockExecuteScript(FALSE);
		}
	}

	private void mockGetDepth(boolean throwsException) throws Exception {

		if (throwsException) {
			Mockito.when(jenkinsClientMock.getDepth(Mockito.anyString()))
				.thenThrow(new JenkinsClientException(null));
		} else {
			Mockito.when(jenkinsClientMock.getDepth(Mockito.anyString()))
				.thenReturn(httpResponseStringMock);
		}
	}
}
