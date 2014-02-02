package com.sci.cadmium.common;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class Globals
{
	private Globals()
	{
	}
	
	/**
	 * Size of UDT packet buffers
	 */
	public static final int PACKET_BUFFER_SIZE = 1024;

	/**
	 * System property for the home location of Cadmium
	 */
	public static final String CADMIUM_HOME_PROPERTY = "cadmium.home";
	
	/**
	 * Server configuration file 
	 */
	public static final String SERVER_CONFIG = "server.cfg";
}