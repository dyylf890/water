package com.loyalove.water.web.config.shiro;

import com.loyalove.water.biz.auth.UserBiz;
import com.loyalove.water.common.enums.UserStatusEnum;
import com.loyalove.water.common.util.StringUtils;
import com.loyalove.water.pojo.UserPO;
import com.loyalove.water.vo.auth.UserVO;
import com.loyalove.water.web.util.SessionKeys;
import com.loyalove.water.web.util.SessionUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Title: MyShiroRealm.java
 * Description: 自定义的ShiroRealm
 * Company: ysh
 *
 * @author: sailuo@yiji.com
 * @date: 2016-11-28 16:18
 */
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    UserBiz userBiz;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        UserPO userPO = (UserPO) SessionUtil.getAttribute(SessionKeys.CURR_USER);
        UserVO userVO = userBiz.queryUserRolePermission(userPO);
        return getAuthorizationInfo(userVO);
    }

    /**
     * 获取认证信息
     *
     * @param userVO
     * @return
     */
    public static AuthorizationInfo getAuthorizationInfo(UserVO userVO) {
        //创建Shiro统一的认证信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //设置角色认证信息
        authorizationInfo.setRoles(userVO.getRoles());
        //设置权限认证信息
        authorizationInfo.setStringPermissions(userVO.getPermissions());
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        //查询用户对象
        UserPO user = userBiz.queryUserByName(username);
        if (null == user) {
            throw new UnknownAccountException("用户名不存在");
        } else if (StringUtils.equals(user.getStatus(), UserStatusEnum.LOCKED.code())) {
            throw new LockedAccountException("用户已锁定");
        } else if (StringUtils.equals(user.getStatus(), UserStatusEnum.INIT.code())) {
            throw new NoActiveAccountException("用户未激活");
        }
        //保存登录用户到Session
        SessionUtil.setAttribute(SessionKeys.CURR_USER, user);
        //创建Shiro统一的验证信息对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getSalt()),
                getName());
        return authenticationInfo;
    }

}
