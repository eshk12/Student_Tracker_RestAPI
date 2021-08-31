package com.project.Controllers;

import com.project.Models.User;
import com.project.Objects.Entities.AuthToken;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.PasswordAuthentication;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Transactional
public class AuthController extends BaseController {

    @Autowired private Persist persist;
    @Autowired private Definitions definitions;
    @Autowired private PasswordAuthentication passwordAuthentication;

    EmailValidator validator = EmailValidator.getInstance();

    @RequestMapping(value = "/authenticate/login", method = RequestMethod.POST)
    public BasicResponseModel getUser(@RequestParam String email, @RequestParam char[] password) {
        BasicResponseModel responseModel;
        if (email.length() == 0 || password.length == 0) {
            responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
        } else {
            List<User> user = persist.getQuerySession().createQuery("FROM User WHERE email = :email")
                    .setParameter("email", email)
                    .list();
            if (user.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.LOGIN_FAILED_NO_EXISTS, definitions.LOGIN_FAILED_NO_EXISTS_MSG);
            } else {
                if (user.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    if (passwordAuthentication.hashPassword(password).equals(user.get(0).getPassword())) { //login succsessful
                        String newToken = passwordAuthentication.createLoginToken(user.get(0).getEmail(), user.get(0).getPassword());
                        User userRow = persist.loadObject(User.class, user.get(0).getId());
                        userRow.setToken(newToken);
                        persist.save(userRow);

                        AuthToken authToken = new AuthToken(
                                newToken,
                                userRow.getFirstName() + " " + userRow.getLastName(),
                                userRow.getId(),
                                userRow.getPermission()
                        );
                        responseModel = new BasicResponseModel(authToken);


                        /*JSONObject jsonToReturn = new JSONObject();
                        jsonToReturn.put("token", newToken);
                        jsonToReturn.put("userid", userRow.getId());
                        jsonToReturn.put("authUser", userRow.getFirstName() + " " + userRow.getLastName());
                        jsonToReturn.put("permission", userRow.getPermission());
                        responseModel = new BasicResponseModel(jsonToReturn.toString());*/


                    } else {
                        responseModel = new BasicResponseModel(definitions.LOGIN_FAILED_WRONG_PASSWORD, definitions.LOGIN_FAILED_WRONG_PASSWORD_MSG);
                    }
                }
            }
        }
        return responseModel;
    }
}
