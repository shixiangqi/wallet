package com.wallet.domain.repository;


import com.wallet.domain.model.Account;
import com.wallet.domain.model.TransactionRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仓储层（用内存变量代替持久化了，为了简便不设计领域驱动依赖接口的依赖倒置了）
 */
@Repository
public class WalletRepository {

    private Map<String, Account> accountMap = new ConcurrentHashMap<>();
    private Map<String, List<TransactionRecord>> recordMap = new HashMap<>();


    public void update(String userId, Account account) {
        //当userId对应的账户version比当前acct大，说明已经被其他线程更新，抛异常，避免ABA问题出现
        if (null != accountMap.get(userId) && accountMap.get(userId).getVersion() >= account.getVersion()) {
            throw new RuntimeException("account balance maybe changed, please try again");
        }
        accountMap.put(userId, account);
    }

    public Account query(String userId) {
        return Optional.ofNullable(accountMap.get(userId)).orElse(Account.builder().build());
    }



    public void updateRecord(String userId, TransactionRecord record) {
        List<TransactionRecord> origin = Optional.ofNullable(recordMap.get(userId)).orElse(new ArrayList<>());
        origin.add(record);
    }

    public List<TransactionRecord> queryRecord(String userId) {
        return recordMap.get(userId);
    }
}
