package com.goldze.mvvmhabit.test;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/3 13:59
 * @Description: 健康码请求
 */
public class HealCodeRequestBody {
    private String username;//访客机接口帐号（宏图提供）
    private String password;//访客机接口密码（宏图提供）
    private String zjhm;//证件号码（必填）
    private String xm;//姓名（必填）
    private String sjhm;//手机号码
    private String lyd;//来源地 （省市县，中间空格隔开）比如:浙江省 台州市 椒江区

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getSjhm() {
        return sjhm;
    }

    public void setSjhm(String sjhm) {
        this.sjhm = sjhm;
    }

    public String getLyd() {
        return lyd;
    }

    public void setLyd(String lyd) {
        this.lyd = lyd;
    }
}
