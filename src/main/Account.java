package main;

public class Account {
    public String accName;
    public String password;
    public String ingameName;
    public String level;
    public String rank;
    public String champPool;

    public String getAccName() {
        return accName;
    }

    public String getPassword() {
        return password;
    }

    public String getIngameName() {
        return ingameName;
    }

    public String getLevel() {
        return level;
    }

    public String getRank() {
        return rank;
    }

    public String getChampPool() {
        return champPool;
    }

    public Account(String accName, String password, String ingameName, String level, String rank, String champPool) {
        this.accName = accName;
        this.password = password;
        this.ingameName = ingameName;
        this.level = level;
        this.rank = rank;
        this.champPool = champPool;
    }
}
