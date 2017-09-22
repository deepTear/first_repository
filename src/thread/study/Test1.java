package thread.study;

import java.util.concurrent.CountDownLatch;



/**
 * 	Google面试题
 *
 * 	有四个线程1、2、3、4。线程1的功能就是输出1，线程2的功能就是输出2，以此类推.........现在有四个文件ABCD。初始都为空。现要让四个文件呈如下格式：

	A：1 2 3 4 1 2....

	B：2 3 4 1 2 3....

	C：3 4 1 2 3 4....

	D：4 1 2 3 4 1....

	请设计程序。
 * @author Administrator
 *
 */
public class Test1 {

	private static volatile int num = 0;

	private static volatile boolean w1 = true;
	private static volatile boolean w2 = false;
	private static volatile boolean w3 = false;
	private static volatile boolean w4 = false;

	private static StringBuffer a = new StringBuffer();
	private static StringBuffer b = new StringBuffer();
	private static StringBuffer c = new StringBuffer();
	private static StringBuffer d = new StringBuffer();

	private static CountDownLatch cdl = new CountDownLatch(4);

	public static void main(String[] args) throws InterruptedException {

		Thread t1 = new Thread(new Thread1(),"线程1");
		Thread t2 = new Thread(new Thread2(),"线程2");
		Thread t3 = new Thread(new Thread3(),"线程3");
		Thread t4 = new Thread(new Thread4(),"线程4");

		t1.start();
		//t1.join();
		t2.start();
		//t2.join();
		t3.start();
		//t3.join();
		t4.start();

		cdl.await();

		System.out.println("a---------->" + a.toString());
		System.out.println("b---------->" + b.toString());
		System.out.println("c---------->" + c.toString());
		System.out.println("d---------->" + d.toString());

	}

	private static class ThreadWrite{
		void write(StringBuffer target,String content){
			target.append(content);
		}
	}

	private static class Thread1 extends ThreadWrite implements Runnable{

		@Override
		public void run() {
			while(num < 10){
				if(w1){
					StringBuffer temp = null;
					switch(num % 4){
						case 0:
							temp = a;
							break;
						case 1:
							temp = d;
							break;
						case 2:
							temp = c;
							break;
						default:
							temp = b;
					}
					write(temp,"1");
					w1 = false;
					w2 = true;
				}
			}
			cdl.countDown();
		}
	}

	private static class Thread2 extends ThreadWrite implements Runnable{

		@Override
		public void run() {
			while(num < 10){
				if(w2){
					StringBuffer temp = null;
					switch(num % 4){
						case 0:
							temp = b;
							break;
						case 1:
							temp = a;
							break;
						case 2:
							temp = d;
							break;
						default:
							temp = c;
					}
					write(temp,"2");
					w2 = false;
					w3 = true;
				}
			}
			cdl.countDown();
		}
	}

	private static class Thread3 extends ThreadWrite implements Runnable{

		@Override
		public void run() {
			while(num < 10){
				if(w3){
					StringBuffer temp = null;
					switch(num % 4){
						case 0:
							temp = c;
							break;
						case 1:
							temp = b;
							break;
						case 2:
							temp = a;
							break;
						default:
							temp = d;
					}
					write(temp,"3");
					w3 = false;
					w4 = true;
				}
			}
			cdl.countDown();
		}
	}

	private static class Thread4 extends ThreadWrite implements Runnable{

		@Override
		public void run() {
			while(num < 10){
				if(w4){
					StringBuffer temp = null;
					switch(num % 4){
						case 0:
							temp = d;
							break;
						case 1:
							temp = c;
							break;
						case 2:
							temp = b;
							break;
						default:
							temp = a;
					}
					write(temp,"4");
					num++;
					w4 = false;
					w1 = true;
				}
			}
			cdl.countDown();
		}
	}
}
