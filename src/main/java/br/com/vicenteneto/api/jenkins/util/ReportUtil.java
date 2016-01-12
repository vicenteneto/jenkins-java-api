package br.com.vicenteneto.api.jenkins.util;

import br.com.vicenteneto.api.jenkins.domain.report.ReportType;

public final class ReportUtil {

	public static String getBaseReportUrl(ReportType reportType) {

		if (reportType.equals(ReportType.COVERAGE_REPORT)) {
			return ConfigurationUtil.getConfiguration("URL_GET_COVERAGE_REPORT");
		} else if (reportType.equals(ReportType.TEST_RESULTS_REPORT)) {
			return ConfigurationUtil.getConfiguration("URL_GET_TEST_RESULTS_REPORT");
		} else {
			return ConfigurationUtil.getConfiguration("URL_GET_STATIC_ANALYSIS_REPORT");
		}
	}

	private ReportUtil() { }
}
