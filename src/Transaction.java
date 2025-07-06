import java.time.LocalDate;

public class Transaction {
    private static int nextId = 1;

    private int id;
    private int amount;
    private String type; // "income" or "expense"
    private String tName;
    private LocalDate date;

    public Transaction(int amount, String type, String tName, LocalDate date) {
        this.id = nextId++;
        this.amount = amount;
        this.type = type;
        this.tName = tName;
        this.date = date;
    }

    // For loading with a known ID (used in fromString)
    private Transaction(int id, int amount, String type, String tName, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.tName = tName;
        this.date = date;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // toString for file saving
    @Override
    public String toString() {
        return String.format("ID: %d,$%d,%s,%s,%s", id, amount, type, tName, date);
    }

    // fromString for file loading
    public static Transaction fromString(String line) {
        String[] parts = line.split(",", 5);
        int id = Integer.parseInt(parts[0]);
        int amount = Integer.parseInt(parts[1]);
        String type = parts[2];
        String tName = parts[3];
        LocalDate date = LocalDate.parse(parts[4]);
        return new Transaction(id, amount, type, tName, date);
    }

    // getters
    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return tName;
    }

    public LocalDate getDate() {
        return date;
    }

    // setters
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTName(String tName) {
        this.tName = tName;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}