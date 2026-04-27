import java.security.SecureRandom;

public class TransactionTask implements Runnable {
    public enum Operation { DEPOSIT, WITHDRAW, TRANSFER }

    private static final SecureRandom random = new SecureRandom();

    private final Account account;
    private final Account target;
    private final double amount;
    private final Operation operation;
    private final Runnable onComplete;

    public TransactionTask(Account account, Account target, double amount, Operation operation, Runnable onComplete) {
        this.account = account;
        this.target = target;
        this.amount = amount;
        this.operation = operation;
        this.onComplete = onComplete;
    }

    public TransactionTask(Account account, double amount, Operation operation, Runnable onComplete) {
        this(account, null, amount, operation, onComplete);
    }

    @Override
    public void run() {
        try { Thread.sleep(random.nextInt(300)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String failure;
        String thread = Account.sanitizeThreadName();

        switch (operation) {
            case DEPOSIT:
                failure = account.deposit(amount);
                printLog(thread, "DEPOSIT", account, null, amount, failure);
                break;
            case WITHDRAW:
                failure = account.withdraw(amount);
                printLog(thread, "WITHDRAW", account, null, amount, failure);
                break;
            case TRANSFER:
                failure = (target != null) ? account.transfer(target, amount) : "No target account.";
                printLog(thread, "TRANSFER", account, target, amount, failure);
                break;
            default:
                break;
        }

        if (onComplete != null) onComplete.run();
    }

    private static synchronized void printLog(String thread, String operation,
                                              Account source, Account target,
                                              double amount, String failure) {
        boolean success = (failure == null);
        System.out.println();
        System.out.println("[" + thread + " | " + operation + " | " + (success ? "SUCCESS" : "FAILED") + "]");
        System.out.println();
        if (target != null) {
            System.out.println("From   : " + source.getAccountId());
            System.out.println("To     : " + target.getAccountId());
        } else {
            System.out.println("Account : " + source.getAccountId());
        }
        System.out.printf("Amount : %.2f%n", amount);
        System.out.println();
        if (success) {
            System.out.println("Updated Balances:");
            System.out.printf(source.getAccountId() + " -> %.2f%n", source.getBalance());
            if (target != null) {
                System.out.printf(target.getAccountId() + " -> %.2f%n", target.getBalance());
            }
        } else {
            System.out.println("Reason : " + failure);
        }
        System.out.println();
    }
}
