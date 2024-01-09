package com.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    @Autowired
    private MainService mainService;

    // 사용자가 메일 발송 버튼 눌렀을 때!
    @ResponseBody
    @PostMapping("/find")
    public String find_user_pw(@RequestBody UserDTO userDTO){
        System.out.println(userDTO);
        return mainService.find_user_pw(userDTO);
    }

    // 패스워드 재설정 페이지로 이동
    @GetMapping("/repw")
    public String re_password_page(@RequestParam("token") String token, Model model){
        if(mainService.find_user_by_token(token)){
            model.addAttribute("token", token);
            return "repw";
        }
        return "repw-expire";
    }


    @PostMapping("/repw")
    public String re_password_page(UserDTO userDTO){
        System.out.println(userDTO);
        mainService.change_user_pw(userDTO);
        // fetch나 alert로
        return "error";
    }






}
