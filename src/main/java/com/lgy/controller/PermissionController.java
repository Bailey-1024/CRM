package com.lgy.controller;

import com.lgy.base.BaseController;
import com.lgy.service.PermissionService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class PermissionController extends BaseController {
    @Resource
    private PermissionService permissionService;

}
