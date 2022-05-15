package com.wallet.application.dto;

import lombok.Data;

/**
 * 防腐定义适配层对象，避免领域模型直接被外部适配器依赖
 */
@Data
public class OperatorRecord {
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
}
