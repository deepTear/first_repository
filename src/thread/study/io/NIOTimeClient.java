package thread.study.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOTimeClient {

	private static int port = 9999;

	private static boolean close = false;

	private static SocketChannel channel;

	private static Selector selector;

	public static void main(String[] args) throws Exception {
		channel = SocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		/*channel.configureBlocking(false);
		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT);*/
		channel.connect(address);

		new Thread(new Runnable() {
			@Override
			public void run() {
				ByteBuffer data = null;
				ByteBuffer dataSize = null;//数据长度
				String readLine = null;
				Scanner print = new Scanner(System.in);
				byte[] b = null;
				while(true){
					readLine = print.next();
					System.out.println("用户输入---------->" + readLine);
					b = readLine.getBytes();
					data = ByteBuffer.wrap(b);
					dataSize = ByteBuffer.wrap(ParseUtils.intToByte(b.length));
					try {
						channel.write(new ByteBuffer[]{dataSize,data});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();


		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						selector.select();
						Iterator<SelectionKey> it = selector.selectedKeys().iterator();
						SelectionKey next = null;
						while(it.hasNext()){
							next = it.next();
							if(next.isValid()){
								if(next.isConnectable()){
									System.out.println("---------------------客户端链接成功!");
									//channel.register(selector,SelectionKey.OP_WRITE);
									new Thread(new Runnable() {
										@Override
										public void run() {
											ByteBuffer bbuffer = ByteBuffer.allocate(512);
											String readLine = null;
											Scanner print = new Scanner(System.in);
											while(true){
												readLine = print.next();
												System.out.println("用户输入---------->" + readLine);
												//bbuffer.flip();
												bbuffer.put(readLine.getBytes());
											}
										}
									}).start();
								}
							}
							next.cancel();
							//it.remove();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();*/
	}

	/*public static void closeConnection() throws IOException{
		if(selector != null){
			selector.close();
		}
		if(channel != null){
			channel.close();
		}
	}*/
}
