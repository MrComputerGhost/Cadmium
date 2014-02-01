package com.sci.cadmium.startup;

import java.util.logging.Logger;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class Cadmium
{
	public static final Cadmium INSTANCE = new Cadmium();

	private Logger log;

	private Cadmium()
	{
		this.log = Logger.getLogger("Cadmium");
	}

	public Logger getLog()
	{
		return this.log;
	}
}