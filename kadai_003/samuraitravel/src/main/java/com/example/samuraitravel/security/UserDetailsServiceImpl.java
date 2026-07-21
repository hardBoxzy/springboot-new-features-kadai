package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;    
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;        
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  
        try {
            User user = userRepository.findByEmail(email);//フォームから送信されたメールアドレスに一致するユーザーを取得する
            String userRoleName = user.getRole().getName();//そのユーザーのロールを取得する
            Collection<GrantedAuthority> authorities = new ArrayList<>();         
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            return new UserDetailsImpl(user, authorities);//上記2つの情報をUserDetailsImplクラスのコンストラクタに渡し、インスタンスを生成する
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }   
}