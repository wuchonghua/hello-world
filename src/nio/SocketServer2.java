package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class SocketServer2 {
	
	public static void main(String[] args) throws IOException {
		server();
	}
	
	private static void server() throws IOException {
		ServerSocketChannel server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(new InetSocketAddress("127.0.0.1", 12345));
		Selector selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			System.out.println("select");
			
			int eventNum = selector.select();
			if (eventNum > 0) {
				System.out.println("监听到事件");
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey s = iterator.next();
					if (s.isAcceptable()) {
						SocketChannel client = ((ServerSocketChannel)s.channel()).accept();
						if (client != null) {
							System.out.println("监听客户端通道");
							client.configureBlocking(false);
							client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						}
						
					} else if (s.isWritable()) {
						SocketChannel client = (SocketChannel)s.channel();
						
					} else if (s.isReadable()) {
						SocketChannel client = (SocketChannel)s.channel();
						
						ByteBuffer b = ByteBuffer.allocate(1024);
						b.clear();
						client.read(b);
						b.flip();
						System.out.println("接收到来自客户端的信息:"
			                    + Charset.forName("UTF-8").newDecoder()
			                    .decode(b).toString());
						
					}
					iterator.remove();
				}
				
			}
		}
	}
	
	
	

}
