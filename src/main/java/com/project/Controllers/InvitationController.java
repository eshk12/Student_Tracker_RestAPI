package com.project.Controllers;

import com.project.Models.Department;
import com.project.Models.Invitation;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.hibernate.query.Query;

import java.util.List;

@RestController
@Transactional
public class InvitationController extends BaseController {
    @Autowired
    private Persist persist;
    @Autowired
    private Definitions definitions;
    @Autowired
    private Permissions permissions;

    private Query getQueryWithPermission(AuthUser authUser, int id){
        Query queryObject;
        if (authUser.getAuthUserpermission() == definitions.ADMIN_PERMISSION) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id")
                    .setParameter("id", id);
        } else if (authUser.getAuthUserpermission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id AND departmentObject.instituteObject.id = :instituteId ")
                    .setParameter("instituteId", authUser.getAuthUserInstituteId())
                    .setParameter("id", id);
        } else { //MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id AND departmentObject.id = :departmentId ")
                    .setParameter("departmentId", authUser.getAuthUserDepartmentId())
                    .setParameter("id", id);
        }
        return queryObject;
    }
    @RequestMapping(value = "/invitation/add", method = RequestMethod.POST)
    public BasicResponseModel addInvitation(
            @RequestParam String name,
            @RequestParam String studyYear,
            @RequestParam(required = false) Integer departmentId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            String query = authUser.getAuthUserpermission() == definitions.ADMIN_PERMISSION
                    ? "FROM Department WHERE id = :id AND :instituteId = :instituteId" // 1 = 1
                    : "FROM Department WHERE id = :id AND instituteObject.id = :instituteId";
            List<Department> departmentList = persist.getQuerySession().createQuery(query)
                    .setParameter("id", departmentId)
                    .setParameter("instituteId", authUser.getAuthUserInstituteId())
                    .list();
            if (!departmentList.isEmpty()) {
                departmentId =
                        (departmentId != null) &&
                                (authUser.getAuthUserpermission() == definitions.ADMIN_PERMISSION)
                                ? departmentId
                                : authUser.getAuthUserDepartmentId();
                if (name.equals(null) || name.equals("") || departmentId <= 0) {
                    responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
                } else {
                    Department departmentObject = persist.loadObject(Department.class, departmentId);
                    if (departmentObject == null) {
                        responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                    } else {
                        Invitation invitation = new Invitation(name, studyYear, departmentObject);
                        persist.save(invitation);
                        responseModel = new BasicResponseModel(invitation, authUser);
                    }
                }
            } else {
                responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/invitation/update", method = RequestMethod.POST)
    public BasicResponseModel updateDepartment(
            @ModelAttribute("Invitation") Invitation invitation,
            AuthUser authUser) {
        BasicResponseModel responseModel = null;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (invitation.getId() > 0) {
                invitation.setDepartmentObject(null); // unable to change department to Event object.
                Query queryObject = null;
                if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_PERMISSION)) {
                    queryObject = persist
                            .getQuerySession()
                            .createQuery("FROM Invitation WHERE id = :id")
                            .setParameter("id", invitation.getId());
                } else if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
                    queryObject = persist
                            .getQuerySession()
                            .createQuery("FROM Invitation WHERE id = :id AND departmentObject.instituteObject.id = :instituteId")
                            .setParameter("instituteId", authUser.getAuthUserInstituteId())
                            .setParameter("id", invitation.getId());
                } else {// MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
                    queryObject = persist
                            .getQuerySession()
                            .createQuery("FROM Invitation WHERE id = :id AND departmentObject.id = :departmentId")
                            .setParameter("departmentId", authUser.getAuthUserDepartmentId())
                            .setParameter("id", invitation.getId());
                }
                List<Invitation> invitationList = queryObject.list();
                if (invitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (invitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Invitation oldInvitation = persist.loadObject(Invitation.class, invitation.getId());
                    oldInvitation.setObject(invitation);
                    persist.save(oldInvitation);
                    responseModel = new BasicResponseModel(oldInvitation, authUser);
                }
            } else {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            }

        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    //GET1 v

    @RequestMapping(value = "/invitation/getInvitation", method = RequestMethod.GET)
    public BasicResponseModel getInvitation(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {

                List<Invitation> invitationList = getQueryWithPermission(authUser, id).list();
                if (invitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (invitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Invitation.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }


    @RequestMapping(value = "/invitation/deleteInvitation", method = RequestMethod.POST)
    public BasicResponseModel deleteInvitation(
            @RequestParam int id,
            @RequestParam boolean deleted,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Invitation> invitationList = getQueryWithPermission(authUser, id).list();
                if (invitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (invitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Invitation invitation = persist.loadObject(Invitation.class, id);
                    invitation.setDeleted(deleted);
                    persist.save(invitation);
                    responseModel = new BasicResponseModel(invitation, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/invitation/getAllInvitations", method = RequestMethod.GET)
    public BasicResponseModel getAllInvitations(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            Query queryObject;
            if (authUser.getAuthUserpermission() == definitions.ADMIN_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation ORDER BY invitation.id DESC");

            } else if (authUser.getAuthUserpermission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation WHERE departmentObject.instituteObject.id = :instituteId ORDER BY invitation.id DESC")
                        .setParameter("instituteId", authUser.getAuthUserInstituteId());
            } else { //MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation WHERE departmentObject.id = :departmentId ORDER BY invitation.id DESC")
                        .setParameter("departmentId", authUser.getAuthUserDepartmentId());
            }
            List<Invitation> invitationList = queryObject.list();

            if (invitationList.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(invitationList, authUser);
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }


    @RequestMapping(value = "/invitation/getDepartments", method = RequestMethod.GET)
    public BasicResponseModel getAllDepartments(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            Query queryObject;
            if (authUser.getAuthUserpermission() == definitions.ADMIN_PERMISSION) { //GET ALL DEP
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Department AS dep ORDER BY dep.id DESC");
            } else if (authUser.getAuthUserpermission() == definitions.ADMIN_INSTITUTE_PERMISSION) { //GET ALL DEP
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Department AS dep WHERE instituteObject.id = :instituteId ORDER BY dep.id DESC ")
                        .setParameter("instituteId", authUser.getAuthUserInstituteId());
            } else { //MIMNUM PERMISSION ADMIN_DEPARTMENT_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Department AS dep WHERE id = :departmentId ORDER BY dep.id DESC ")
                        .setParameter("departmentId", authUser.getAuthUserDepartmentId());
            }
            List<Department> allDepartments = queryObject.list();

            if (allDepartments.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allDepartments, authUser);
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

}
