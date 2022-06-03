package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private boolean connected = false;
	private boolean isNumber = false;
	private boolean selectedDay = false;
	private boolean waiting = false;
	
	private int day = -1;
	private int hour;
	
	private long startTime;
	private long elapsedTime;
	
	public ClientHandler(Socket socket) throws IOException {
		ServerProgram.addClient(socket);
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream());
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				String command = reader.readLine();
				if(connected)
				{
					if(command.equals("reset"))
					{
						resetVar();
					}
					
					if(command.equals("rezervari"))
					{
						writer.println("14:00->15:00 15:00->16:00 16:00->17:00 17:00->18:00 18:00->19:00 19:00->20:00 20:00->21:00 21:00->22:00");
						for (int i = 1; i < 30; i++) 
						{
							writer.print(i + " -> ");
							for(int j = 1; j <= 8; j++)
							{
								writer.print(ServerProgram.res.get(i).get(j) + " ");
							}
							writer.println();
						}
						writer.flush();
					}
					
					if(waiting)
					{
						if(command.toLowerCase().equals("da"))
						{
							if(System.currentTimeMillis() - startTime < 10000)
							{
								writer.println("Rezervare creata!");
								writer.flush();
								ServerProgram.notifyAllClients("Rezervare creata pe data de " + day + " la ora " + hour + ".");
								resetVar();
							}
							else
							{
								writer.println("Timpul a expirat!");
								writer.flush();
								ServerProgram.notifyAllClients("Rezervare deblocata pe data de " + day + " la ora " + ServerProgram.hourList.get(hour) + ".");
								ServerProgram.res.get(day).set(hour, 0);
								resetVar();
							}
							
						}
						else if (command.toLowerCase().equals("nu"))
						{
							writer.println("Rezervare anulata.");
							writer.flush();
							ServerProgram.notifyAllClients("Rezervare deblocata pe data de " + day + " la ora " + ServerProgram.hourList.get(hour) + ".");
							ServerProgram.res.get(day).set(hour, 0);
							resetVar();
						}
						else
						{
							writer.println("Raspundeti cu 'da' sau 'nu'.");
							writer.flush();
						}
					}
					else
					{
						int val = 100;
						try {
							val = Integer.parseInt(command);
							isNumber = true;
						}catch (NumberFormatException e) {
							isNumber = false;
						}
						
						if(isNumber) 
						{
							if(selectedDay)
							{
								if(val > 0 && val < 9)
								{
									if(ServerProgram.res.get(day).get(val) == 0)
									{
										hour = val;
										waiting = true;
										ServerProgram.notifyAllClients("Rezervare blocata pe data de " + day + " la ora " + ServerProgram.hourList.get(hour) + ".");
										startTime = System.currentTimeMillis();
										ServerProgram.res.get(day).set(val, 1);
										writer.println("Confirmati rezervarea? (aveti 10s)");
										writer.flush();
									}
									else
									{
										writer.println("Exista deja o programare, alegeti alta ora.");
										writer.flush();
									}
									
								}
								else
								{
									writer.println("Nu exista ora dorita.");
									writer.flush();
								}
							}
							else
							{
								if(val > 0 && val < 30)
								{
									day = val;
									selectedDay = true;
									writer.println("Alegeti ora.");
									writer.flush();
								}
								else
								{
									writer.println("Nu exista data dorita.");
									writer.flush();
								}
							}
						}
					}
				}else {
					for (String s : ServerProgram.userList) 
					{
						if(command.equals(s))
						{
							connected = true;
						}
					}
					if(!connected)
					{
						writer.println("Utilizatorul nu exista");
						writer.flush();
					}else {
						writer.println("Conectat!");
						writer.println("14:00->15:00 15:00->16:00 16:00->17:00 17:00->18:00 18:00->19:00 19:00->20:00 20:00->21:00 21:00->22:00");
						for (int i = 1; i < 30; i++) 
						{
							writer.print(i + " -> ");
							for(int j = 1; j <= 8; j++)
							{
								writer.print(ServerProgram.res.get(i).get(j) + " ");
							}
							writer.println();
						}
						writer.flush();
					}
				}
			} catch (Exception e) {
				writer.println(e.getMessage());
				writer.flush();
			}	
		}		
	}
	
	private void resetVar()
	{
		selectedDay = false;
		day = -1;
		hour = -1;
		waiting = false;
	}
	
}