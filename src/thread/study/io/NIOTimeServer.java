package thread.study.io;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOTimeServer {

	private static int port = 9999;

	public static void main(String[] args) throws Exception {
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		serverChannel.socket().bind(address);
		serverChannel.configureBlocking(false);
		Selector selector = Selector.open();
		serverChannel.register(selector,SelectionKey.OP_ACCEPT);

		ByteBuffer data = null;
		ByteBuffer dataSize = ByteBuffer.allocate(4);
		while(true){
			selector.select();
			Set<SelectionKey> skeys = selector.selectedKeys();
			Iterator<SelectionKey> it = skeys.iterator();
			SelectionKey nextIt = null;
			while(it.hasNext()){
				nextIt = it.next();
				//it.remove();//需要调用remove 或者 SelectionKey的cancel  具体原因https://stackoverflow.com/questions/7132057/why-the-key-should-be-removed-in-selector-selectedkeys-iterator-in-java-ni
				if(nextIt.isValid()){
					if(nextIt.isAcceptable()){
						System.out.println("-----------------isAcceptable--------------------");
						// 当 OP_ACCEPT 事件到来时, 我们就有从 ServerSocketChannel 中获取一个 SocketChannel,
	                    // 代表客户端的连接
	                    // 注意, 在 OP_ACCEPT 事件中, 从 key.channel() 返回的 Channel 是 ServerSocketChannel.
	                    // 而在 OP_WRITE 和 OP_READ 中, 从 key.channel() 返回的是 SocketChannel.
	                    SocketChannel clientChannel = ((ServerSocketChannel) nextIt.channel()).accept();
	                    clientChannel.configureBlocking(false);
	                    //在 OP_ACCEPT 到来时, 再将这个 Channel 的 OP_READ 注册到 Selector 中.
	                    // 注意, 这里我们如果没有设置 OP_READ 的话, 即 interest set 仍然是 OP_ACCEPT 的话, 那么 select 方法会一直直接返回.
	                    clientChannel.register(selector,SelectionKey.OP_READ);
					}
					if(nextIt.isReadable()){
						System.out.println("-----------------isReadable-------------------");
						SocketChannel socketChannel_ = (SocketChannel)nextIt.channel();
						int data_size = 0;
						socketChannel_.read(dataSize);
						dataSize.flip();
						if(dataSize.hasRemaining()){
							data_size = ParseUtils.byteToInt(dataSize.array());
							System.out.println("客户端消息长度---------->" + data_size + "  字节");
						}
						dataSize.clear();

						data = ByteBuffer.allocate(data_size);
						socketChannel_.read(data);
						data.flip();
						if(data.hasRemaining()){
							System.out.println("客户端消息------------>" + new String(data.array(),"UTF-8"));
						}
						data.clear();
					}
				}
				it.remove();
			}
		}
	}
}
