package thread.study;

public class ThreadDeadLock {

	public static void main(String[] args) {
		new Thread1().start();
		new Thread2().start();
	}

	private static class Thread1 extends Thread{

		@Override
		public void run() {
			try {
				printA();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private static void printA() throws InterruptedException{
			System.out.println("进入A");
			synchronized(Thread1.class){
				Thread.sleep(2000);
				Thread2.printB();
				System.out.println("AAAA");
			}

		}


	}

	private static class Thread2 extends Thread{

		@Override
		public void run() {
			try {
				printB();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private static void printB() throws InterruptedException{
			System.out.println("进入B");
			synchronized(Thread2.class){
				Thread1.printA();
				//Thread.sleep(2000);
				System.out.println("BBBB");
			}
		}
	}


}
