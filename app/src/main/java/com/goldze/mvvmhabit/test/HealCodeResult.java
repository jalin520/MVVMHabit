package com.goldze.mvvmhabit.test;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/3 13:59
 * @Description: 健康码请求
 */
public class HealCodeResult {
    private int rc;//	状态码 0 正常 1异常
    private String err;//	状态码说明
    private Object ret;//
    private String id;//	id
    private String sfzh;//	身份证号码
    private String xm;//	姓名
    private String mffd;//	码发放地
    private String mzt;//	码状态
    private String hmcmyy;//	红马，黄码原因
    private String source;//	来源
    private String sjh;//	手机号码
    private String mlx;//	码类型
    private String scsqsj;//	首次申请时间
    private String scffsj;//	首次发放时间
    private String zjgxsj;//	最近更新时间
    private String zjlx;//	证件类型

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getMffd() {
        return mffd;
    }

    public void setMffd(String mffd) {
        this.mffd = mffd;
    }

    public String getMzt() {
        return mzt;
    }

    public void setMzt(String mzt) {
        this.mzt = mzt;
    }

    public String getHmcmyy() {
        return hmcmyy;
    }

    public void setHmcmyy(String hmcmyy) {
        this.hmcmyy = hmcmyy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSjh() {
        return sjh;
    }

    public void setSjh(String sjh) {
        this.sjh = sjh;
    }

    public String getMlx() {
        return mlx;
    }

    public void setMlx(String mlx) {
        this.mlx = mlx;
    }

    public String getScsqsj() {
        return scsqsj;
    }

    public void setScsqsj(String scsqsj) {
        this.scsqsj = scsqsj;
    }

    public String getScffsj() {
        return scffsj;
    }

    public void setScffsj(String scffsj) {
        this.scffsj = scffsj;
    }

    public String getZjgxsj() {
        return zjgxsj;
    }

    public void setZjgxsj(String zjgxsj) {
        this.zjgxsj = zjgxsj;
    }

    public String getZjlx() {
        return zjlx;
    }

    public void setZjlx(String zjlx) {
        this.zjlx = zjlx;
    }
}
