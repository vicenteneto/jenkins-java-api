package br.com.vicenteneto.api.jenkins.util;

public final class Constants {
	
	private static final String SLASH = "/";
	private static final String STR_VARIABLE = "%s";

	public static final String URL_CREATE_VIEW = SLASH + "createView";
	public static final String URL_CREATE_JOB = SLASH + "createItem";
	public static final String URL_DO_DELETE = SLASH + "doDelete";
	public static final String URL_SCRIPT_TEXT = SLASH + "scriptText";

	public static final String URL_ADD_JOB_TO_VIEW = SLASH + "view" + SLASH + STR_VARIABLE + SLASH + "addJobToView";
	
	private Constants() { }
	
}
