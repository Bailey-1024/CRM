package com.lgy.controller;

import com.lgy.base.BaseController;
import com.lgy.service.UserRoleService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class UserRoleController extends BaseController {
    @Resource
    private UserRoleService userRoleService;
}
