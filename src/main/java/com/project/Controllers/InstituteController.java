package com.project.Controllers;

import com.project.Models.Institute;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class InstituteController extends BaseController {
    @Autowired
    private Persist persist;
    @Autowired
    private Definitions definitions;
    @Autowired
    private Permissions permissions;

    @RequestMapping(value = "/institute/add", method = RequestMethod.POST)
    public BasicResponseModel addInstitute(
            @ModelAttribute("Institute") Institute institute,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            if (institute.objectIsEmpty()) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                persist.save(institute);
                responseModel = new BasicResponseModel(institute);
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/getInstitute", method = RequestMethod.GET)
    public BasicResponseModel getInstitute(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Institute.class, id));
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/getAllInstitutes", method = RequestMethod.GET)
    public BasicResponseModel getAllInstitutes(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            List<Institute> allInstitutes = persist.getQuerySession().createQuery("FROM Institute AS ins ORDER BY ins.id DESC").list();
            if (allInstitutes.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allInstitutes);
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/deleteInstitute", method = RequestMethod.POST)
    public BasicResponseModel deleteInstitute(
            @RequestParam int id,
            @RequestParam boolean delete,
            AuthUser authUser
    ) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Institute institute = persist.loadObject(Institute.class, id);
                    institute.setDeleted(delete);
                    persist.save(institute);
                    responseModel = new BasicResponseModel(institute);
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/update", method = RequestMethod.POST)
    public BasicResponseModel updateInstitute(
            @ModelAttribute("Institute") Institute institute,
            AuthUser authUser
    ) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
            if (institute.getId() <= 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", institute.getId())
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Institute oldInstitute = persist.loadObject(Institute.class, institute.getId());
                    oldInstitute.setObject(institute);
                    persist.save(oldInstitute);
                    responseModel = new BasicResponseModel(oldInstitute);
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
