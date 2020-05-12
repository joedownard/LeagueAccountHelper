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

    public String getCensoredPassword() {
        return "*******";
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Account) {
            return ((Account) obj).accName.equals(accName) &&
                    ((Account) obj).password.equals(password) &&
                    ((Account) obj).ingameName.equals(ingameName) &&
                    ((Account) obj).level.equals(level) &&
                    ((Account) obj).rank.equals(rank) &&
                    ((Account) obj).champPool.equals(champPool);
        }
        return false;
    }

    public Account () {

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
