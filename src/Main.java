import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Map<String, Account> accounts = new LinkedHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        String[][] seed = {
                {"ACC-1", "Aline NZIKWINKUNDA",   "100000"},
                {"ACC-2", "Bob KAMARI",      "50000"},
                {"ACC-3", "Carol RWANGOMBWA ",     "75000"},
                {"ACC-4", "David KARENZI",  "200000"},
                {"ACC-5", "Eve MBABAZI",       "30000"},
                {"ACC-6", "Frank BIZIMANA",    "120000"},
                {"ACC-7", "Grace TETA",  "60000"},
                {"ACC-8", "Henry NZARAMBA",      "90000"}
        };
        for (String[] row : seed) {
            accounts.put(row[0], new Account(row[0], row[1], Double.parseDouble(row[2])));
        }

        Scanner scanner = new Scanner(System.in);
        List<Thread> threads = new ArrayList<>();
        int userCount = 1;
        int transactionCount = 0;

        // --- Section 1: One-time login ---
        System.out.println("Concurrent Bank Account System");
        System.out.println("--------------------------------");
        Account loggedIn = null;
        while (loggedIn == null) {
            System.out.print("Enter your account ID: ");
            String id = scanner.nextLine().trim().toUpperCase();
            loggedIn = accounts.get(id);
            if (loggedIn == null) {
                System.out.println("Account not found. Try again.");
            }
        }
        System.out.println();
        System.out.println("Welcome, " + loggedIn.getHolderName());
        System.out.printf("Current Balance: %.2f%n", loggedIn.getBalance());

        // --- Section 3: Persistent menu ---
        while (true) {
            System.out.println();
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View All Accounts");
            System.out.println("5. Check Balance");
            System.out.println("6. View My Transactions");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");
            String option = scanner.nextLine().trim();

            if (option.equals("7")) break;

            if (option.equals("4")) {
                System.out.println();
                for (Account a : accounts.values()) {
                    System.out.println("Account ID : " + a.getAccountId());
                    System.out.println("Holder     : " + a.getHolderName());
                    System.out.println();
                }
                continue;
            }

            if (option.equals("5")) {
                System.out.println();
                System.out.println("Account  : " + loggedIn.getAccountId());
                System.out.println("Holder   : " + loggedIn.getHolderName());
                System.out.printf("Balance  : %.2f%n", loggedIn.getBalance());
                System.out.println();
                continue;
            }

            if (option.equals("6")) {
                System.out.println();
                List<Account.TransactionRecord> history = loggedIn.getHistory();
                if (history.isEmpty()) {
                    System.out.println("No transactions yet.");
                } else {
                    System.out.println("Transaction History — " + loggedIn.getAccountId());
                    System.out.println("--------------------------------");
                    for (int i = 0; i < history.size(); i++) {
                        Account.TransactionRecord r = history.get(i);
                        System.out.println("#" + (i + 1));
                        System.out.println("Operation : " + r.operation);
                        System.out.printf("Amount    : %.2f%n", r.amount);
                        if (r.target != null) System.out.println("Account   : " + r.target);
                        System.out.println("Status    : " + r.status);
                        System.out.println("Time      : " + r.timestamp);
                        System.out.println();
                    }
                    System.out.println("Total : " + history.size() + " transaction(s)");
                }
                System.out.println();
                continue;
            }

            if (!option.equals("1") && !option.equals("2") && !option.equals("3")) {
                System.out.println("Invalid option. Enter a number from 1 to 7.");
                continue;
            }

            System.out.print("Amount: ");
            double amount;
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
                continue;
            }

            TransactionTask task;
            String threadName = "User-" + userCount++;

            if (option.equals("3")) {
                System.out.print("Target account ID: ");
                String targetId = scanner.nextLine().trim().toUpperCase();
                Account target = accounts.get(targetId);
                if (target == null) {
                    System.out.println("Target account not found.");
                    continue;
                }
                if (loggedIn == target) {
                    System.out.println("Cannot transfer to your own account.");
                    continue;
                }
                task = new TransactionTask(loggedIn, target, amount, TransactionTask.Operation.TRANSFER, null);
            } else {
                TransactionTask.Operation op = option.equals("1")
                        ? TransactionTask.Operation.DEPOSIT
                        : TransactionTask.Operation.WITHDRAW;
                task = new TransactionTask(loggedIn, amount, op, null);
            }

            Thread thread = new Thread(task, threadName);
            thread.setDaemon(false);
            threads.add(thread);
            thread.start();
            thread.join();
            transactionCount++;
            System.out.println("Transaction complete.");
        }

        System.out.println();
        System.out.println("Waiting for all transactions to complete...");
        for (Thread t : threads) t.join();
        System.out.println();
        System.out.println("Session Summary");
        System.out.println("---------------");
        System.out.println("Account  : " + loggedIn.getAccountId());
        System.out.println("Holder   : " + loggedIn.getHolderName());
        System.out.println("Transactions completed : " + transactionCount);
        System.out.println("---------------");
        System.out.println("Goodbye, " + loggedIn.getHolderName() + ".");
        scanner.close();
    }
}
