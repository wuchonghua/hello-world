package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class NettyClient {
	
	public static void main(String[] args) throws InterruptedException {
		Bootstrap b = new Bootstrap();
		EventLoopGroup g = new NioEventLoopGroup();
		try {
			ChannelFuture f = b.group(g).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel c) throws Exception {
					
				}
				
			}).connect("127.0.0.1", 8070).sync();
			f.channel().closeFuture().sync();
		} finally {
			g.shutdownGracefully().sync();
		}
	}

}
