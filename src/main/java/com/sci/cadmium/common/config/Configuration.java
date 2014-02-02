package com.sci.cadmium.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sci.cadmium.server.util.Close;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Configuration
{
	/**
	 * File of this configuration
	 */
	private File file;
	
	/**
	 * Class this configuration loads to
	 */
	private Object obj;
	
	/**
	 * This configuration's logger
	 */
	private Logger log;

	public Configuration(File file, Object obj)
	{
		this.file = file;
		this.obj = obj;
		this.log = Logger.getLogger("Configuration");
	}

	/**
	 * Loads configuration from file
	 */
	public void load()
	{
		Map<String, String> properties = new HashMap<String, String>();

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(this.file));

			String line;
			while((line = reader.readLine()) != null)
			{
				String[] split = line.split("=");
				if(split.length != 2)
				{
					this.log.log(Level.WARNING, "Invalid config line: " + line);
					continue;
				}

				properties.put(split[0], split[1]);
			}
		}
		catch(IOException e)
		{
			this.log.log(Level.SEVERE, "An error occured loading config file!", e);
		}
		finally
		{
			Close.quiet(reader);
		}

		Field[] fields = this.obj.getClass().getDeclaredFields();
		for(Field field : fields)
		{
			ConfigProperty property = field.getAnnotation(ConfigProperty.class);
			if(property != null)
			{
				Class<?> type = field.getType();
				boolean flag = field.isAccessible();

				try
				{
					field.setAccessible(true);

					if(type.toString().toLowerCase().equals("string"))
					{
						field.set(this.obj, String.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("boolean"))
					{
						field.set(this.obj, Boolean.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("int"))
					{
						field.set(this.obj, Integer.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("double"))
					{
						field.set(this.obj, Double.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("float"))
					{
						field.set(this.obj, Float.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("short"))
					{
						field.set(this.obj, Short.valueOf(properties.get(property.key())));
					}
					else if(type.toString().toLowerCase().equals("byte"))
					{
						field.set(this.obj, Byte.valueOf(properties.get(property.key())));
					}
				}
				catch(IllegalAccessException e)
				{
					this.log.log(Level.WARNING, "Could not set field!", e);
				}
				finally
				{
					field.setAccessible(flag);
				}
			}
		}
	}

	/**
	 * Saves configuration to file
	 */
	public void save()
	{
		Map<String, String> properties = new HashMap<String, String>();

		Field[] fields = this.obj.getClass().getDeclaredFields();
		for(Field field : fields)
		{
			ConfigProperty property = field.getAnnotation(ConfigProperty.class);
			if(property != null)
			{
				try
				{
					properties.put(property.key(), String.valueOf(field.get(this.obj)));
				}
				catch(Exception e)
				{
					this.log.log(Level.WARNING, "An error occured getting property value!", e);
				}
			}
		}

		write(properties);
	}

	public void write(Map<String, String> properties)
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(this.file));

			Iterator<String> keys = properties.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				writer.write(key + "=" + properties.get(key) + "\n");
			}

			writer.flush();
		}
		catch(IOException e)
		{
			this.log.log(Level.SEVERE, "An error occured saving config file!");
		}
		finally
		{
			Close.quiet(writer);
		}
	}
}