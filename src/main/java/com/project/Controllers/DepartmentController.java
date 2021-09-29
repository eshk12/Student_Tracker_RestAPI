package com.project.Controllers;

import com.project.Models.Department;
import com.project.Models.Institute;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class DepartmentController extends BaseController {

    @Autowired private Persist persist;
    @Autowired private Definitions definitions;
    @Autowired private Permissions permissions;

    private Query getQueryWithPermission(AuthUser authUser, int id){
        Query queryObject;
        if(permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)){
            queryObject = persist.getQuerySession().createQuery("FROM Department WHERE id = :id")
                    .setParameter("id", id);
        }else{
            queryObject = persist.getQuerySession().createQuery("FROM Department WHERE id = :id AND instituteObject.id = :instituteId")
                    .setParameter("id", id)
                    .setParameter("instituteId", authUser.getAuthUser_instituteId());
        }
        return queryObject;
    }
    @RequestMapping(value = "/department/add", method = RequestMethod.POST)
    public BasicResponseModel addDepartment(
            @RequestParam String name,
            @RequestParam(required = false) Integer instituteId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            List<Institute> instituteList = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                    .setParameter("id", authUser.getAuthUser_instituteId())
                    .list();
            if (!instituteList.isEmpty()) {//here we check if the instituteId inserted is exists

                instituteId = //here we check if ADMIN_INSTITUTE_PERMISSION enter another instituteId of its own,
                              // if he is so we cast it into  his instituteId.
                        (instituteId != null) &&
                                (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION)
                                ? instituteId
                                : authUser.getAuthUser_instituteId();
                if (name.equals(null) || name.equals("") || instituteId <= 0) {
                    responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
                } else {
                    Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                    if (instituteObject == null) {
                        responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
                    } else {
                        Department department = new Department(name, instituteObject);
                        persist.save(department);
                        responseModel = new BasicResponseModel(department);
                    }
                }
            } else {
                responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_EXISTS, definitions.INSTITUTE_NOT_EXISTS_MSG);
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }


        return responseModel;
    }

    @RequestMapping(value = "/department/update", method = RequestMethod.POST)
    public BasicResponseModel updateDepartment(
            @ModelAttribute("Department") Department department,
            @RequestParam(required = false) Integer instituteId,
            AuthUser authUser) {

        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            instituteId =
                    (instituteId != null) &&
                            (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION)
                            ? instituteId
                            : authUser.getAuthUser_instituteId();
            if (department.getId() <= 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = getQueryWithPermission(authUser, department.getId()).list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    if (instituteId > 0 && instituteId != departmentRow.get(0).getInstituteObject().getId()) { // we update the department.
                        Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                        if (instituteObject == null) {
                            responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_EXISTS, definitions.INSTITUTE_NOT_EXISTS_MSG);
                        } else {
                            //need to update
                            department.setInstituteObject(instituteObject);
                            Department oldDepartment = persist.loadObject(Department.class, department.getId());
                            oldDepartment.setObject(department);
                            persist.save(oldDepartment);
                            responseModel = new BasicResponseModel(oldDepartment);
                        }
                    } else {
                        Department oldDepartment = persist.loadObject(Department.class, department.getId());
                        oldDepartment.setObject(department);
                        persist.save(oldDepartment);
                        responseModel = new BasicResponseModel(oldDepartment);
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

    @RequestMapping(value = "/department/deleteDepartment", method = RequestMethod.POST)
    public BasicResponseModel deleteDepartment(@RequestParam int id, @RequestParam boolean delete, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = getQueryWithPermission(authUser, id).list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Department department = persist.loadObject(Department.class, id);
                    department.setDeleted(delete);
                    persist.save(department);
                    responseModel = new BasicResponseModel(department);
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getDepartment", method = RequestMethod.GET)
    public BasicResponseModel getDepartment(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = getQueryWithPermission(authUser, id).list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.DEPARTMENT_NOT_FOUND, definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Department.class, id));
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getAllDepartments", method = RequestMethod.GET)
    public BasicResponseModel getAllDepartments(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            Query queryObject;
            if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Department AS dep ORDER BY dep.id DESC");
            } else { //MIMNUM PERMISSION ADMIN_INSTITUTE_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Department AS dep WHERE instituteObject.id = :instituteId ORDER BY dep.id DESC ")
                        .setParameter("instituteId", authUser.getAuthUser_instituteId());
            }
            List<Department> allDepartments = queryObject.list();

            if (allDepartments.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allDepartments);
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getAllDepartmentsWithInstituteId", method = RequestMethod.GET)
    public BasicResponseModel getAllDepartmentsWithInstituteId(
            Integer instituteId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            //if user is not ADMIN_PERMISSION THEN use authUser instituteId,
            //if user has ADMIN_PERMISSION THEN use the inserted instituteId if exists.\
            instituteId =
                    (instituteId != null) &&
                            (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION)
                            ? instituteId
                            : authUser.getAuthUser_instituteId();
            List<Institute> instituteRow = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                    .setParameter("id", instituteId)
                    .list();
            if (instituteRow.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.INSTITUTE_NOT_FOUND, definitions.INSTITUTE_NOT_FOUND_MSG);
            } else if (instituteRow.size() > 1) {
                responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
            } else {
                List<Department> allDepartments =
                        persist.getQuerySession()
                                .createQuery("FROM Department AS dep WHERE instituteObject.id = :instituteId ORDER BY dep.id DESC ")
                                .setParameter("instituteId", instituteId)
                                .list();
                if (allDepartments.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
                } else {
                    responseModel = new BasicResponseModel(allDepartments);
                }
            }
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getAllInstitutes", method = RequestMethod.GET)
    public BasicResponseModel getAllInstitutes(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            Query queryObject;
            if(permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_PERMISSION)){
                queryObject = persist.getQuerySession().createQuery("FROM Institute AS ins ORDER BY ins.id DESC");

            }else{
                queryObject = persist.getQuerySession().createQuery("FROM Institute AS ins WHERE id = :id ORDER BY ins.id DESC")
                        .setParameter("id", authUser.getAuthUser_instituteId());
            }
            List<Institute> allInstitutes = queryObject.list();
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

}
