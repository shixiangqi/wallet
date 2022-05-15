package com.wallet.application;

import com.alibaba.fastjson.JSON;
import com.wallet.application.dto.OperatorRecord;
import com.wallet.domain.model.Account;
import com.wallet.domain.model.Money;
import com.wallet.domain.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

/**
 * 组合编排领域层服务，对外提供统一功能服务
 */
@Service
public class UserService extends UserBehavior {


    @Autowired
    private WalletService walletService;

    /**
     * 用户存钱，核心是变更是钱包模型的变更，定义在应用层调用，是为了避免业务在钱包动作前后的业务定制逻辑侵入领域层
     * 当前业务场景不需要做额外的动作，后续业务定制扩展内容可以避免领域层的改动
     *
     * @param userId   用户id
     * @param amount   存钱金额
     * @param currency 币种
     * @return 存钱后余额
     */
    public Long deposit(String userId, Long amount, Currency currency) {
        return moneyOperatorTemplate(userId, amount, currency, () -> {
            Money money = new Money(amount, currency);
            Account account = walletService.deposit(userId, money);
            return account.getAmount().longValue();
        });
    }


    public Long withdraw(String userId, Long amount, Currency currency) {
        return moneyOperatorTemplate(userId, amount, currency, () -> {
            Money money = new Money(amount, currency);
            Account account = walletService.withdraw(userId, money);
            return account.getAmount().longValue();
        });
    }


    public void transfer(String from, String to, Long amount, Currency currency) {
        moneyOperatorTemplate(from, amount, currency, () -> {
            Money money = new Money(amount, currency);
            walletService.transfer(from, to, money);
            return null;
        });
    }


    public Long queryBalance(String userId) {
        return walletService.queryBalance(userId).getAmount().longValue();
    }


    public List<OperatorRecord> queryTransactionRecord(String userId) {
        String jsonString = JSON.toJSONString(walletService.queryTransactionRecord(userId));
        return JSON.parseArray(jsonString, OperatorRecord.class);
    }
}
