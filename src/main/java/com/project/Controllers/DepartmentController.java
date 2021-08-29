package com.project.Controllers;

import com.project.Models.Department;
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
public class DepartmentController extends BaseController {
    @Autowired
    private Persist persist;

    @RequestMapping(value = "/department/add", method = RequestMethod.POST)
    public BasicResponseModel addInstitute(@RequestParam String name,
                                           @RequestParam(required = false) Integer instituteId,
                                           AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            List<Institute> instituteList = persist.getQuerySession().createQuery("FROM Institute WHERE id = :id")
                    .setParameter("id", authUser.getAuthUserInstituteId())
                    .list();
            if (!instituteList.isEmpty()) {
               // instituteId = instituteId != null ? instituteId : authUser.getAuthUserInstituteId();

                instituteId =
                        (instituteId != null) &&
                                (authUser.getAuthUserpermission() == Definitions.ADMIN_PERMISSION)
                                ? instituteId
                                : authUser.getAuthUserInstituteId();
                if (name.equals(null) || instituteId < 0) {
                    responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
                } else {
                    Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                    if (instituteObject == null) {
                        responseModel = new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                    } else {
                        Department department = new Department(name, instituteObject);
                        persist.save(department);
                        responseModel = new BasicResponseModel(department, authUser);
                    }
                }
            } else {
                responseModel = new BasicResponseModel(Definitions.INSTITUTE_NOT_EXISTS, Definitions.INSTITUTE_NOT_EXISTS_MSG);
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/update", method = RequestMethod.POST)
    public BasicResponseModel updateDepartment(
            @ModelAttribute("Department") Department department,
            @RequestParam(required = false) Integer instituteId,
            AuthUser authUser) {

        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            instituteId =
                    (instituteId != null) &&
                            (authUser.getAuthUserpermission() == Definitions.ADMIN_PERMISSION)
                            ? instituteId
                            : authUser.getAuthUserInstituteId();
            if (department.getId() <= 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = persist.getQuerySession().createQuery("FROM Department WHERE id = :id")
                        .setParameter("id", department.getId())
                        .list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    if (instituteId > 0 && instituteId != departmentRow.get(0).getInstituteObject().getId()) { // we update the department.
                        Institute instituteObject = persist.loadObject(Institute.class, instituteId);
                        if(instituteObject == null){
                            responseModel = new BasicResponseModel(Definitions.INSTITUTE_NOT_EXISTS, Definitions.INSTITUTE_NOT_EXISTS_MSG);
                        }else{
                            //need to update
                            department.setInstituteObject(instituteObject);
                            Department oldDepartment = persist.loadObject(Department.class, department.getId());
                            oldDepartment.setObject(department);
                            persist.save(oldDepartment);
                            responseModel = new BasicResponseModel(oldDepartment, authUser);
                        }
                    } else {
                        Department oldDepartment = persist.loadObject(Department.class, department.getId());
                        oldDepartment.setObject(department);
                        persist.save(oldDepartment);
                        responseModel = new BasicResponseModel(oldDepartment, authUser);
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

    @RequestMapping(value = "/department/deleteDepartment", method = RequestMethod.POST)
    public BasicResponseModel deleteDepartment(@RequestParam int id, @RequestParam boolean delete, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = persist.getQuerySession().createQuery("FROM Department WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    Department department = persist.loadObject(Department.class, id);
                    department.setDeleted(delete);
                    persist.save(department);
                    responseModel = new BasicResponseModel(department, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getDepartment", method = RequestMethod.GET)
    public BasicResponseModel getDepartment(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(Definitions.MISSING_FIELDS, Definitions.MISSING_FIELDS_MSG);
            } else {
                List<Department> departmentRow = persist.getQuerySession().createQuery("FROM Department WHERE id = :id")
                        .setParameter("id", id)
                        .list();
                if (departmentRow.isEmpty()) {
                    responseModel = new BasicResponseModel(Definitions.DEPARTMENT_NOT_FOUND, Definitions.DEPARTMENT_NOT_FOUND_MSG);
                } else if (departmentRow.size() > 1) {
                    responseModel = new BasicResponseModel(Definitions.MULTI_RECORD, Definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Department.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getAllDepartments", method = RequestMethod.GET)
    public BasicResponseModel getAllDepartments(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
            List<Department> allDepartments = persist.getQuerySession().createQuery("FROM Department AS dep ORDER BY dep.id DESC").list();
            if (allDepartments.isEmpty()) {
                responseModel = new BasicResponseModel(Definitions.EMPTY_LIST, Definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(allDepartments, authUser);
            }
        } else if (authUser.getAuthUserpermission() == Definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(Definitions.INVALID_TOKEN, Definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(Definitions.NO_PERMISSIONS, Definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/department/getAllInstitutes", method = RequestMethod.GET)
    public BasicResponseModel getAllInstitutes(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (Permissions.validPermission(authUser.getAuthUserpermission(), Definitions.ADMIN_INSTITUTE_PERMISSION)) {
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

}
