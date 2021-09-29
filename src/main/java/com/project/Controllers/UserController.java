package com.project.Controllers;

import com.project.Models.Department;
import com.project.Models.Institute;
import com.project.Models.User;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.PasswordAuthentication;
import com.project.Utils.Permissions;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
public class UserController extends BaseController {

    @Autowired
    private Persist persist;
    @Autowired
    private Definitions definitions;
    @Autowired
    private Permissions permissions;
    @Autowired
    private PasswordAuthentication passwordAuthentication;
    EmailValidator validator = EmailValidator.getInstance();

    private Query getQueryWithPermission(AuthUser authUser, int id) {
        Query queryObject;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            queryObject = persist.getQuerySession()
                    .createQuery("FROM User WHERE id = :id")
                    .setParameter("id", id);
        } else {
            queryObject = persist.getQuerySession()
                    .createQuery("FROM User WHERE id = :id AND instituteObject.id = :instituteId")
                    .setParameter("id", id)
                    .setParameter("instituteId", authUser.getAuthUser_instituteId());
        }
        return queryObject;
    }


    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public BasicResponseModel addUser(
            @ModelAttribute("User") User user,
            @RequestParam(required = false) Integer instituteId,
            @RequestParam(required = false) Integer departmentId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            instituteId =
                    (instituteId != null) &&
                            (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION)
                            ? instituteId
                            : authUser.getAuthUser_instituteId();
            departmentId = departmentId != null ? departmentId : null;
            if (departmentId != null) {
                //here we check if the department that inserted is belong to the user's institute or even exists.
                List<Department> departmentList =
                        persist.getQuerySession()
                                .createQuery
                                        ("FROM Department WHERE id = :id AND instituteObject.id = :instituteId")
                                .setParameter("id", departmentId)
                                .setParameter("instituteId", instituteId)
                                .list();
                if (departmentList.isEmpty()) {
                    return new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                }
            }
            if (user.objectIsEmpty() || !permissions.validPermissionRange(user.getPermission())) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                if (!validator.isValid(user.getEmail())) {
                    responseModel = new BasicResponseModel(definitions.INVALID_EMAIL, definitions.INVALID_EMAIL_MSG);
                } else {
                    List<User> userList = persist.getQuerySession().createQuery("FROM User WHERE email = :email")
                            .setParameter("email", user.getEmail())
                            .list();
                    if (userList.isEmpty()) {
                        //check if valid permission
                        //here we check that the user have ADMIN_INSTITUTE_PERMISSION and tries to make
                        //user with ADMIN_PERMISSION so we cast it to ADMIN_INSTITUTE_PERMISSION
                        if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION &&
                                user.getPermission() == definitions.ADMIN_PERMISSION) {
                            user.setPermission(definitions.ADMIN_INSTITUTE_PERMISSION);
                        }
                        Department departmentObject = departmentId != null ? persist.loadObject(Department.class, departmentId) : null;
                        Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                        user.setPassword(passwordAuthentication.hashPassword(user.getPassword()));
                        user.setDepartmentObject(departmentObject);
                        user.setInstituteObject(instituteObject); // because we allow user without department
                        persist.save(user);
                        responseModel = new BasicResponseModel(user);
                    } else {
                        responseModel = new BasicResponseModel(definitions.EMAIL_EXISTS, definitions.EMAIL_EXISTS_MSG);
                    }
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    //UPDATE

    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public BasicResponseModel updateUser(
            @ModelAttribute("User") User user,
            @RequestParam(required = false) Integer instituteId,
            @RequestParam(required = false) Integer departmentId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (user.getId() <= 0 ||
                    user.getPermission() < definitions.ADMIN_PERMISSION ||
                    user.getPermission() > definitions.GUEST_PERMISSION) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                instituteId =
                        (instituteId != null) &&
                                (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION)
                                ? instituteId
                                : authUser.getAuthUser_instituteId();
                departmentId = departmentId != null ? departmentId : null;
                user.setPassword(null); //unable to update password in this route.
                user.setToken(null); //unable to update password in this route.
                if (departmentId != null) {
                    //here we check if the department that inserted is belong to the user's institute or even exists.
                    List<Department> departmentList =
                            persist.getQuerySession()
                                    .createQuery
                                            ("FROM Department WHERE id = :id AND instituteObject.id = :instituteId")
                                    .setParameter("id", departmentId)
                                    .setParameter("instituteId", instituteId)
                                    .list();
                    if (departmentList.isEmpty()) {
                        return new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                    }
                }
                if (!permissions.validPermissionRange(user.getPermission())) {
                    responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
                } else {
                    List<User> userRow = persist.getQuerySession().createQuery("FROM User WHERE id = :id")
                            .setParameter("id", user.getId())
                            .list();
                    if (userRow.isEmpty()) {
                        responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
                    } else if (userRow.size() > 1) {
                        responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                    } else { //so far so good.

                        User oldUser = persist.loadObject(User.class, user.getId());
                        if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION &&
                                user.getPermission() == definitions.ADMIN_PERMISSION) {
                            user.setPermission(definitions.ADMIN_INSTITUTE_PERMISSION);
                        }
                        if (departmentId != oldUser.getDepartmentObject().getId()) {
                            Department departmentObject = departmentId != null ? persist.loadObject(Department.class, departmentId) : null;
                            user.setDepartmentObject(departmentObject);
                        }

                        if (instituteId != oldUser.getInstituteObject().getId()) {
                            Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                            user.setInstituteObject(instituteObject); // because we allow user without department
                        }

                        if (user.getEmail() != null && !user.getEmail().equals(userRow.get(0).getEmail())) {
                            if (!validator.isValid(user.getEmail())) {
                                responseModel = new BasicResponseModel(definitions.INVALID_EMAIL, definitions.INVALID_EMAIL_MSG);
                            } else {
                                List<User> userList = persist.getQuerySession().createQuery("FROM User WHERE email = :email")
                                        .setParameter("email", user.getEmail())
                                        .list();
                                if (userList.size() > 0) {
                                    responseModel = new BasicResponseModel(definitions.EMAIL_EXISTS, definitions.EMAIL_EXISTS_MSG);
                                } else {
                                    oldUser.setObject(user);
                                    persist.save(oldUser);
                                    responseModel = new BasicResponseModel(oldUser);
                                }
                            }
                        } else {
                            oldUser.setObject(user);
                            persist.save(oldUser);
                            responseModel = new BasicResponseModel(oldUser);
                        }
                    }
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

    @RequestMapping(value = "/users/updateUserPassword", method = RequestMethod.POST)
    public BasicResponseModel updateUserPassword(
            @RequestParam int id,
            @RequestParam String password,
            @RequestParam String valid_password,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0 || password.length() == 0 || valid_password.length() == 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else if (!password.equals(valid_password)) {
                responseModel = new BasicResponseModel(definitions.PASSWORD_DONT_MATCH, definitions.PASSWORD_DONT_MATCH_MSG);
            } else if (password.length() < 8) {// no need to check if valid_password's length shorter than 8 because password == valid_password.
                responseModel = new BasicResponseModel(definitions.PASSWORD_IS_SHORT, definitions.PASSWORD_IS_SHORT_MSG);
            } else {
                List<User> userRow = getQueryWithPermission(authUser, id).list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    User oldUser = persist.loadObject(User.class, id);
                    oldUser.setPassword(passwordAuthentication.hashPassword(password));
                    oldUser.setToken(null);
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

    @RequestMapping(value = "/users/getUser", method = RequestMethod.GET)
    public BasicResponseModel getUser(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<User> userRow = getQueryWithPermission(authUser, id).list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(User.class, id));
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/users/deleteUser", method = RequestMethod.POST)
    public BasicResponseModel deleteUser(@RequestParam int id, @RequestParam boolean deleted, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<User> userRow = getQueryWithPermission(authUser, id).list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.USER_NOT_FOUND, definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    User user = persist.loadObject(User.class, id);
                    user.setDeleted(deleted);
                    persist.save(user);
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

    @RequestMapping(value = "/users/getAllUsers", method = RequestMethod.GET)
    public BasicResponseModel getAllUsers(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            Query queryObject;
            if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM User as user ORDER BY user.id DESC");
            } else { //MIMNUM PERMISSION ADMIN_INSTITUTE_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM User AS user WHERE instituteObject.id = :instituteId ORDER BY user.id DESC ")
                        .setParameter("instituteId", authUser.getAuthUser_instituteId());
            }
            List<Department> allUsers = queryObject.list();
            if (allUsers.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allUsers);
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

}
