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


    @RequestMapping(value = "/institute/add", method = RequestMethod.POST)
    public BasicResponseModel addInstitute(
            @ModelAttribute("Institute") Institute institute,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_PERMISSION)) {
            if (institute.objectIsEmpty()) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                persist.save(institute);
                responseModel = new BasicResponseModel(institute, authUser);
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/getInstitute", method = RequestMethod.GET)
    public BasicResponseModel getInstitute(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.INSTITUTE_NOT_FOUND, Definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Institute.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/getAllInstitutes", method = RequestMethod.GET)
    public BasicResponseModel getAllInstitutes(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_PERMISSION)) {
            List<Institute> allInstitutes = persist.getQuerySession().createQuery("FROM Institute AS ins ORDER BY ins.id DESC").list();
            if (allInstitutes.isEmpty()) {
                responseModel = new BasicResponseModel(Definitions.EMPTY_LIST, Definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allInstitutes, authUser);
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }
    @RequestMapping(value = "/institute/deleteInstitute", method = RequestMethod.POST)
    public BasicResponseModel deleteInstitute(@RequestParam int id, @RequestParam boolean delete, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.INSTITUTE_NOT_FOUND, Definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    Institute institute = persist.loadObject(Institute.class, id);
                    institute.setDeleted(delete);
                    persist.save(institute);
                    responseModel = new BasicResponseModel(institute, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/institute/update", method = RequestMethod.POST)
    public BasicResponseModel updateInstitute(@ModelAttribute("Institute") Institute institute, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_PERMISSION)) {
            if (institute.getId() <= 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                        .setParameter("id", institute.getId())
                        .list();
                if (instituteRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.INSTITUTE_NOT_FOUND, Definitions.INSTITUTE_NOT_FOUND_MSG);
                } else if (instituteRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    Institute oldInstitute = persist.loadObject(Institute.class, institute.getId());
                    oldInstitute.setObject(institute);
                    persist.save(oldInstitute);
                    responseModel = new BasicResponseModel(oldInstitute, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }
}
