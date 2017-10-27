package thread.study.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NIO2Client {

	private static AsynchronousSocketChannel asyChannel;

	private static int port = 9999;

	private final static CountDownLatch cdl = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		asyChannel = AsynchronousSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		ClientCompletionHandler handler = new ClientCompletionHandler();
		asyChannel.connect(address, null, handler);
		cdl.await();
		System.out.println("主线程结束");
	}

	private static class ClientCompletionHandler implements CompletionHandler<Void, AsynchronousSocketChannel>{

		@Override
		public void completed(Void result,AsynchronousSocketChannel attachment) {
			System.out.println("--------------------------------服务器链接成功----------------------------");

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
						if(readLine.equals("close")){
							try {
								asyChannel.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
						System.out.println("用户输入---------->" + readLine);
						b = readLine.getBytes();
						data = ByteBuffer.wrap(b);
						dataSize = ByteBuffer.wrap(ParseUtils.intToByte(b.length));
						try {
							asyChannel.write(new ByteBuffer[]{dataSize,data}, 0, 2,30L, TimeUnit.SECONDS, null, new CompletionHandler<Long, Object>() {
								@Override
								public void completed(Long result, Object attachment) {
									System.out.println("-----------服务端收到");
								}

								@Override
								public void failed(Throwable exc, Object attachment) {
									exc.printStackTrace();
									NIO2Client.cdl.countDown();
								}

							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					cdl.countDown();
				}
			}).start();
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
			exc.printStackTrace();
			NIO2Client.cdl.countDown();
			try {
				attachment.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
