package com.project.Controllers;

import com.project.Models.Department;
import com.project.Models.Event;
import com.project.Models.Institute;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Transactional
public class EventController extends BaseController{
    @Autowired private Persist persist;

    @Autowired private Definitions definitions;

    @Autowired private Permissions permissions;

    @RequestMapping(value = "/event/add", method = RequestMethod.POST)
    public BasicResponseModel addEvent(
            @RequestParam String name,
            @RequestParam(required = false) Integer departmentId,
            AuthUser authUser)
    {
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
                if (name.equals(null) || name.equals("")|| departmentId <= 0) {
                    responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
                } else {
                    Department departmentObject = persist.loadObject(Department.class, departmentId);
                    if (departmentObject == null) {
                        responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                    } else {
                        Event event = new Event(name, departmentObject);
                        persist.save(event);
                        responseModel = new BasicResponseModel(event, authUser);
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
    //GET1 v

    //GETALL v

    //UPDATE

    //DELETE

    @RequestMapping(value = "/event/deleteEvent", method = RequestMethod.POST)
    public BasicResponseModel deleteEvent(@RequestParam int id, @RequestParam boolean delete, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Event> eventList = persist.getQuerySession().createQuery("FROM Event WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (eventList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.EVENT_NOT_FOUND, definitions.EVENT_NOT_FOUND_MSG);
                } else if (eventList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Department department = persist.loadObject(Department.class, id);
                    department.setDeleted(delete);
                    persist.save(department);
                    responseModel = new BasicResponseModel(department, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }
}
