package com.wallet.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验管理器
 */
@Component
public class ValidatorManager {

    private List<Validator> validatorList = new ArrayList<>();


    public void validate() {
        validatorList.forEach(Validator::check);
    }

    @Autowired
    private void setValidatorList(List<Validator> validatorList) {
        this.validatorList = validatorList;
    }
}
