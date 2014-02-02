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
import com.sci.cadmium.common.Globals;
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
	 * Bans file
	 */
	private ListFile bans;

	/**
	 * Ops file
	 */
	private ListFile ops;

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

		try
		{
			this.bans = new ListFile(new File(this.cadmiumHome, Globals.BANS_FILE));
			this.ops = new ListFile(new File(this.cadmiumHome, Globals.OPS_FILE));
		}
		catch(IOException e)
		{
			this.log.log(Level.SEVERE, "An error occured creating files!", e);
		}
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
				this.clients.clear();
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
					handlePacket(getClient(ip, packet.getPort()), ip, packet.getPort(), pkt);
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

	public Client getClient(InetAddress ip, int port)
	{
		for(Client client : this.clients)
		{
			if(client.getIpAddress().equals(ip) && port == client.getPort())
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

	public void sendPacket(Client client, Packet pkt)
	{
		sendPacket(client.getIpAddress(), client.getPort(), pkt);
	}

	public void broadcast(Packet pkt)
	{
		for(Client c : this.clients)
		{
			sendPacket(c, pkt);
		}
	}

	public void broadcastToAllBut(Client toSkip, Packet pkt)
	{
		for(Client c : this.clients)
		{
			if(c.equals(toSkip))
				continue;
			sendPacket(c, pkt);
		}
	}

	public void handlePacket(Client client, InetAddress ip, int port, Packet pkt)
	{
		if(pkt instanceof Packet0Connect)
		{
			Packet0Connect connectPacket = (Packet0Connect) pkt;
			if(getClient(connectPacket.getUsername()) != null)
			{
				sendPacket(ip, port, new Packet3Kick("Username in use!"));
				return;
			}

			try
			{
				Client c = new Client();
				c.setUsername(connectPacket.getUsername());
				c.setPort(port);
				c.setIpAddress(ip);

				if(this.bans.contains(c.getUsername()))
				{
					sendPacket(c, new Packet3Kick("You are banned from this server!"));
				}

				if(this.ops.contains(c.getUsername()))
				{
					c.setLevel(2);
				}

				this.clients.add(c);
				broadcast(new Packet2Message("SERVER", c.getUsername() + " connected!"));
				this.log.log(Level.INFO, c.getUsername() + " connected!");
			}
			catch(IOException e)
			{
				this.log.log(Level.WARNING, "An error occured while connecting client!", e);
			}
		}
		else if(pkt instanceof Packet1Disconnect)
		{
			Packet1Disconnect disconnectPacket = (Packet1Disconnect) pkt;
			Client c = getClient(disconnectPacket.getUsername());
			if(c != null)
			{
				this.clients.remove(c);
				broadcast(new Packet2Message("SERVER", c.getUsername() + " disconnected!"));
				this.log.log(Level.INFO, c.getUsername() + " disconnected!");
			}
		}
		else if(pkt instanceof Packet2Message) // TODO commands
		{
			Packet2Message messagePacket = (Packet2Message) pkt;
			if(messagePacket.getMessage().startsWith("/"))
			{
				String[] arguments = messagePacket.getMessage().substring(1, messagePacket.getMessage().length()).split(" ");
				handleCommand(client, arguments);
			}
			else
			{
				broadcastToAllBut(client, new Packet2Message(client.getUsername(), messagePacket.getMessage()));
				this.log.log(Level.INFO, client.getUsername() + ": " + messagePacket.getMessage());
			}
		}
	}

	public void handleCommand(Client client, String[] arguments)
	{
		if(arguments[0].equals("op"))
		{
			if(client.getLevel() == 2)
			{
				if(arguments.length == 2)
				{
					if(!client.getUsername().equals(arguments[1]))
					{
						Client c = getClient(arguments[1]);
						if(c != null)
						{
							c.setLevel(2);
							this.ops.add(c.getUsername());
						}
						else
						{
							sendPacket(client, new Packet2Message("SERVER", "User not found!"));
						}
					}
					else
					{
						sendPacket(client, new Packet2Message("SERVER", "You cannot op yourself!"));
					}
				}
				else
				{
					sendPacket(client, new Packet2Message("SERVER", "Usage: /op <name>"));
				}
			}
			else
			{
				sendPacket(client, new Packet2Message("SERVER", "You do not have permission to do that!"));
			}
		}
		else if(arguments[0].equals("deop"))
		{
			if(client.getLevel() == 2)
			{
				if(arguments.length == 2)
				{
					if(!client.getUsername().equals(arguments[1]))
					{
						Client c = getClient(arguments[1]);
						if(c != null)
						{
							c.setLevel(0);
							this.ops.remove(c.getUsername());
						}
						else
						{
							sendPacket(client, new Packet2Message("SERVER", "User not found!"));
						}
					}
					else
					{
						sendPacket(client, new Packet2Message("SERVER", "You cannot deop yourself!"));
					}
				}
				else
				{
					sendPacket(client, new Packet2Message("SERVER", "Usage: /deop <name>"));
				}
			}
			else
			{
				sendPacket(client, new Packet2Message("SERVER", "You do not have permission to do that!"));
			}
		}
		else if(arguments[0].equals("kick"))
		{
			if(client.getLevel() == 2)
			{
				if(arguments.length == 2 || arguments.length == 3)
				{
					if(!client.getUsername().equals(arguments[1]))
					{
						Client c = getClient(arguments[1]);
						if(c != null)
						{
							sendPacket(c, new Packet3Kick(arguments.length == 3 ? arguments[2] : "You were kicked from the server!"));
							this.clients.remove(c);
							broadcast(new Packet2Message("SERVER", c.getUsername() + " was kicked from the server!"));
						}
						else
						{
							sendPacket(client, new Packet2Message("SERVER", "User not found!"));
						}
					}
					else
					{
						sendPacket(client, new Packet2Message("SERVER", "You cannot kick yourself!"));
					}
				}
				else
				{
					sendPacket(client, new Packet2Message("SERVER", "Usage: /kick <name>"));
				}
			}
			else
			{
				sendPacket(client, new Packet2Message("SERVER", "You do not have permission to do that!"));
			}
		}
		else if(arguments[0].equals("ban"))
		{
			if(client.getLevel() == 2)
			{
				if(arguments.length == 2 || arguments.length == 3)
				{
					if(!client.getUsername().equals(arguments[1]))
					{
						Client c = getClient(arguments[1]);
						if(c != null)
						{
							sendPacket(c, new Packet3Kick(arguments.length == 3 ? arguments[2] : "You were banned from the server!"));
							this.clients.remove(c);
							broadcast(new Packet2Message("SERVER", c.getUsername() + " was banned from the server!"));
							this.bans.add(c.getUsername());
						}
						else
						{
							sendPacket(client, new Packet2Message("SERVER", "User not found!"));
						}
					}
					else
					{
						sendPacket(client, new Packet2Message("SERVER", "You cannot ban yourself!"));
					}
				}
				else
				{
					sendPacket(client, new Packet2Message("SERVER", "Usage: /ban <name>"));
				}
			}
			else
			{
				sendPacket(client, new Packet2Message("SERVER", "You do not have permission to do that!"));
			}
		}
		else if(arguments[0].equals("unban"))
		{
			if(client.getLevel() == 2)
			{
				if(arguments.length == 2)
				{
					if(!client.getUsername().equals(arguments[1]))
					{
						if(this.bans.contains(arguments[1]))
						{
							this.bans.remove(arguments[1]);
						}
					}
					else
					{
						sendPacket(client, new Packet2Message("SERVER", "You cannot unban yourself!"));
					}
				}
				else
				{
					sendPacket(client, new Packet2Message("SERVER", "Usage: /unban <name>"));
				}
			}
			else
			{
				sendPacket(client, new Packet2Message("SERVER", "You do not have permission to do that!"));
			}
		}
	}
}