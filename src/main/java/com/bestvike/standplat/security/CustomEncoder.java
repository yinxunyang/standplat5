package com.bestvike.standplat.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by lihua on 2017/5/3.
 */
public class CustomEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return true;
        // return rawPassword.equals(encodedPassword);
        // return true;
    }
}
