package com.wallet.domain.service;

import com.wallet.domain.model.Account;
import com.wallet.domain.model.Money;
import com.wallet.domain.model.TransactionRecord;
import com.wallet.domain.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class WalletService {

    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAW = "WITHDRAW";
    private static final String TRANSFER = "TRANSFER";

    @Autowired
    private WalletRepository repository;

    /**
     * 存钱，自动加余额，插入交易记录
     * @param userId 存钱人userId
     * @param money money
     * @return Account 存钱之后的账户
     */
    public Account deposit(String userId, Money money) {
        //不涉及db存储事务控制省略了
        Account account = recharge(userId, money);
        repository.update(userId, account);
        repository.updateRecord(userId, buildRecord(userId, money, DEPOSIT, userId, userId));
        return account;
    }


    /**
     * 取钱，自动扣余额，插入交易记录
     * @param userId 取钱人userId
     * @param money money
     * @return Account 取钱之后的账户
     */
    public Account withdraw(String userId, Money money) {
        Account account = deduct(userId, money);
        repository.update(userId, account);
        repository.updateRecord(userId, buildRecord(userId, money, WITHDRAW, userId, userId));
        return account;
    }


    /**
     * 转账
     * @param from 来源
     * @param to 去向
     * @param money money
     */
    public void transfer(String from, String to, Money money) {
        //form扣钱
        repository.update(from, deduct(from, money));
        //to加钱
        repository.update(to, recharge(to, money));
        repository.updateRecord(from, buildRecord(from, money, TRANSFER, from, to));
    }

    public Account queryBalance(String userId) {
        //本身就是从缓存种读取，暂不设计缓存
        return repository.query(userId);
    }


    public List<TransactionRecord> queryTransactionRecord(String userId) {
        return repository.queryRecord(userId);
    }



    /**
     * 返回给账户充值money后的Account
     * @param userId userId
     * @param money 充值的money
     * @return 充值后的Account
     */
    private Account recharge(String userId, Money money) {
        return buildAccount(userId, money.getCent(), money.getCurrencyCode());
    }


    /**
     * 返回给账户扣减money后的Account
     * @param userId userId
     * @param money 扣减的money
     * @return 扣减后的Account
     */
    private Account deduct(String userId, Money money) {
        //negate变成负数之后再加
        return buildAccount(userId, new BigDecimal(money.getCent()).negate().longValue(), money.getCurrencyCode());
    }


    private Account buildAccount(String userId, Long cent, String currency) {
        Account origin = repository.query(userId);
        return Account.builder()
                .amount(new AtomicLong(origin.getAmount().addAndGet(cent)))
                .currency(currency)
                .userId(userId)
                .version(origin.getVersion() + 1)
                .gmt_create(new Date())
                .gmt_modify(new Date())
                .build();
    }


    private TransactionRecord buildRecord(String userId, Money money, String operatorType, String from, String to) {
        return TransactionRecord.builder()
                .from(from)
                .to(to)
                .operatorAmount(money.getCent())
                .operatorCurrency(money.getCurrencyCode())
                .operatorUserId(userId)
                .operatorType(operatorType)
                .gmt_create(new Date())
                .gmt_modify(new Date())
                .remark(userId + operatorType + money.getAmountString())
                .build();
    }
}
