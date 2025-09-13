package com.esun.socialmedia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 密碼服務類別
 * 
 * 提供密碼加密、驗證和強度檢查功能
 * 
 * @author 開發團隊
 */
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    // 密碼強度正則表達式：至少 8 字元，包含英文字母和數字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );

    @Autowired
    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 加密密碼
     * 
     * @param rawPassword 原始密碼
     * @return 加密後的密碼
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密碼不能為空");
        }
        
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 驗證密碼
     * 
     * @param rawPassword 原始密碼
     * @param encodedPassword 加密後的密碼
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 檢查密碼強度
     * 
     * @param password 密碼
     * @return 是否符合強度要求
     */
    public boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 獲取密碼強度要求說明
     * 
     * @return 密碼要求說明
     */
    public String getPasswordRequirements() {
        return "密碼必須至少 8 字元，包含英文字母和數字";
    }

    /**
     * 驗證密碼並返回詳細錯誤訊息
     * 
     * @param password 密碼
     * @return 錯誤訊息，如果密碼有效則返回 null
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密碼不能為空";
        }
        
        if (password.length() < 8) {
            return "密碼長度至少需要 8 字元";
        }
        
        if (password.length() > 128) {
            return "密碼長度不能超過 128 字元";
        }
        
        if (!password.matches(".*[a-zA-Z].*")) {
            return "密碼必須包含至少一個英文字母";
        }
        
        if (!password.matches(".*\\d.*")) {
            return "密碼必須包含至少一個數字";
        }
        
        // 檢查是否包含不允許的字元
        if (!password.matches("[a-zA-Z\\d@$!%*?&]+")) {
            return "密碼只能包含英文字母、數字和特殊字元 @$!%*?&";
        }
        
        return null; // 密碼有效
    }

    /**
     * 生成隨機密碼（用於測試或重置密碼）
     * 
     * @param length 密碼長度
     * @return 隨機密碼
     */
    public String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        StringBuilder password = new StringBuilder();
        
        // 確保至少包含一個字母和一個數字
        password.append((char) ('A' + Math.random() * 26));
        password.append((char) ('0' + Math.random() * 10));
        
        // 填充剩餘字元
        for (int i = 2; i < length; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        
        // 打亂字元順序
        return shuffleString(password.toString());
    }

    /**
     * 打亂字串順序
     */
    private String shuffleString(String string) {
        char[] chars = string.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
