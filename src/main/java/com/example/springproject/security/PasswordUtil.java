/**BCrypt 已交给 Spring Security**/


//package com.example.springproject.security;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//
//public class PasswordUtil {
//// static 因为 encoder 是 static，所以整个 JVM 里通常只有这一份。 就可以使用因为 encoder 是 static，所以整个 JVM 里通常只有这一份。 不需要new PasswordUtil
//    //final 才是“不能重新赋值”
//    //private就是只能在这个class中使用
//  private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    //encode 加密
//    //static --- 加了就可以直接用 PasswordUtil.encode("123456");
//    public static String encode(String password){
//       return encoder.encode(password);
//    }
//    //matches 对比
//    //static --- 加了就可以直接用 PasswordUtil.matches("123456");
//
//    public static boolean matches(String rowPassword, String hashPassword){
//        return encoder.matches(rowPassword, hashPassword);
//    }
//}
