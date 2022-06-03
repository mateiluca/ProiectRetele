package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ServerProgram {
	
	public static List<String> userList = new ArrayList<>();
	public static List<String> hourList = new ArrayList<>();
	public static ArrayList<ArrayList<Integer>> res = new ArrayList<>();
	public static ArrayList<Socket> clients = new ArrayList<>();
	
	public static PrintWriter writer;

	public static void main(String[] args) {
		userList.add("Ana");
		userList.add("Ionut");
		userList.add("Gigi");
		hourList.add("");
		hourList.add("14:00->15:00");
		hourList.add("15:00->16:00");
		hourList.add("16:00->17:00");
		hourList.add("17:00->18:00");
		hourList.add("18:00->19:00");
		hourList.add("19:00->20:00");
		hourList.add("20:00->21:00");
		hourList.add("21:00->22:00");
		
		for(int i=1; i <= 31; i++) {
		    res.add(new ArrayList());
		}
		
		for (int i = 1; i <= 30; i++) 
		{
			for(int j = 0; j < 9; j++)
			{
				res.get(i).add(0);
			}
		}
		
		int port = Integer.parseInt(ResourceBundle.getBundle("settings").getString("port"));
		try (Server server = new Server(port)) {
			System.out.println(String.format("Server running on port %d, type 'exit' to close", port));
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if (command == null || "exit".equals(command)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}
	
	public static void addClient(Socket s)
	{
		clients.add(s);
	}
	
	public static void notifyAllClients(String message)
	{
		for(Socket socket : clients)
		{
			try {
				writer = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.println(message);
			writer.flush();
		}
		
	}

}