package com.esun.socialmedia.security;

import com.esun.socialmedia.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定義 UserDetails 實作
 * 
 * 包裝 User 實體以符合 Spring Security 的 UserDetails 介面
 * 
 * @author 開發團隊
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * 獲取使用者權限
     * 目前所有使用者都有 USER 角色
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 獲取密碼
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * 獲取使用者名稱
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 帳號是否未過期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 帳號是否未鎖定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 憑證是否未過期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 帳號是否啟用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 獲取使用者實體
     */
    public User getUser() {
        return user;
    }

    /**
     * 獲取使用者 ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * 獲取電子郵件
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 獲取個人簡介
     */
    public String getBiography() {
        return user.getBiography();
    }

    /**
     * 獲取封面圖片
     */
    public String getCoverImage() {
        return user.getCoverImage();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserPrincipal that = (UserPrincipal) obj;
        return user.getId().equals(that.user.getId());
    }

    @Override
    public int hashCode() {
        return user.getId().hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserPrincipal{id=%d, username='%s', email='%s'}", 
            user.getId(), user.getUsername(), user.getEmail());
    }
}
