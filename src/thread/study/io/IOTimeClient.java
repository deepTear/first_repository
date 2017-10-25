package thread.study.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class IOTimeClient {

	private static int port = 9999;

	public static void main(String[] args) throws IOException {
		final Socket socket = new Socket();
		InetSocketAddress inetAddress = new InetSocketAddress("127.0.0.1",9999);
		socket.connect(inetAddress);

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		String readLine = null;
		Scanner print = new Scanner(System.in);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String readLine2 = null;
					while(true){
						readLine2 = reader.readLine();
						System.out.println(readLine2);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		while(true){
			readLine = print.next();
			System.out.println("用户输入---------->" + readLine);
			writer.write(readLine);
			writer.newLine();
			writer.flush();
		}
	}
}
