package com.test.model;

import java.io.InputStream;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import sun.misc.BASE64Encoder;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

/**
 * 
 * @author 易磊
 * 金山网盘申请Token
 * 
 *
 */
public class ApiTest {
	//注册得到的key与secret
	public String comsumerKey ;
	public String comsumerSecret ;
	
	// 第一步的访问基本网址
	public String stepOneUrl = new String("https://openapi.kuaipan.cn/open/requestToken");
	// 第三步的基本网址
	public String stepThreeUrl = new String("https://openapi.kuaipan.cn/open/accessToken");
	
	// 随机产生的字符串
	public String nonce;
	// 时间戳 
	public String timestamp;
	
	// 第一步生成签名的基字符串
	public String stepOneBaseString;
	// 第三步生成签名的基字符串
	public String stepThreeBaseString;
	
	// 第一步的签名
	public String stepOneSignature;
	// 第三步的签名
	public String stepThreeSignature;
	
	// 第一步签名的密钥
	public String stepOneSignatureKey;
	// 第三步签名的密钥
	public String stepThreeSignatureKey;
	
	// 临时的token
	public String tempToken ;
	// 临时的token secret
	public String tempTokenSecret ;
	
	// 第一步访问地址
	public String stepOneRealUrl;
	// 第三部访问地址
	public String stepThreeRealUrl;
	
	
	public ApiTest(String comsumerKey ,String comsumerSecret) {
		this.comsumerKey = comsumerKey ;
		this.comsumerSecret = comsumerSecret ;
	}
	
	//生成nonce
	public void setnonce(){
		String base = "abcdefghijklmnopqrstuvwxyz0123456789"; 
		Random random = new Random(); 
		StringBuffer sb = new StringBuffer(); 
	    for (int i = 0; i < 8; i++) {
	        int number = random.nextInt(base.length());
	        sb.append(base.charAt(number));
	        }
	    this.nonce = sb.toString(); 
	}
	
	//生成timestamp
	public void settimestamp(){
	    Date date = new Date();
	    long time = date.getTime();
	    this.timestamp = (time + "").substring(0, 10); //时间戳位数存疑？
	}
	
	//获取基串
	@SuppressWarnings("deprecation")
	public void getBaseString(){
		this.setnonce();
		this.settimestamp();
		String encodebaseurl = URLEncoder.encode(stepOneUrl);
		String encodeparam = URLEncoder.encode("oauth_consumer_key="+this.comsumerKey+"&"+"oauth_nonce="+this.nonce+"&"+"oauth_signature_method=HMAC-SHA1"+"&"+"oauth_timestamp="+this.timestamp+"&"+"oauth_version=1.0");
		this.stepOneBaseString = "GET&"+encodebaseurl+"&"+encodeparam;
	}
	//获取基串2
	@SuppressWarnings("deprecation")
	public void getBaseString2(){
		this.setnonce();
		this.settimestamp();
		String encodebaseurl = URLEncoder.encode(stepThreeUrl);
		String encodeparam = URLEncoder.encode("oauth_consumer_key="+this.comsumerKey+"&"+"oauth_nonce="+this.nonce+"&"+"oauth_signature_method=HMAC-SHA1"+"&"+"oauth_timestamp="+this.timestamp+"&"+"oauth_token="+this.tempToken+"&"+"oauth_version=1.0");
		this.stepThreeBaseString = "GET&"+encodebaseurl+"&"+encodeparam;
	}
	//生成signaturekey
	public void getsignaturekey(){
		this.stepOneSignatureKey = comsumerSecret+"&";

	}
	//生成signaturekey2
	public void getsignaturekey2(){
		this.stepThreeSignatureKey = comsumerSecret+"&"+tempTokenSecret;
	}
	//生成signature
	@SuppressWarnings("deprecation")
	public void getsignature(){
		this.getBaseString();
		this.getsignaturekey();
		byte[] byteHMAC = null; 
		 try {
			    Mac mac = Mac.getInstance("HmacSHA1");
			    SecretKeySpec spec = new SecretKeySpec(stepOneSignatureKey.getBytes(), "HmacSHA1");
			    mac.init(spec);
			    byteHMAC = mac.doFinal(stepOneBaseString.getBytes());
			    } catch (InvalidKeyException e) {
			    	e.printStackTrace();
			    } catch (NoSuchAlgorithmException ignore) {
			    } 
		 BASE64Encoder enc = new BASE64Encoder();
		 String s = enc.encode(byteHMAC);
		 this.stepOneSignature = URLEncoder.encode(s);
	}
	//生成signature2
	@SuppressWarnings("deprecation")
	public void getsignature2(){
		this.getBaseString2();
		this.getsignaturekey2();
		byte[] byteHMAC = null; 
		 try {
			    Mac mac = Mac.getInstance("HmacSHA1");
			    SecretKeySpec spec = new SecretKeySpec(stepThreeSignatureKey.getBytes(), "HmacSHA1");
			    mac.init(spec);
			    byteHMAC = mac.doFinal(stepThreeBaseString.getBytes());
			    } catch (InvalidKeyException e) {
			    	e.printStackTrace();
			    } catch (NoSuchAlgorithmException ignore) {
			    } 
		 BASE64Encoder enc = new BASE64Encoder();
		 String s = enc.encode(byteHMAC);
		 this.stepThreeSignature = URLEncoder.encode(s);
	}
	//生成链接
	public void geturl(){
		this.getsignature();
		stepOneRealUrl = "https://openapi.kuaipan.cn/open/requestToken?oauth_consumer_key="+this.comsumerKey+"&oauth_nonce="+this.nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+this.timestamp+"&oauth_version=1.0&oauth_signature="+this.stepOneSignature;
	}
	//生成链接2
	public void geturl2(){
		this.getsignature2();
		stepThreeRealUrl = "https://openapi.kuaipan.cn/open/accessToken?oauth_signature="+this.stepThreeSignature+"&oauth_consumer_key="+this.comsumerKey+"&oauth_nonce="+this.nonce+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+this.timestamp+"&oauth_token="+this.tempToken+"&oauth_version=1.0";
	}
	//发送请求
	public JSONObject sendGet(String urlname) 
	{
		JSONObject result = null;
		try
		{
			URL realUrl = new URL(urlname);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			// 获取所有响应头字段
			InputStream map = conn.getInputStream();
			int i;
			String str="";
			while((i = map.read())!=-1){
				str = str+String.valueOf((char)i);
			}
			result = new JSONObject(str);
			conn.disconnect();

		}
		catch(Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return result;
	}
}
