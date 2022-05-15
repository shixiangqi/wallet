package com.wallet.adapter.facade;

import com.wallet.application.UserService;
import com.wallet.application.dto.OperatorRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Currency;
import java.util.List;

/**
 * 定义用户可操作的功能（区别于应用层接口，这个接口所有的语义都是去业务特性和去领域模型语义的）
 * 适配用户的请求，转发给应用层（一般是适配协议+请求模型转换）
 */
@Controller
public class UserFacade {

    @Autowired
    private UserService userService;

    /**
     * 存钱
     * @param userId 用户标识
     * @param amount 金额
     * @param currency 币种
     * @return 存后金额
     */
    public Long deposit(String userId, Long amount, Currency currency) {
        return userService.deposit(userId, amount, currency);
    }


    /**
     * 取钱
     * @param userId
     * @param amount
     * @param currency
     * @return
     */
    public Long withdraw(String userId, Long amount, Currency currency) {
        return userService.withdraw(userId, amount, currency);
    }


    /**
     * 转账
     * @param userId 转账人标识
     * @param amount 转账金额
     * @param currency 转账币种
     * @param targetUserId 到账人标识
     */
    public void transfer(String userId, Long amount, Currency currency, String targetUserId) {
        userService.transfer(userId, targetUserId, amount, currency);
    }


    /**
     * 查询余额
     * @param userId 用户标识
     * @return 余额
     */
    public Long queryBalance(String userId) {
        return userService.queryBalance(userId);
    }


    /**
     * 查询历史账单
     * @param userId  用户标识
     * @return 历史账单信息
     */
    public List<OperatorRecord> queryTransactionRecord(String userId) {
        return userService.queryTransactionRecord(userId);
    }
}
