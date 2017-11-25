package com.huangj.retrofit_rxjava.netNestDemo;

import android.util.Log;

/**
 * Created by huangasys on 2017/11/25.10:20
 */

public class LoginClassBean {


    /**
     * status : 1
     * content : {"from":"en-EU","to":"zh-CN","vendor":"baidu","out":"嗨，登录","ciba_out":"HI登录","ciba_use":"来自机器翻译","err_no":0}
     */

    private int status;
    private ContentBean content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * from : en-EU
         * to : zh-CN
         * vendor : baidu
         * out : 嗨，登录
         * ciba_out : HI登录
         * ciba_use : 来自机器翻译
         * err_no : 0
         */

        private String from;
        private String to;
        private String vendor;
        private String out;
        private String ciba_out;
        private String ciba_use;
        private int err_no;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getOut() {
            return out;
        }

        public void setOut(String out) {
            this.out = out;
        }

        public String getCiba_out() {
            return ciba_out;
        }

        public void setCiba_out(String ciba_out) {
            this.ciba_out = ciba_out;
        }

        public String getCiba_use() {
            return ciba_use;
        }

        public void setCiba_use(String ciba_use) {
            this.ciba_use = ciba_use;
        }

        public int getErr_no() {
            return err_no;
        }

        public void setErr_no(int err_no) {
            this.err_no = err_no;
        }
    }
    //定义 输出返回数据 的方法
    public void show() {

        Log.d("RxJava", "翻译内容 = " + content.out);

    }
}
