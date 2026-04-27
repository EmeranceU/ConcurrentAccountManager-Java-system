public class Account {
    private double Amount = 100000;
    public synchronized void withdraw(double amount ,String threadName){
        System.out.println("***** " + threadName + " starts the withdrawing with balance " + Amount + " *****");

        if (Amount >= amount){
            try{
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            Amount-= amount;
            System.out.println(threadName + " successfully withdraw. the remaining : " + Amount);
        }else{
            System.out.println("Insufficient Balance");
        }
    }
// we have to go beyond like success in real bank account and sending money btn two accounts, it's like sharing accounts
}

