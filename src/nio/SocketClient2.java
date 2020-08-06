package nio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient2 {
	
	public static void main(String[] args) throws IOException {
		client();
	}
	
	private static void client() throws IOException {
		Socket client = new Socket("127.0.0.1", 12345);
		
		Reader r = new BufferedReader(new InputStreamReader(System.in));
		Writer w =  new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		while (true) {
			
			char[] c = new char[1024];
			int i = r.read(c);
			System.out.println(i);
			
			w.write(c, 0, i);
			w.flush();
			
		}
	}

}
