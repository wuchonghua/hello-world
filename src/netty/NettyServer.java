package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	
	public static void main(String[] args) throws InterruptedException {
		
		ServerBootstrap b = new ServerBootstrap();
		EventLoopGroup parent = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			ChannelFuture f = b.group(parent, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel c) throws Exception {
					// TODO Auto-generated method stub
					
				}
				
			}).bind(8070).sync();
			f.channel().closeFuture().sync();
		} finally {
			parent.shutdownGracefully().sync();
			worker.shutdownGracefully().sync();
		}
		
	}

}
