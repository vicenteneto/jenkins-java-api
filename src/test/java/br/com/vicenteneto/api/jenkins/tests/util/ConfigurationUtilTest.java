package br.com.vicenteneto.api.jenkins.tests.util;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import br.com.vicenteneto.api.jenkins.util.ConfigurationUtil;

@RunWith(PowerMockRunner.class)
public class ConfigurationUtilTest {

	private static final String KEY_INVALID = "conf.INVALID";
	private static final String KEY_VALID = "AUTHORIZATION";
	private static final String VALUE_INVALID = "!conf.INVALID!";
	private static final String VALUE_VALID = "Authorization";

	@Test
	public void instantiateTest() throws Exception {
		Class<?> cls = ConfigurationUtil.class;
		Constructor<?> c = cls.getDeclaredConstructors()[0];
		c.setAccessible(true);
		c.newInstance((Object[]) null);
	}

	@Test
	public void getConfigurationInvalidTest() throws Exception {
		String configuration = ConfigurationUtil.getConfiguration(KEY_INVALID);
		Assert.assertEquals(configuration, VALUE_INVALID);
	}

	@Test
	public void getConfigurationTest() throws Exception {
		String configuration = ConfigurationUtil.getConfiguration(KEY_VALID);
		Assert.assertEquals(configuration, VALUE_VALID);
	}
}
