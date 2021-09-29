package com.project.Controllers;
import com.project.Models.User;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.PasswordAuthentication;
import com.project.Utils.Permissions;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
public class ProfileController extends BaseController {
    @Autowired private Persist persist;
    @Autowired private Definitions definitions;
    @Autowired private Permissions permissions;
    @Autowired private PasswordAuthentication passwordAuthentication;
    EmailValidator validator = EmailValidator.getInstance();

    @RequestMapping(value = "/profile/getDetails", method = RequestMethod.GET)
    public BasicResponseModel getDetails(AuthUser authUser){
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.LOWEST_LOGGED_IN_PERMISSION)) {
            int id = authUser.getAuthUser_id();
            List<User> userRow = persist.getQuerySession()
                    .createQuery("FROM User WHERE id = :id")
                    .setParameter("id", id)
                    .list();
            if (userRow.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
            } else if (userRow.size() > 1) {
                responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
            } else {
                responseModel = new BasicResponseModel(persist.loadObject(User.class, id));
            }
        }else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/profile/updateUser", method = RequestMethod.POST)
    public BasicResponseModel updateUser(//limit the user choise
            String uid,
            String firstName,
            String lastName,
            String phone,
            AuthUser authUser
    ) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.LOWEST_LOGGED_IN_PERMISSION)) {
            if (
                    uid == null || firstName == null || lastName == null || phone == null ||
                    uid.equals("") || firstName.equals("") || lastName.equals("") || phone.equals("")
            ) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<User> userRow = persist.getQuerySession().createQuery("FROM User WHERE id = :id")
                        .setParameter("id", authUser.getAuthUser_id())
                        .list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {

                    User currentUser = new User(authUser.getAuthUser_id(), uid, firstName, lastName, phone);
                    User oldUser = persist.loadObject(User.class, authUser.getAuthUser_id());
                    oldUser.setObject(currentUser);
                    persist.save(oldUser);
                    responseModel = new BasicResponseModel(oldUser);
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }
    //UPDATE PASSWORD

    @RequestMapping(value = "/profile/updatePassword", method = RequestMethod.POST)
    public BasicResponseModel updatePassword(
            @RequestParam String password,
            @RequestParam String valid_password,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.LOWEST_LOGGED_IN_PERMISSION)) {
            if (password.length() == 0 || valid_password.length() == 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else if (!password.equals(valid_password)) {
                responseModel = new BasicResponseModel(definitions.PASSWORD_DONT_MATCH, definitions.PASSWORD_DONT_MATCH_MSG);
            } else if (password.length() < 8) {// no need to check if valid_password's length shorter than 8 because password == valid_password.
                responseModel = new BasicResponseModel(definitions.PASSWORD_IS_SHORT, definitions.PASSWORD_IS_SHORT_MSG);
            } else {
                int id = authUser.getAuthUser_id();
                List<User> userRow = persist.getQuerySession()
                        .createQuery("FROM User WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {


                    User user = persist.loadObject(User.class, id);
                    String newToken = passwordAuthentication.createLoginToken(user.getEmail(), user.getPassword());
                    user.setPassword(passwordAuthentication.hashPassword(password));
                    user.setToken(newToken);
                    responseModel = new BasicResponseModel(user);
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }



}
