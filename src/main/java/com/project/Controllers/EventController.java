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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class EventController extends BaseController {
    @Autowired private Persist persist;
    @Autowired private Definitions definitions;
    @Autowired private Permissions permissions;

    @RequestMapping(value = "/event/add", method = RequestMethod.POST)
    public BasicResponseModel addEvent(
            @RequestParam String name,
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

    @RequestMapping(value = "/event/update", method = RequestMethod.POST)
    public BasicResponseModel updateDepartment(
            @ModelAttribute("Event") Event event,
            AuthUser authUser) {
        BasicResponseModel responseModel = null;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (event.getId() > 0) {
                event.setDepartmentObject(null); // unable to change department to Event object.
                String query;
                if(permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_PERMISSION)){
                    query = "FROM Event WHERE id = :id AND :departmentId = :departmentId AND :instituteId = :instituteId" ;
                } else if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
                    query = "FROM Event WHERE id = :id AND departmentObject.instituteObject.id = :instituteId AND :departmentId = :departmentId";
                }else{// MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
                    query = "FROM Event WHERE id = :id AND departmentObject.id = :departmentId AND :instituteId = :instituteId";
                }
                List<Event> eventList = persist.getQuerySession().createQuery(query)
                        .setParameter("id", event.getId())
                        .setParameter("instituteId", authUser.getAuthUserInstituteId())
                        .setParameter("departmentId", authUser.getAuthUserDepartmentId())
                        .list();
                if (eventList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.EVENT_NOT_FOUND, definitions.EVENT_NOT_FOUND_MSG);
                } else if (eventList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                        Event oldEvent = persist.loadObject(Event.class, event.getId());
                        oldEvent.setObject(event);
                        persist.save(oldEvent);
                        responseModel = new BasicResponseModel(oldEvent, authUser);
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

    @RequestMapping(value = "/event/getEvent", method = RequestMethod.GET)
    public BasicResponseModel getDepartment(@RequestParam int id, AuthUser authUser) {
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
                    responseModel = new BasicResponseModel(persist.loadObject(Event.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }


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
                    Event event = persist.loadObject(Event.class, id);
                    event.setDeleted(delete);
                    persist.save(event);
                    responseModel = new BasicResponseModel(event, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/event/getAllEvents", method = RequestMethod.GET)
    public BasicResponseModel getAllEvents(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            List<Event> eventList = persist.getQuerySession().createQuery("FROM Event AS event ORDER BY event.id DESC").list();
            if (eventList.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(eventList, authUser);
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }


}
