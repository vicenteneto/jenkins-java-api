package br.com.vicenteneto.api.jenkins;

import java.net.URI;

public class Teste {
	public static void main(String[] args) throws Exception {
		JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://192.168.25.7:8000"), "vicente", "6esncj4m");
		jenkinsServer.getJobByName("JOB");
	}
}
