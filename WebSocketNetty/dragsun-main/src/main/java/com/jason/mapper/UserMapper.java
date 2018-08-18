package com.jason.mapper;

import com.jason.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/6/19
 */
@Mapper
public interface UserMapper {

    public List<User> selectAllUserList();

    @Select("select * from `user` where user_id =#{id}")
    public User getUser(Long id);


    @Insert("INSERT INTO `dragsunweb`.`user`(`user_id`, `user_name`, `user_password`) VALUES (#{id}, #{name}, #{password})")
    @Options(useGeneratedKeys = true)
    public Integer insert(User user);


}
