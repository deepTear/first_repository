package thread.study;

import java.util.concurrent.atomic.AtomicInteger;

public class Test2 {

	private static AtomicInteger i = new AtomicInteger(0);

	private static int j = 1;

	private static volatile int f = 1;

	public static void main(String[] args) throws InterruptedException {
		Thread1 t1 = new Thread1("线程1");
		Thread2 t2 = new Thread2("线程2");
		Thread3 t3 = new Thread3("线程3");

		t1.start();
		//t1.join();
		t2.start();
		//t2.join();
		t3.start();
	}

	private static class Thread1 extends Thread{

		public Thread1(String name){
			this.setName(name);
		}

		@Override
		public void run() {
			run0();
		}

		private void run0(){
			int x = 0;
			while(i.get() < 75){
				while(f == 1){
					x++;
					int j = i.addAndGet(1);
					System.out.println(Thread.currentThread().getName() + "---------->" + j);
					if(x == 5){
						f = 2;
						x = 0;
						System.out.println();
					}
				}
			}
		}
	}

	private static class Thread2 extends Thread{

		public Thread2(String name){
			this.setName(name);
		}

		@Override
		public void run() {
			run0();
		}

		private void run0(){
			int x = 0;
			while(i.get() < 75){
				while(f == 2){
					x++;
					int j = i.addAndGet(1);
					System.out.println(Thread.currentThread().getName() + "---------->" + j);
					if(x == 5){
						f = 3;
						x = 0;
						System.out.println();
					}
				}
			}
		}
	}

	private static class Thread3 extends Thread{

		public Thread3(String name){
			this.setName(name);
		}

		@Override
		public void run() {
			run0();
		}

		private void run0(){
			int x = 0;
			while(i.get() < 75){
				while(f == 3){
					x++;
					int j = i.addAndGet(1);
					System.out.println(Thread.currentThread().getName() + "---------->" + j);
					if(x == 5){
						f = 1;
						x = 0;
						System.out.println();
					}
				}
			}
		}
	}
}
