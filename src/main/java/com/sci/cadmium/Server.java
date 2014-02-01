package com.sci.cadmium;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public interface Server extends Runnable
{
	public void start();
	
	public void stop();
}