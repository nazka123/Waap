package com.waap.member.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.waap.common.base.BaseController;
import com.waap.member.service.MemberService;
import com.waap.member.vo.MemberVO;

@Controller("memberController")
@RequestMapping(value="/member")
public class MemberControllerImpl extends BaseController implements MemberController{
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberVO memberVO;
	
	@Override
	@RequestMapping(value="/login.do" ,method = RequestMethod.POST)
	public ModelAndView login(@RequestParam Map<String, String> loginMap,
			                  HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		memberVO=memberService.login(loginMap);
		
		if(memberVO!= null && memberVO.getMember_id()!=null){
			HttpSession session = request.getSession();
			session.setAttribute("isLogOn", "true");
			session.setAttribute("memberInfo", memberVO);
				
			mav.setViewName("redirect:/main/main.do");
			
			
		}else{
			mav.addObject("message", "???????????? ??????????????????. ?????? ???????????????");
			mav.setViewName("/member/loginForm");	
		}
		
		
		return mav;
	}
	
	
	@Override
	@RequestMapping(value="/logout.do" ,method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		
		session.setAttribute("isLogOn", "false");
		session.removeAttribute("memberInfo");
		
		
		mav.setViewName("redirect:/main/main.do");
		
		return mav;
	}
	
	@Override
	@RequestMapping(value="/addMember.do" ,method =  RequestMethod.POST)
	public ResponseEntity addMember(HttpServletRequest request, HttpServletResponse response, MemberVO memberVO)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=UTF-8");

		String message = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		
		try {
			memberService.addMember(memberVO);

			message = "<script>";
			message += " alert('??????????????? ?????????????????????.');";
			message += " location.href='" + request.getContextPath() + "/main/main.do';";
			message += ("</script>");

		} catch (Exception e) {

			message = "<script>";
			message += " alert('??????????????? ??????????????????.');";
			message += " location.href='" + request.getContextPath() + "/main/main.do';";
			message += ("</script>");

			e.printStackTrace();

		}

		ResponseEntity resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);

		return resEntity;
	}
	
	
	@Override
	@RequestMapping(value="/idCheck.do" ,method = RequestMethod.POST)
	@ResponseBody
	public int idCheck(HttpServletRequest request, HttpServletResponse response, String member_id) throws Exception {
		
 
		
		return memberService.idCheck(member_id);
		
	}
	
	@Override
	@RequestMapping(value="idFindEmail.do", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> idFindEmail(String member_name, String sending_member_email, String sending_member_email2, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		
		
		// ?????????
		String member_email = sending_member_email;	
		String member_email2 = sending_member_email2; 
		
		//????????? 
		final String user = "dloere15@gmail.com";
		final String password = "gosungho1234";
		
		// Property??? SMTP ?????? ?????? ??????
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 465);
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");		
				
        // SMTP ??????????????? ????????? ????????? ???????????? Session ???????????? ???????????? ??????
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        
        MimeMessage emailMessage = new MimeMessage(session);
		
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        
        
        MemberVO memberVO = memberService.emailAndNameCheck(member_name, member_email, member_email2); 
        
        if(memberVO == null) {
        	resultMap.put("message", "?????? ????????? ?????????.");
        	return resultMap;
        }else{
        	
        	resultMap.put("member_id", memberVO.getMember_id());
            
            try {
            	emailMessage.setFrom(new InternetAddress(user));
            	
            	String sendAddress = member_email + member_email2;
                // ????????? ?????? ??????
            	emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(sendAddress));

                // Subject
            	emailMessage.setSubject("????????? ?????? ??????????????????.");
            	
            	Random r = new Random();
        		int code = r.nextInt(999999);
            	
        		resultMap.put("code", code);
        		
                // Text
            	emailMessage.setText("????????? ????????? ????????? ["+code+"]");

                Transport.send(emailMessage);    // send message
                
                
            }catch(AddressException e) {
            	
            	e.printStackTrace();
            }catch(MessagingException e) {
            	
            	e.printStackTrace();
            }
    		
    	
            resultMap.put("message", "??????????????? ?????????????????????.");
    		return resultMap;
        }
        
        

	}
	

	@Override
	@RequestMapping(value="pwFindEmail.do", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> pwFindEmail(String member_id, String sending_member_email, String sending_member_email2, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		
		
		// ?????????
		String member_email = sending_member_email;	
		String member_email2 = sending_member_email2; 
		
		//????????? 
		final String user = "dloere15@gmail.com";
		final String password = "gosungho1234";
		
		// Property??? SMTP ?????? ?????? ??????
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 465);
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");		
				
        // SMTP ??????????????? ????????? ????????? ???????????? Session ???????????? ???????????? ??????
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        
        MimeMessage emailMessage = new MimeMessage(session);
		
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        
        MemberVO memberVO = memberService.emailAndIdCheck(member_id, member_email, member_email2); 
        
        if(memberVO == null) {
        	resultMap.put("message", "?????? ????????? ????????? ?????? ????????? ?????????.");
        	return resultMap;
        }else{
        	
        	resultMap.put("member_pw", memberVO.getMember_pw());
            
            try {
            	emailMessage.setFrom(new InternetAddress(user));
            	
            	String sendAddress = member_email + member_email2;
                // ????????? ?????? ??????
            	emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(sendAddress));

                // Subject
            	emailMessage.setSubject("???????????? ?????? ??????????????????.");
            	
            	Random r = new Random();
        		int code = r.nextInt(999999);
            	
        		resultMap.put("code", code);
        		
                // Text
            	emailMessage.setText("???????????? ????????? ????????? ["+code+"]");

                Transport.send(emailMessage);    // send message
                
                
            }catch(AddressException e) {
            	
            	e.printStackTrace();
            }catch(MessagingException e) {
            	
            	e.printStackTrace();
            }
    		
    	
            resultMap.put("message", "??????????????? ?????????????????????.");
    		return resultMap;
        }
        
        

	}
	
	
	
}
