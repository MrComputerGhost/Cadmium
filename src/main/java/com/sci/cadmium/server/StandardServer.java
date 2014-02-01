package com.sci.cadmium.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sci.cadmium.Globals;
import com.sci.cadmium.Server;
import com.sci.cadmium.config.Configuration;
import com.sci.cadmium.packet.Packet;

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
	 * UDP socket for server to communuicate on
	 */
	private DatagramSocket socket;

	/**
	 * Main server thread
	 */
	private Thread thread;

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
		this.thread = new Thread(this, "Cadmium");
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
			this.log.log(Level.INFO, "Generated default config file!");
			configuration.write(ServerConfig.DEFAULTS);
		}

		configuration.load();

		try
		{
			this.socket = new DatagramSocket(this.config.getServerPort());
		}
		catch(SocketException e)
		{
			this.log.log(Level.SEVERE, "Could not bind port!", e);
			System.exit(1);
		}

		this.running = true;
		this.thread.start();
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
		try
		{
			this.log.log(Level.INFO, "Listening on UDP:" + InetAddress.getLocalHost().getHostAddress() + ":" + this.config.getServerPort());
		}
		catch(UnknownHostException e)
		{
			this.log.log(Level.SEVERE, "Unable to get localhost!", e);
			System.exit(1);
		}

		while(this.running)
		{
			if(this.stopping)
			{

			}
			else
			{
				try
				{
					byte[] data = new byte[Globals.PACKET_BUFFER_SIZE];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					this.socket.receive(packet);
					InetAddress ip = packet.getAddress();
					data = packet.getData();
					DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
					int packetID = din.readInt();
					Packet pkt = Packet.createPacket(packetID);
					handlePacket(ip, pkt);
				}
				catch(Exception e)
				{
					this.log.log(Level.WARNING, "An error occured receiving packet!", e);
				}
			}
		}
	}

	public void handlePacket(InetAddress ip, Packet pkt)
	{

	}
}