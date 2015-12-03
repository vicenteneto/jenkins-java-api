package br.com.vicenteneto.api.jenkins.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ConfigurationUtil {

	private static final String BASE_NAME = "br.com.vicenteneto.api.jenkins.configuration";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BASE_NAME);

	public static String getConfiguration(String key) {

		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException exception) {
			return "!" + key + "!";
		}
	}

	private ConfigurationUtil() { }
}
