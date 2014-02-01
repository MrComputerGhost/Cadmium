package com.sci.cadmium.config;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConfigTest
{
	private class Config
	{
		public int getIntProp()
		{
			return intProp;
		}

		public boolean getBooleanProp()
		{
			return booleanProp;
		}

		public String getStringProp()
		{
			return stringProp;
		}

		public double getDoubleProp()
		{
			return doubleProp;
		}

		public float getFloatProp()
		{
			return floatProp;
		}

		public short getShortProp()
		{
			return shortProp;
		}

		public byte getByteProp()
		{
			return byteProp;
		}

		@ConfigProperty(key = "intProp")
		private int intProp;

		@ConfigProperty(key = "booleanProp")
		private boolean booleanProp;

		@ConfigProperty(key = "stringProp")
		private String stringProp;

		@ConfigProperty(key = "doubleProp")
		private double doubleProp;

		@ConfigProperty(key = "floatProp")
		private float floatProp;

		@ConfigProperty(key = "shortProp")
		private short shortProp;

		@ConfigProperty(key = "byteProp")
		private byte byteProp;
	}

	@Test
	public void configTest()
	{
		File file = new File("test/config/testConfig.cfg");
		boolean first = false;

		if(!file.exists())
		{
			first = true;

			try
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			catch(IOException e)
			{
			}
		}

		Config cfg = new Config();
		Configuration config = new Configuration(file, cfg);

		if(first)
		{
			Map<String, String> defaults = new HashMap<String, String>();
			defaults.put("intProp", "222");
			defaults.put("booleanProp", "true");
			defaults.put("shortProp", "42");
			defaults.put("byteProp", "11");
			defaults.put("stringProp", "test123");
			defaults.put("floatProp", "0.25");
			defaults.put("doubleProp", "3.14");
			config.write(defaults);
		}

		config.load();

		assertEquals(222, cfg.getIntProp());
		assertEquals(true, cfg.getBooleanProp());
		assertEquals(42, cfg.getShortProp());
		assertEquals(11, cfg.getByteProp());
		assert (0.25f == cfg.getFloatProp());
		assert (3.14 == cfg.getDoubleProp());
		assert ("test123".equals(cfg.getStringProp()));
	}
}