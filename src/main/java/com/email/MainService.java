package com.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class MainService {
    private final String SEND_EMAIL_FROM = "kmina3471@naver.com";
    private final String RESET_PASSWORD_URL = "http://localhost:8080/repw?token=";


    @Autowired
    private TemplateEngine templateEngine;

    @Autowired private MainMapper mainMapper;
    @Autowired JavaMailSender javaMailSender;

    // 비밀번호를 재설정하는 페이지에서 유효한 유저의 검사 (+ 후 메일 전송)
    public String find_user_pw(UserDTO userDTO){
        UserDTO findedUser = mainMapper.find_user_by_email(userDTO);
        if(Objects.isNull(findedUser)){
            return "해당 유저가 존재하지 않습니다";
        }
        try {
            // 토큰과 만료 날짜를 생성한다
            String token = UUID.randomUUID().toString();
            LocalDateTime tokenExpireDate = LocalDateTime.now().plusMinutes(2); // 토큰 만료 2분
            // 만들어진 토큰과 만료 날짜를 유저에게 설정한다
            findedUser.setPwReToken(token);
            findedUser.setPwReTokenExpire(tokenExpireDate);
            // DB에 UPDATE 시킨다
            mainMapper.update_user_repw_token(findedUser);
            // 해당 유저에게 해당 토큰과 함께 메일을 보낸다
            send_mail_of_user_password(findedUser.getEmail(), token);
        }catch (MessagingException e){
            return "어떠한 이유로 메일 보내기에 실패했습니다. 관리자에게 문의해주세요";
        }

        return "메일을 발송했습니다";
    }

    // 비밀번호를 재설정하는 페이지의 유효한 token 검사
    public boolean find_user_by_token(String token){
        // 타고 온 링크의 token 값으로 해당 유저가 존재하는지 검사
        UserDTO userDTO = mainMapper.find_user_by_token(token);
        // 해당 유저가 존재하지 않거나, 토큰이 만료되었으면 실패!
        if(Objects.isNull(userDTO) || userDTO.getPwReTokenExpire().isBefore(LocalDateTime.now())){
            return false;
        }
        return true;
    }

    // 유저 패스워드 변경하기
    public void change_user_pw(UserDTO userDTO){
        mainMapper.update_user_password_by_token(userDTO);
    }

    // 유저에게 비밀번호 변경 메일 (URL이 첨부되어있는) 전송하기
    private void send_mail_of_user_password(String to, String token) throws MessagingException {
        System.out.println("메일 보내기 시도");

        Context ctx = new Context();
        ctx.setVariable("link", RESET_PASSWORD_URL + token);


        String htmlContent = templateEngine.process("/mail/re-password-template.html", ctx);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(SEND_EMAIL_FROM); // 누가 보내는가?
        mimeMessageHelper.setTo(to);  // 누구한테 보내는가?
        mimeMessageHelper.setSubject("[KOREA Shop] 비밀번호 재설정"); // 제목은 무엇인가?
        mimeMessageHelper.setText(htmlContent , true);    // 내용은 무엇인가?
        javaMailSender.send(mimeMessage);
    }



}
