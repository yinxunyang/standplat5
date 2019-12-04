package com.bestvike.standplat.enums;

public enum ReturnCode {

    success("00000", "成功"),
    fail("99999", "失败"),

    /*01 数据库错误*/
    qpt_insert_fail("Q0160001", "数据库新增失败"),
    qpt_update_fail("Q0160002", "数据库修改失败"),
    qpt_delete_fail("Q0160003", "数据库删除失败"),
    qpt_null_info_fail("Q0160004", "数据库没有该用户信息"),

    /*10 数据检查错误*/
    qpt_request_is_null("Q1060001", "请求对象不能为空"),
    qpt_request_param_is_null("Q1060002", "请求参数不能为空"),
    qpt_request_param_not_valid("Q1060003", "请求参数未通过校验"),
    qpt_param_lack("Q1060004", "参数缺失"),
    qpt_param_length("Q1060005", "参数长度超限"),

    /*04 应用系统错误*/
    qpt_sys_error("Q0460001", "系统异常"),

    /*05 加解密错误*/
    qpt_decrypt_error("Q0560001", "解密异常"),
    qpt_download_error("Q0560003", "下载文件失败"),

    /*20 配置信息错误*/
    qpt_not_support("Q2060001", "不支持此业务"),
    qpt_config_not_exists("Q2060002", "配置不存在"),
    qpt_config_disable("Q2060003", "配置未开启"),
    qpt_config_error("Q2060004", "配置信息有误"),
    qpt_dict_not_exists("Q2060005", "字典项未配置"),
    qpt_dict_error("Q2060006", "字典项配置错误");

    private String code;
    private String desc;


    ReturnCode(String code, String message) {
        this.code = code;
        this.desc = message;
    }

    public String toCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
