package com.sci.cadmium.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sci.cadmium.common.config.Configuration;
import com.sci.cadmium.common.packet.Packet;
import com.sci.cadmium.common.packet.Packet0Connect;
import com.sci.cadmium.common.packet.Packet1Disconnect;
import com.sci.cadmium.common.packet.Packet2Message;
import com.sci.cadmium.common.packet.Packet3Kick;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class StandardServer implements Server
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
	 * Clients connected to the server
	 */
	private List<Client> clients;

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
		this.clients = new ArrayList<Client>();
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
				broadcast(new Packet3Kick("Server stopping!"));
				this.running = false;
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
					pkt.read(din);
					handlePacket(ip, packet.getPort(), pkt);
				}
				catch(Exception e)
				{
					this.log.log(Level.WARNING, "An error occured receiving packet!", e);
				}
			}
		}
	}

	public Client getClient(String username)
	{
		if(username == null)
			return null;
		for(Client client : this.clients)
		{
			if(username.equals(client.getUsername()))
				return client;
		}
		return null;
	}

	public Client getClient(InetAddress ip)
	{
		for(Client client : this.clients)
		{
			if(client.getIpAddress().equals(ip))
				return client;
		}
		return null;
	}

	public void sendPacket(InetAddress ip, int port, Packet pkt) // TODO
																	// encrypted
																	// packets
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(baos);

			dout.writeInt(pkt.getID());
			pkt.write(dout);

			byte[] data = baos.toByteArray();
			this.socket.send(new DatagramPacket(data, data.length, ip, port));
		}
		catch(IOException e)
		{
			this.log.log(Level.WARNING, "An error occured sending packet!", e);
		}
	}

	public void broadcast(Packet pkt)
	{
		for(Client c : this.clients)
		{
			sendPacket(c.getIpAddress(), c.getPort(), pkt);
		}
	}

	public void broadcastToAllBut(InetAddress toSkip, Packet pkt)
	{
		for(Client c : this.clients)
		{
			if(c.getIpAddress().equals(toSkip))
				continue;
			sendPacket(c.getIpAddress(), c.getPort(), pkt);
		}
	}

	public void handlePacket(InetAddress ip, int port, Packet pkt)
	{
		if(pkt instanceof Packet0Connect)
		{
			Packet0Connect connectPacket = (Packet0Connect) pkt;
			if(getClient(connectPacket.getUsername()) != null)
			{
				sendPacket(ip, getClient(connectPacket.getUsername()).getPort(), new Packet3Kick("Username in use!"));
				return;
			}

			try
			{
				Client client = new Client(ip);
				client.setUsername(connectPacket.getUsername());
				client.setPort(port);
				this.clients.add(client);
				broadcastToAllBut(ip, new Packet2Message(client.getUsername() + " connected!"));
				this.log.log(Level.INFO, client.getUsername() + " connected!");
			}
			catch(IOException e)
			{
				this.log.log(Level.WARNING, "An error occured while connecting client!", e);
			}
		}
		else if(pkt instanceof Packet1Disconnect)
		{
			Packet1Disconnect disconnectPacket = (Packet1Disconnect) pkt;
			Client client = getClient(disconnectPacket.getUsername());
			if(client != null)
			{
				this.clients.remove(client);
				broadcastToAllBut(ip, new Packet2Message(client.getUsername() + " disconnected!"));
				this.log.log(Level.INFO, client.getUsername() + " disconnected!");
			}
		}
		else if(pkt instanceof Packet2Message) // TODO commands
		{
			Packet2Message messagePacket = (Packet2Message) pkt;
			broadcastToAllBut(ip, new Packet2Message(messagePacket.getMessage()));
			this.log.log(Level.INFO, getClient(ip).getUsername() + ": " + messagePacket.getMessage());
		}
	}
}