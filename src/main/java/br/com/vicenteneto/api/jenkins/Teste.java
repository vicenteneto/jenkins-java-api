package br.com.vicenteneto.api.jenkins;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import com.mashape.unirest.http.HttpResponse;

import br.com.vicenteneto.api.jenkins.exception.JenkinsServerException;

public class Teste {
	public static void main(String[] args)
			throws URISyntaxException, JenkinsServerException {
		URI serverURI = new URIBuilder()
				.setScheme("http")
				.setHost("192.168.25.5")
				.setPort(8000)
				.build();
		
		JenkinsServer server = new JenkinsServer(serverURI);
		HttpResponse<String> deleteJob = server.deleteJob("test");
	}
}
