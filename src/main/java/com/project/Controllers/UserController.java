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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
public class UserController extends BaseController {

    @Autowired
    private Persist persist;

    EmailValidator validator = EmailValidator.getInstance();

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public BasicResponseModel addUser(
            @ModelAttribute("User") User user,
            @RequestParam(required = false) Integer instituteId,
            @RequestParam(required = false) Integer departmentId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            instituteId =
                    (instituteId != null) &&
                            (authUser.getAuthUserpermission() == Definitions.ADMIN_PERMISSION)
                            ? instituteId
                            : authUser.getAuthUserInstituteId();
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
                    return new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                }
            }
            if (user.objectIsEmpty()) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                if (!validator.isValid(user.getEmail())) {
                    responseModel = new BasicResponseModel(Definitions.INVALID_EMAIL, Definitions.INVALID_EMAIL_MSG);
                } else {
                    List<User> userList = persist.getQuerySession().createQuery("FROM User WHERE email = :email")
                            .setParameter("email", user.getEmail())
                            .list();
                    if (userList.isEmpty()) {
                        //check if valid permission
                        //here we check that the user have ADMIN_INSTITUTE_PERMISSION and tries to make
                        //user with ADMIN_PERMISSION so we cast it to ADMIN_INSTITUTE_PERMISSION
                        if (authUser.getAuthUserpermission() == Definitions.ADMIN_INSTITUTE_PERMISSION &&
                                user.getPermission() == Definitions.ADMIN_PERMISSION) {
                            user.setPermission(Definitions.ADMIN_INSTITUTE_PERMISSION);
                        }
                        Department departmentObject = departmentId != null ? persist.loadObject(Department.class, departmentId) : null;
                        Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                        user.setPassword(PasswordAuthentication.hashPassword(user.getPassword()));
                        user.setDepartmentObject(departmentObject);
                        user.setInstituteObject(instituteObject); // because we allow user without department
                        persist.save(user);
                        responseModel = new BasicResponseModel(user, authUser);
                    } else {
                        responseModel = new BasicResponseModel(Definitions.EMAIL_EXISTS, Definitions.EMAIL_EXISTS_MSG);
                    }
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
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
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (user.getId() <= 0 ||
                    user.getPermission() < Definitions.ADMIN_PERMISSION ||
                    user.getPermission() > Definitions.GUEST_PERMISSION) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                instituteId =
                        (instituteId != null) &&
                                (authUser.getAuthUserpermission() == Definitions.ADMIN_PERMISSION)
                                ? instituteId
                                : authUser.getAuthUserInstituteId();
                departmentId = departmentId != null ? departmentId : null;
                user.setPassword(null); //unable to update password in this route.
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
                        return new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                    }
                }
                List<User> userRow = persist.getQuerySession().createQuery("FROM User WHERE id = :id")
                        .setParameter("id", user.getId())
                        .list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.USER_NOT_FOUND, Definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else { //so far so good.

                    User oldUser = persist.loadObject(User.class, user.getId());
                    if (authUser.getAuthUserpermission() == Definitions.ADMIN_INSTITUTE_PERMISSION &&
                            user.getPermission() > Definitions.ADMIN_PERMISSION) {
                        user.setPermission(Definitions.ADMIN_INSTITUTE_PERMISSION);
                    }

                    Department departmentObject = departmentId != null ? persist.loadObject(Department.class, departmentId) : null;
                    Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                    user.setDepartmentObject(departmentObject);
                    user.setInstituteObject(instituteObject); // because we allow user without department

                    if (user.getEmail() != null && !user.getEmail().equals(userRow.get(0).getEmail())) {
                        if (!validator.isValid(user.getEmail())) {
                            responseModel = new BasicResponseModel(Definitions.INVALID_EMAIL, Definitions.INVALID_EMAIL_MSG);
                        } else {
                            List<User> userList = persist.getQuerySession().createQuery("FROM User WHERE email = :email")
                                    .setParameter("email", user.getEmail())
                                    .list();
                            if (userList.size() > 0) {
                                responseModel = new BasicResponseModel(Definitions.EMAIL_EXISTS, Definitions.EMAIL_EXISTS_MSG);
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
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    //UPDATE PASSWORD

    @RequestMapping(value = "/users/updateUserPassword", method = RequestMethod.POST)
    public BasicResponseModel updateUserPassword(@RequestParam int id, @RequestParam String password, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0 || password.length() == 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                String Query = authUser.getAuthUserpermission() == Definitions.ADMIN_PERMISSION
                        ? "FROM User WHERE id = :id AND :instituteId = :instituteId"
                        : "FROM User WHERE id = :id AND instituteObject.id = :instituteId"
                ;
                List<User> userRow = persist.getQuerySession()
                        .createQuery(Query)
                        .setParameter("id", id)
                        .setParameter("instituteId", authUser.getAuthUserInstituteId())
                        .list();

                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.USER_NOT_FOUND, Definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    User oldUser = persist.loadObject(User.class, id);
                    oldUser.setPassword(PasswordAuthentication.hashPassword(password));
                    responseModel = new BasicResponseModel(oldUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/users/getUser", method = RequestMethod.GET)
    public BasicResponseModel getUser(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<User> userRow = persist.getQuerySession().createQuery("FROM User WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.USER_NOT_FOUND, Definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(User.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/users/deleteUser", method = RequestMethod.POST)
    public BasicResponseModel deleteUser(@RequestParam int id, @RequestParam boolean delete, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<User> userRow = persist.getQuerySession().createQuery("FROM User WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (userRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.USER_NOT_FOUND, Definitions.USER_NOT_FOUND_MSG);
                } else if (userRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    User user = persist.loadObject(User.class, id);
                    user.setDeleted(delete);
                    persist.save(user);
                    responseModel = new BasicResponseModel(user);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/users/getAllUsers", method = RequestMethod.GET)
    public BasicResponseModel getAllUsers(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            List<User> allUsers = persist.getQuerySession().createQuery("FROM User").list();
            if (allUsers.isEmpty()) {
                responseModel = new BasicResponseModel(Definitions.EMPTY_LIST, Definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allUsers);
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

}
