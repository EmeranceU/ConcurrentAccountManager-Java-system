public class accountThreading implements Runnable{
    public Account account;
    public double amountToWithdraw;
    public accountThreading(Account account, double amount){
        this.account = account;
        this.amountToWithdraw =amount;
    }
    @Override
    public void run() {
        account.withdraw(amountToWithdraw,Thread.currentThread().getName());

    }
}

