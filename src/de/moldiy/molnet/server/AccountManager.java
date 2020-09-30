package de.moldiy.molnet.server;

import java.util.HashMap;

public class AccountManager {

    public enum AccountSearchType {
        name, uuid
    }

    private final HashMap<String, Account> activeAccountByName = new HashMap<>();
    private final HashMap<String, Account> activeAccountByUuid = new HashMap<>();

    public void add(Account account) {
        if(this.activeAccountByName.containsKey(account.username)) {
            throw new IllegalArgumentException("The user is already logged in!");
        }
        this.activeAccountByName.put(account.username, account);
        this.activeAccountByUuid.put(account.uuid, account);
    }

    public Account getAccount(AccountSearchType type, String s) {
        if(type == AccountSearchType.name) {
            return this.activeAccountByName.get(s);
        } else {
            return this.activeAccountByUuid.get(s);
        }
    }

}
