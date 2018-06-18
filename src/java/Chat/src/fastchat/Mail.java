package fastchat;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class Mail {
	
	static public String a = null;
	
	
	/**
	 * @author wrf
	 * 产生4位随机数
	 */
    static public String identifycode() { // 产生四位数字验证码
    	String str="0123456789";
        StringBuilder sb=new StringBuilder(4);
        for(int i=0;i<4;i++) {
        char ch=str.charAt(new Random().nextInt(str.length()));
        sb.append(ch);
        }
        String context = sb.toString();
        return context;
    }

    
    /**
	 * @author wrf
	 * 创建相关message信息
	 */
    static public Message createMessage(Session session, String targetEmail) throws AddressException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("m15639081168@163.com")); // 发送邮件地址
        
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(targetEmail));
        message.setSubject("轻聊验证码"); // 邮件标题
        a = identifycode();
        message.setContent(a, "text/html;charset=UTF-8");//邮件内容
        message.saveChanges();
        System.out.println(identifycode());
        return message;
    }

}


