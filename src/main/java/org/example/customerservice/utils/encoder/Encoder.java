package org.example.customerservice.utils.encoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }
    public static boolean checkPassword(String password, String savedPassword) {
        return encoder.matches(password, savedPassword);
    }
}
