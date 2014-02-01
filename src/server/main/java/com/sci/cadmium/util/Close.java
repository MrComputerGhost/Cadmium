package com.sci.cadmium.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class Close
{
	private Close()
	{
	}

	/**
	 * Close any closeable object quietly 
	 * @param c
	 */
	public static void quiet(Closeable c)
	{
		if(c == null)
			return;
		try
		{
			c.close();
		}
		catch(IOException e)
		{
		}
	}
}