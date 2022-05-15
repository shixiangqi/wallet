package com.wallet.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 交易记录模型
 */
@Data
@Builder
public class TransactionRecord {
    /**
     * 操作类型
     */
    private String operatorType;
    /**
     * 操作金额
     */
    private Long operatorAmount;
    /**
     * 操作币种
     */
    private String operatorCurrency;
    /**
     * 操作人
     */
    private String operatorUserId;
    /**
     * 交易动作的来源
     */
    private String from;
    /**
     * 交易动作的去向
     */
    private String to;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date gmt_create;
    /**
     * 更新时间
     */
    private Date gmt_modify;
}
