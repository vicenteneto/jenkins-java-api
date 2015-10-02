package br.com.vicenteneto.api.jenkins.exception;

public class JenkinsServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4991901531451402986L;

	public JenkinsServerException(String message) {
		super(message);
	}

	public JenkinsServerException(Throwable throwable) {
		super(throwable);
	}

}
