package thread.study.io;

import java.nio.channels.SelectionKey;

public class Test {

	public static void main(String[] args) {
		System.out.println(Integer.toBinaryString(SelectionKey.OP_CONNECT|SelectionKey.OP_WRITE));
	}
}
