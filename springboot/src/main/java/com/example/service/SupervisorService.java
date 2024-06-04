package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Supervisor;
import com.example.exception.CustomException;
import com.example.mapper.SupervisorMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员业务处理
 **/
@Service
public class SupervisorService {

    @Resource
    private SupervisorMapper supervisorMapper;

    /**
     * 新增
     */
    public void add(Supervisor supervisor) {
        Supervisor dbSupervisor = supervisorMapper.selectByUsername(supervisor.getUsername());
        if (ObjectUtil.isNotNull(dbSupervisor)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(supervisor.getPassword())) {
            supervisor.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(supervisor.getName())) {
            supervisor.setName(supervisor.getUsername());
        }
        supervisor.setRole(RoleEnum.SUPERVISOR.name());
        supervisorMapper.insert(supervisor);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        supervisorMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            supervisorMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Supervisor supervisor) {
        supervisorMapper.updateById(supervisor);
    }

    /**
     * 根据ID查询
     */
    public Supervisor selectById(Integer id) {
        return supervisorMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Supervisor> selectAll(Supervisor supervisor) {
        return supervisorMapper.selectAll(supervisor);
    }

    /**
     * 分页查询
     */
    public PageInfo<Supervisor> selectPage(Supervisor supervisor, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Supervisor> list = supervisorMapper.selectAll(supervisor);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbSupervisor = supervisorMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbSupervisor)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbSupervisor.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbSupervisor.getId() + "-" + RoleEnum.SUPERVISOR.name();
        String token = TokenUtils.createToken(tokenData, dbSupervisor.getPassword());
        dbSupervisor.setToken(token);
        return dbSupervisor;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        Supervisor supervisor = new Supervisor();
        BeanUtils.copyProperties(account, supervisor);
        add(supervisor);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Supervisor dbSupervisor = supervisorMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbSupervisor)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbSupervisor.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbSupervisor.setPassword(account.getNewPassword());
        supervisorMapper.updateById(dbSupervisor);
    }

}