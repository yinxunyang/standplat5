package com.bestvike.standplat.exception;

import com.bestvike.standplat.enums.ReturnCode;

/**
 * @Classname BusinessException
 * @Description
 * @Date 2019/11/6 11:27
 * @Created by yl
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String retFlag;
    private String retMsg;

    public BusinessException(String retFlag, String retMsg) {
        this.retFlag = retFlag;
        this.retMsg = retMsg;
    }

    public BusinessException(ReturnCode returnCode) {
        this.retFlag = returnCode.toCode();
        this.retMsg = returnCode.getDesc();
    }

    public BusinessException(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getRetFlag() {
        return retFlag;
    }

    public void setRetFlag(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
