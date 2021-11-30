package com.whaleal.icefrog.extra.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 用户名密码验证器
 *
 * @author looly
 */
public class UserPassAuthenticator extends Authenticator {

    private final String user;
    private final String pass;

    /**
     * 构造
     *
     * @param user 用户名
     * @param pass 密码
     */
    public UserPassAuthenticator( String user, String pass ) {
        this.user = user;
        this.pass = pass;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.user, this.pass);
    }

}
