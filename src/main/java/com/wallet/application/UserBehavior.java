package com.wallet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.function.Supplier;

/**
 * 用户行为，方便统一收口，流控，监控，统计和分析
 */
@Component
public class UserBehavior {

    @Autowired
    private ValidatorManager validatorManager;

    public <R> R moneyOperatorTemplate(String userId, Long amount, Currency currency, Supplier<R> supplier) {
        //统一校验
        validatorManager.validate();
        //其他能统一做的先省略了，上面只是做一个示例
        return supplier.get();
    }
}
