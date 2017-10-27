package thread.study.io;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class NIO2Server {

	private static int port = 9999;

	private static AsynchronousServerSocketChannel asyServerChannel;

	private final static CountDownLatch cdl = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		asyServerChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		asyServerChannel.bind(address);
		ServerCompletionHandler completionHandler = new ServerCompletionHandler();
		asyServerChannel.accept(null, completionHandler);
		cdl.await();
		System.out.println("主线程结束");
	}

	private static class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Object>{

		ByteBuffer dataBuffer;

		ByteBuffer sizeBuffer = ByteBuffer.allocate(4);//客户端发送数据字节长度，缓冲数据

		int dataSize;

		Attach attach;

		ReadCompletionHandler readHandler;

		@Override
		public void completed(AsynchronousSocketChannel result, Object attachment) {
			System.out.println("有新的客户端加入");
			asyServerChannel.accept(null, this);
			attach = new Attach();
			attach.setAsySocketChannel(result);
			readHandler = new ReadCompletionHandler(attach);
			readHandler.read();
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			exc.printStackTrace();
			NIO2Server.cdl.countDown();
		}

	}

	static class ReadCompletionHandler{

		public Attach attach;

		ReadCompletionHandler(Attach attach){
			this.attach = attach;
		}

		public void read(){
			attach.getAsySocketChannel().read(attach.getSizeBuffer(), this, new CompletionHandler<Integer, ReadCompletionHandler>() {
				@Override
				public void completed(Integer result, ReadCompletionHandler attachment) {
					attach.getSizeBuffer().flip();
					if(attach.getSizeBuffer().hasRemaining()){
						attach.setDataSize(ParseUtils.byteToInt(attach.getSizeBuffer().array()));
						System.out.println("客户端消息长度---------->" + attach.getDataSize() + "  字节");

						attach.setDataBuffer(ByteBuffer.allocate(attach.getDataSize()));
						attachment.attach.asySocketChannel.read(attach.getDataBuffer(), attachment, new CompletionHandler<Integer, ReadCompletionHandler>() {
							@Override
							public void completed(Integer result, ReadCompletionHandler attachment) {
								attachment.attach.dataBuffer.flip();
								if(attachment.attach.dataBuffer.hasRemaining()){
									try {
										System.out.println("客户端消息------------>" + new String(attachment.attach.dataBuffer.array(),"UTF-8"));
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
								}
								attachment.attach.dataBuffer.clear();
								attachment.read();
							}

							@Override
							public void failed(Throwable exc, ReadCompletionHandler attachment) {
								exc.printStackTrace();
								NIO2Server.cdl.countDown();
							}
						});
					}
					attach.getSizeBuffer().clear();
				}

				@Override
				public void failed(Throwable exc, ReadCompletionHandler attachment) {
					exc.printStackTrace();
					NIO2Server.cdl.countDown();
				}

			});
		}

	}

	static class Attach{

		private ByteBuffer dataBuffer;

		private ByteBuffer sizeBuffer = ByteBuffer.allocate(4);//客户端发送数据字节长度，缓冲数据

		private int dataSize;

		private AsynchronousSocketChannel asySocketChannel;

		public ByteBuffer getDataBuffer() {
			return dataBuffer;
		}

		public void setDataBuffer(ByteBuffer dataBuffer) {
			this.dataBuffer = dataBuffer;
		}

		public ByteBuffer getSizeBuffer() {
			return sizeBuffer;
		}

		public void setSizeBuffer(ByteBuffer sizeBuffer) {
			this.sizeBuffer = sizeBuffer;
		}

		public int getDataSize() {
			return dataSize;
		}

		public void setDataSize(int dataSize) {
			this.dataSize = dataSize;
		}

		public AsynchronousSocketChannel getAsySocketChannel() {
			return asySocketChannel;
		}

		public void setAsySocketChannel(AsynchronousSocketChannel asySocketChannel) {
			this.asySocketChannel = asySocketChannel;
		}
	}
}
