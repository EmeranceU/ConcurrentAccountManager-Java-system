import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountId;
    private final String holderName;
    private double balance;
    private final List<TransactionRecord> history = new ArrayList<>();

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class TransactionRecord {
        final String operation;
        final double amount;
        final String status;
        final String target;
        final String timestamp;

        TransactionRecord(String operation, double amount, String status, String target) {
            this.operation = operation;
            this.amount    = amount;
            this.status    = status;
            this.target    = target;
            this.timestamp = LocalDateTime.now().format(fmt);
        }
    }

    public Account(String accountId, String holderName, double initialBalance) {
        this.accountId = accountId;
        this.holderName = holderName;
        this.balance = initialBalance;
    }

    public String getAccountId()  { return accountId; }
    public String getHolderName() { return holderName; }

    public synchronized double getBalance() { return balance; }

    public synchronized List<TransactionRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    private static String sanitize(String input) {
        return input.replaceAll("[\r\n\t]", "_");
    }

    public synchronized String deposit(double amount) {
        if (amount <= 0) return "Amount must be greater than zero.";
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        balance += amount;
        history.add(new TransactionRecord("DEPOSIT", amount, "SUCCESS", null));
        return null;
    }

    public synchronized String withdraw(double amount) {
        if (amount <= 0) return "Amount must be greater than zero.";
        if (balance < amount) {
            history.add(new TransactionRecord("WITHDRAW", amount, "FAILED", null));
            return "Insufficient balance. Available: " + String.format("%.2f", balance);
        }
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        balance -= amount;
        history.add(new TransactionRecord("WITHDRAW", amount, "SUCCESS", null));
        return null;
    }

    public String transfer(Account target, double amount) {
        if (amount <= 0) return "Amount must be greater than zero.";
        Account first  = accountId.compareTo(target.accountId) < 0 ? this : target;
        Account second = first == this ? target : this;
        synchronized (first) {
            synchronized (second) {
                if (balance < amount) {
                    history.add(new TransactionRecord("TRANSFER", amount, "FAILED", target.accountId));
                    return "Insufficient balance. Available: " + String.format("%.2f", balance);
                }
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                balance -= amount;
                target.balance += amount;
                history.add(new TransactionRecord("TRANSFER OUT", amount, "SUCCESS", target.accountId));
                target.history.add(new TransactionRecord("TRANSFER IN", amount, "SUCCESS", accountId));
                return null;
            }
        }
    }

    public static String sanitizeThreadName() {
        return sanitize(Thread.currentThread().getName());
    }
}
