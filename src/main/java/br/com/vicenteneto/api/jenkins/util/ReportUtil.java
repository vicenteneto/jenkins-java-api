package br.com.vicenteneto.api.jenkins.util;

import br.com.vicenteneto.api.jenkins.domain.report.ReportType;
import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;

public final class ReportUtil {

	public static String getBaseReportUrl(ReportType reportType) throws JenkinsServerException {

		if (reportType.equals(ReportType.COVERAGE_REPORT)) {
			return ConfigurationUtil.getConfiguration("URL_GET_COVERAGE_REPORT");
		} else if (reportType.equals(ReportType.TEST_RESULTS_REPORT)) {
			return ConfigurationUtil.getConfiguration("URL_GET_TEST_RESULTS_REPORT");
		} else if (reportType.equals(ReportType.STATIC_ANALYSIS_REPORT)) {
			return ConfigurationUtil.getConfiguration("URL_GET_STATIC_ANALYSIS_REPORT");
		}

		throw new JenkinsServerException(ConfigurationUtil.getConfiguration("UNKNOW_REPORT_TYPE"));
	}

	private ReportUtil() { }
}
