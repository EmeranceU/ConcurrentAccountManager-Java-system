public class Main {
    public static void main(String[] args) throws InterruptedException {
        Account account = new Account();

        Thread thread1 = new Thread(new accountThreading(account, 30000), "person1");
        Thread thread2 = new Thread(new accountThreading(account, 90000), "person2");

        thread1.start();
        thread2.start();
    }
}

