package com.project.Controllers;

import com.project.Models.Invitation;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Objects.Entities.StatisticsResponse;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Transactional
public class StatisticsController extends BaseController {
    @Autowired
    private Persist persist;
    @Autowired
    private Permissions permissions;
    @Autowired
    private Definitions definitions;

    private StatisticsResponse countInstituteModel(AuthUser authUser) {
        StatisticsResponse statisticsResponse = null;
        if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM Institute").uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_INSTITUTE_TITLE, definitions.STATS_INSTITUTE_ICON, count);
        }
        return statisticsResponse;
    }

    private StatisticsResponse countDepartmentModel(AuthUser authUser) {
        StatisticsResponse statisticsResponse = null;
        if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM Department")
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_DEPARTMENT_TITLE, definitions.STATS_DEPARTMENT_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM Department WHERE instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUser_instituteId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_DEPARTMENT_TITLE, definitions.STATS_DEPARTMENT_ICON, count);
        }
        return statisticsResponse;
    }

    private StatisticsResponse countUserModel(AuthUser authUser) {
        StatisticsResponse statisticsResponse = null;
        if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM User")
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_USER_TITLE, definitions.STATS_USER_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM User WHERE instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUser_instituteId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_USER_TITLE, definitions.STATS_USER_ICON, count);
        }
        return statisticsResponse;
    }

    private StatisticsResponse countInvitationModel(AuthUser authUser) {
        StatisticsResponse statisticsResponse = null;
        if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM Invitation")
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_INVITATION_TITLE, definitions.STATS_INVITATION_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
            Long count = (Long) persist.getQuerySession()
                    .createQuery("SELECT COUNT(*) FROM Invitation WHERE departmentObject.instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUser_instituteId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_INVITATION_TITLE, definitions.STATS_INVITATION_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_DEPARTMENT_PERMISSION) {
            Long count = (Long) persist.getQuerySession()
                    .createQuery("SELECT COUNT(*) FROM Invitation WHERE departmentObject.id = :departmentId")
                    .setParameter("departmentId", authUser.getAuthUser_departmentId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_INVITATION_TITLE, definitions.STATS_INVITATION_ICON, count);
        }
        return statisticsResponse;
    }

    private StatisticsResponse countCandidateModel(AuthUser authUser) {
        StatisticsResponse statisticsResponse = null;
        if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
            Long count = (Long) persist.getQuerySession().createQuery("SELECT COUNT(*) FROM Candidate")
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_CANDIDATE_TITLE, definitions.STATS_CANDIDATE_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
            Long count = (Long) persist.getQuerySession()
                    .createQuery("SELECT COUNT(*) FROM Candidate WHERE invitationObject.departmentObject.instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUser_instituteId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_CANDIDATE_TITLE, definitions.STATS_CANDIDATE_ICON, count);
        } else if (authUser.getAuthUser_permission() == definitions.ADMIN_DEPARTMENT_PERMISSION) {
            Long count = (Long) persist.getQuerySession()
                    .createQuery("SELECT COUNT(*) FROM Candidate WHERE invitationObject.departmentObject.id = :departmentId")
                    .setParameter("departmentId", authUser.getAuthUser_departmentId())
                    .uniqueResult();
            statisticsResponse = new StatisticsResponse(definitions.STATS_CANDIDATE_TITLE, definitions.STATS_CANDIDATE_ICON, count);
        }
        return statisticsResponse;
    }

    @RequestMapping(value = "/statistics/getCounts", method = RequestMethod.GET)
    public BasicResponseModel getCounts(AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            List<StatisticsResponse> statisticsResponses = new ArrayList<>();
            if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
                statisticsResponses.add(countInstituteModel(authUser));
                statisticsResponses.add(countDepartmentModel(authUser));
                statisticsResponses.add(countUserModel(authUser));
                statisticsResponses.add(countInvitationModel(authUser));
                statisticsResponses.add(countCandidateModel(authUser));
            } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
                statisticsResponses.add(countDepartmentModel(authUser));
                statisticsResponses.add(countUserModel(authUser));
                statisticsResponses.add(countInvitationModel(authUser));
                statisticsResponses.add(countCandidateModel(authUser));
            } else {//ADMIN_DEPARTMENT_PERMISSION
                statisticsResponses.add(countInvitationModel(authUser));
                statisticsResponses.add(countCandidateModel(authUser));
            }
            responseModel = new BasicResponseModel(statisticsResponses);
        } else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/statistics/lastestInvitations", method = RequestMethod.GET)
    public BasicResponseModel lastestInvitations(AuthUser authUser) {
        BasicResponseModel responseModel = null;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            Query queryObject;
            if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation ORDER BY invitation.id DESC ");

            } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation WHERE departmentObject.instituteObject.id = :instituteId ORDER BY invitation.id DESC")
                        .setParameter("instituteId", authUser.getAuthUser_instituteId());
            } else { //MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Invitation AS invitation WHERE departmentObject.id = :departmentId ORDER BY invitation.id DESC")
                        .setParameter("departmentId", authUser.getAuthUser_departmentId());
            }
            List<Invitation> invitationList = queryObject.setMaxResults(5).list();

            if (invitationList.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                for (int i = 0; i < invitationList.size(); i++) {
                    invitationList.get(i).setNumOfCandidates(
                            (Long)persist.getQuerySession()
                            .createQuery("SELECT COUNT(*) FROM Candidate WHERE invitationObject.id = :invitationId")
                            .setParameter("invitationId", invitationList.get(i).getId())
                            .uniqueResult()
                    );
                }
                responseModel = new BasicResponseModel(invitationList);
            }
        }else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/statistics/lastestCandidates", method = RequestMethod.GET)
    public BasicResponseModel lastestCandidates(AuthUser authUser) {
        BasicResponseModel responseModel = null;
        if (permissions.validPermission(authUser.getAuthUser_permission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            Query queryObject;
            if (authUser.getAuthUser_permission() == definitions.ADMIN_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Candidate AS can ORDER BY can.id DESC ");

            } else if (authUser.getAuthUser_permission() == definitions.ADMIN_INSTITUTE_PERMISSION) {
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Candidate AS can WHERE invitationObject.departmentObject.instituteObject.id = :instituteId ORDER BY can.id DESC")
                        .setParameter("instituteId", authUser.getAuthUser_instituteId());
            } else { //MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
                queryObject = persist
                        .getQuerySession()
                        .createQuery("FROM Candidate AS can WHERE invitationObject.departmentObject.id = :departmentId ORDER BY can.id DESC")
                        .setParameter("departmentId", authUser.getAuthUser_departmentId());
            }
            List<Invitation> invitationList = queryObject.setMaxResults(5).list();

            if (invitationList.isEmpty()) {
                responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
            } else {
                responseModel = new BasicResponseModel(invitationList);
            }
        }else if (authUser.getAuthUser_permission() == definitions.INVALID_TOKEN || authUser.getAuthUser_permission() == definitions.GUEST_PERMISSION) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }
}
