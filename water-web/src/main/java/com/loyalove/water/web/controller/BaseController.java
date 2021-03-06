package com.loyalove.water.web.controller;

import com.loyalove.water.pojo.UserPO;
import com.loyalove.water.web.util.SessionKeys;
import com.loyalove.water.web.util.SessionUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: BaseController.java
 * Description: BaseController
 * Company: ysh
 *
 * @author: sailuo@yiji.com
 * @date: 2016-11-29 18:02
 */
public class BaseController {
    //日志
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //只获取当前的Session
    protected Session currSession() {
        return SecurityUtils.getSubject().getSession(false);
    }

    //只获取当前的登录用户
    protected UserPO currUser() {
        return (UserPO) SessionUtil.getAttribute(SessionKeys.CURR_USER);
    }
}
