package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientProgram {

	public static void main(String[] args) {
		int port = Integer.parseInt(ResourceBundle.getBundle("settings").getString("port"));
		String hostname = ResourceBundle.getBundle("settings").getString("host");
		try (Socket socket = new Socket(hostname, port)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			try(Scanner scanner = new Scanner(System.in)) {
				new Thread(() -> {
					String response;
					try {
						while(true) 
						{
							response = reader.readLine();
							System.out.println(response);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();
				while(true) {
					String command = scanner.nextLine();
					writer.println(command);
					writer.flush();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}