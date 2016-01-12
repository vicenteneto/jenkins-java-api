package br.com.vicenteneto.api.jenkins.tests.util;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import br.com.vicenteneto.api.jenkins.domain.report.ReportType;
import br.com.vicenteneto.api.jenkins.util.ReportUtil;

@RunWith(PowerMockRunner.class)
public class ReportUtilTest {

	@Test
	public void instantiateTest() throws Exception {
		Class<?> cls = ReportUtil.class;
		Constructor<?> c = cls.getDeclaredConstructors()[0];
		c.setAccessible(true);
		c.newInstance((Object[]) null);
	}

	@Test
	public void getBaseCoverageReportUrlTest() throws Exception {
		String baseRepoUrl = ReportUtil.getBaseReportUrl(ReportType.COVERAGE_REPORT);
		Assert.assertEquals(baseRepoUrl, "/job/%s/%d/cobertura/api/json");
	}

	@Test
	public void getBaseTestResultsReportUrlTest() throws Exception {
		String baseRepoUrl = ReportUtil.getBaseReportUrl(ReportType.TEST_RESULTS_REPORT);
		Assert.assertEquals(baseRepoUrl, "/job/%s/%d/testReport/api/json");
	}

	@Test
	public void getBaseStaticAnalysisReportUrlTest() throws Exception {
		String baseRepoUrl = ReportUtil.getBaseReportUrl(ReportType.STATIC_ANALYSIS_REPORT);
		Assert.assertEquals(baseRepoUrl, "/job/%s/%d/violations");
	}
}
