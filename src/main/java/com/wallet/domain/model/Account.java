package com.wallet.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 账户模型
 */
@Data
@Builder
public class Account {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 原子变量保证原子性，单机的话可以放弃锁的开销提升性能
     */
    private AtomicLong amount;
    /**
     * 币种
     */
    private String currency;
    /**
     * version避免并发ABA问题
     */
    private Long version;
    /**
     * 创建时间
     */
    private Date gmt_create;
    /**
     * 更新时间
     */
    private Date gmt_modify;
}
