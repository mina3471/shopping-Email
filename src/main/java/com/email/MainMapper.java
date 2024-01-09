package com.email;


import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MainMapper {
    @Select("SELECT * FROM `user` WHERE `id` = #{id} AND `email` = #{email}")
    UserDTO find_user_by_email(UserDTO userDTO);

    @Update("UPDATE `user` SET `pw_re_token` = #{pwReToken}, `pw_re_token_expire` = #{pwReTokenExpire} WHERE `id` = #{id}")
    void update_user_repw_token(UserDTO userDTO);


    // 해당 패스워드 재설정 이메일의 링크를 클릭했다면 해당 토큰 값을 가진 유저를 가져오기
    @Select("SELECT * FROM `user` WHERE `pw_re_token` = #{token}")
    UserDTO find_user_by_token(String token);

    // 비밀번호 재설정을 한 유저의 비밀번호를 변경하기
    @Update("UPDATE `user` SET `password` = #{password} WHERE `pw_re_token` = #{pwReToken}")
    void update_user_password_by_token(UserDTO userDTO);

}
