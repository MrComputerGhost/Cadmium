package com.sci.cadmium.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.sci.cadmium.common.config.ConfigProperty;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class ServerConfig
{
	/**
	 * Default configuration properties
	 */
	public static final Map<String, String> DEFAULTS;

	static
	{
		Map<String, String> defaults = new HashMap<String, String>();
		defaults.put("port", "8080");
		DEFAULTS = Collections.unmodifiableMap(defaults);
	}

	/**
	 * Port the server is running on
	 */
	@ConfigProperty(key = "port")
	private int serverPort;

	public ServerConfig()
	{
	}

	public int getServerPort()
	{
		return this.serverPort;
	}
}