package com.shop.test;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;

public class TestMD5 {

	@Test
	public void show(){
		String pwd = "123";  //需要加密的密码明文
		String salt = "qwer";
		//直接使用MD5加密
		//Md5Hash md5 = new Md5Hash(pwd);
		//加盐的方式加密
//		Md5Hash md5 = new Md5Hash(pwd, salt);
		//添加加密次数的方式加密
		Md5Hash md5 = new Md5Hash(pwd, salt,2);
		System.out.println("加密后的密码为:"+md5.toString());
	}
}
