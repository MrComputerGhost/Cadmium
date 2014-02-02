package com.sci.cadmium.server;


/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class Cadmium
{
	/**
	 * The server instance
	 */
	private static Server server = null;

	public static void main(String[] args)
	{
		String cadmiumHome = System.getProperty(Globals.CADMIUM_HOME_PROPERTY);
		if(cadmiumHome == null)
		{
			System.err.println("cadmium.home must be set!");
			System.exit(1);
		}

		Cadmium.server = new StandardServer(cadmiumHome);
		Cadmium.server.start();
	}
}