package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Nio {
	
	public static void main(String[] args) throws IOException {
		FileChannel f = new RandomAccessFile("", "").getChannel();
		FileChannel destF = new RandomAccessFile("", "").getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int i = f.read(buffer);
		while (i != -1) {
			destF.write(buffer);
		}
	}

}
