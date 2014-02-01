package com.sci.cadmium.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sci.cadmium.Globals;
import com.sci.cadmium.Server;
import com.sci.cadmium.config.Configuration;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class StandardServer implements Server
{
	/**
	 * Cadmium home directory
	 */
	private File cadmiumHome;

	/**
	 * Cadmium server config
	 */
	private ServerConfig config;

	/**
	 * Cadmium root logger
	 */
	private Logger log;

	/**
	 * True while server is running
	 */
	private boolean running;

	/**
	 * True while the server is shutting down
	 */
	private boolean stopping;

	/**
	 * Standard Cadmium server
	 * 
	 * @param cadmiumHome
	 */
	public StandardServer(String cadmiumHome)
	{
		this.cadmiumHome = new File(cadmiumHome);
		this.config = new ServerConfig();
		this.log = Logger.getLogger("Cadmium");
	}

	@Override
	public void start()
	{
		this.log.log(Level.INFO, "Starting Cadmium Server...");

		if(!this.cadmiumHome.exists())
		{
			this.log.log(Level.FINE, "Creating Cadmium home directory");
			this.cadmiumHome.mkdirs();
		}

		boolean first = false;

		File cfgFile = new File(this.cadmiumHome, Globals.SERVER_CONFIG);
		if(!cfgFile.exists())
		{
			try
			{
				cfgFile.createNewFile();
				first = true;
			}
			catch(IOException e)
			{
				this.log.log(Level.SEVERE, "An error occured creating server configuration file!", e);
				System.exit(1);
			}
		}

		Configuration configuration = new Configuration(cfgFile, this.config);

		if(first)
		{
			configuration.write(ServerConfig.DEFAULTS);
		}

		configuration.load();
	}

	@Override
	public void stop()
	{
		this.log.log(Level.INFO, "Shutting down Cadmium server...");
		this.stopping = true;
	}

	@Override
	public void run()
	{
		while(this.running)
		{
			if(this.stopping)
			{
				
			}
			else
			{
				
			}
		}
	}
}