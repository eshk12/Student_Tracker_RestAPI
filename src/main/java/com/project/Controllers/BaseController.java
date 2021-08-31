package com.project.Controllers;

import com.project.Models.User;
import com.project.Objects.Entities.AuthUser;
import com.project.Persist;
import com.project.Utils.Definitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@Transactional
public class BaseController {
    @Autowired
    private Persist persist;

    @Autowired
    private Definitions definitions;

    @ModelAttribute
    public AuthUser isUserAuthenticated(HttpServletRequest request){
        AuthUser authUser;
        String token = request.getHeader("PARAM_X_AUTH");
        Integer id = request.getHeader("PARAM_ID_AUTH") != null ? Integer.parseInt(request.getHeader("PARAM_ID_AUTH")) : 0 ;
        if(id > 0 && !token.equals("token_not_exists")){
            User user = (User) persist.getQuerySession().createQuery("FROM User WHERE token = :token AND id = :id")
                    .setParameter("id", id)
                    .setParameter("token", token)
                    .uniqueResult();
            if(user != null){
                int departmentId = (user.getDepartmentObject() != null ) ? user.getDepartmentObject().getId() : 0;
                authUser = new AuthUser(user.getPermission(), user.getInstituteObject().getId(), departmentId);
            }else{ //invalid token
                authUser = new AuthUser(definitions.INVALID_TOKEN);
            }
        }else{
            authUser = new AuthUser(definitions.GUEST_PERMISSION);
        }
        return authUser;
    }
}
