package com.project.Controllers;

import com.google.gson.JsonObject;
import com.project.Models.Candidate;
import com.project.Models.Department;
import com.project.Models.Institute;
import com.project.Models.Invitation;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Objects.Entities.CandidatesState;
import com.project.Persist;
import com.project.Services.UploadService;
import com.google.gson.JsonArray;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@RestController
@Transactional
public class CandidateController extends BaseController {
    @Autowired private UploadService uploadService;
    @Autowired private Persist persist;
    @Autowired private Permissions permissions;
    @Autowired private Definitions definitions;

    private Query getInvitationQueryWithPermission(AuthUser authUser, Integer invitationId ){
        Query queryObject;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_PERMISSION)) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id")
                    .setParameter("id", invitationId);
        } else if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id AND departmentObject.instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUserInstituteId())
                    .setParameter("id", invitationId);
        } else {// MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Invitation WHERE id = :id AND departmentObject.id = :departmentId")
                    .setParameter("departmentId", authUser.getAuthUserDepartmentId())
                    .setParameter("id", invitationId);
        }
        return queryObject;
    }

    private Query getCandidateQueryWithPermission(AuthUser authUser, Integer candidateId ){
        Query queryObject;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_PERMISSION)) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Candidate WHERE id = :id")
                    .setParameter("id", candidateId);
        } else if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_INSTITUTE_PERMISSION)) {
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Candidate WHERE id = :id AND invitationObject.departmentObject.instituteObject.id = :instituteId")
                    .setParameter("instituteId", authUser.getAuthUserInstituteId())
                    .setParameter("id", candidateId);
        } else {// MINIMUM PERMISSION ~ ADMIN_DEPARTMENT_PERMISSION
            queryObject = persist
                    .getQuerySession()
                    .createQuery("FROM Candidate WHERE id = :id AND invitationObject.departmentObject.id = :departmentId")
                    .setParameter("departmentId", authUser.getAuthUserDepartmentId())
                    .setParameter("id", candidateId);
        }
        return queryObject;
    }


    @RequestMapping(value = "/candidate/uploadxlsx", method = RequestMethod.POST)
    public BasicResponseModel upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer invitationId,
            AuthUser authUser) throws Exception {
        BasicResponseModel responseModel = null;

        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (invitationId != null && invitationId > 0) {
                List<Invitation> invitationList = getInvitationQueryWithPermission(authUser, invitationId).list();
                if (invitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (invitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    BasicResponseModel convertFileResponse = uploadService.uploadAndConvertXlsx2JSON(file);
                    if (convertFileResponse.getObject() != null && !convertFileResponse.getCustomMessage().equals(null)) {
                        //HERE WE INSERT TO DATABASE
                        JsonArray candidates = (JsonArray) convertFileResponse.getObject();
                        List<CandidatesState> candidatesStatus = new ArrayList<>();
                        for (int i = 0; i < candidates.size(); i++) {
                            Candidate candidate = new Candidate();
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            JsonObject can = candidates.get(i).getAsJsonObject();
                            if (can.get("uid") != null && can.get("candidateName") != null) { //uid and canidatenName is not null
                                if (can.get("uid").getAsString().equals("") || can.get("candidateName").getAsString().equals("")) {
                                    candidatesStatus.add(new CandidatesState(can.get("candidateName").getAsString(), true));
                                } else {
                                    candidate.setUid(can.get("uid").getAsString()); //required
                                    candidate.setCandidateName(can.get("candidateName").getAsString()); // required
                                    candidate.setEventDate(String.valueOf(timestamp.getTime()));

                                    //TODO check why those rows doesnot insert to db.
                                    candidate.setScheduleDate((can.get("scheduleDate") != null) ? can.get("scheduleDate").getAsString() : "");
                                    candidate.setScheduleDate((can.get("candidateStatus") != null) ? can.get("candidateStatus").getAsString() : "");
                                    candidate.setScheduleDate((can.get("comment") != null) ? can.get("comment").getAsString() : "");
                                    candidate.setInvitationObject(invitationList.get(0));
                                    //update candidate state list
                                    candidatesStatus.add(new CandidatesState(can.get("candidateName").getAsString(), false));

                                    //insert candidate to database
                                    persist.save(candidate);
                                }
                            } else if (can.get("candidateName") != null) {

                                //update candidate state list
                                candidatesStatus.add(new CandidatesState(can.get("candidateName").getAsString(), true));
                            }
                        }
                        responseModel = new BasicResponseModel(candidatesStatus, authUser);
                    } else {
                        responseModel = new BasicResponseModel(convertFileResponse.getErrorCode(), convertFileResponse.getErrorName());
                    }
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

    //add
    @RequestMapping(value = "/candidate/add", method = RequestMethod.POST)
    public BasicResponseModel addCandidate(
            @ModelAttribute("Candidate") Candidate candidate,
            @RequestParam Integer invitationId,
            AuthUser authUser
    ) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (candidate.isValidObject() && invitationId != null && invitationId > 0) {
                List<Invitation> invitationList = getInvitationQueryWithPermission(authUser, invitationId).list();
                if (invitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (invitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    candidate.setEventDate(String.valueOf(timestamp.getTime()));
                    candidate.setInvitationObject(invitationList.get(0));


                    persist.save(candidate);
                    responseModel = new BasicResponseModel(candidate, authUser);
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

    @RequestMapping(value = "/candidate/getCandidatesWithInvitationId", method = RequestMethod.GET)
    public BasicResponseModel getCandidatesWithInvitationId(
            Integer invitationId,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if(invitationId == null || invitationId < 1) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            }else{
                List<Invitation> InvitationList = getInvitationQueryWithPermission(authUser, invitationId).list();
                if (InvitationList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.INVITATION_NOT_FOUND, definitions.INVITATION_NOT_FOUND_MSG);
                } else if (InvitationList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    List<Candidate> allCandidate =
                            persist.getQuerySession()
                                    .createQuery("FROM Candidate AS can WHERE invitationObject.id = :invitationId ORDER BY can.id DESC ")
                                    .setParameter("invitationId", invitationId)
                                    .list();
                    if (allCandidate.isEmpty()) {
                        responseModel = new BasicResponseModel(definitions.EMPTY_LIST, definitions.EMPTY_LIST_MSG);
                    } else {
                        responseModel = new BasicResponseModel(allCandidate, authUser);
                    }
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/candidate/getCandidate", method = RequestMethod.GET)
    public BasicResponseModel getCandidate(@RequestParam int id, AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Candidate> candidateList = getCandidateQueryWithPermission(authUser, id).list();
                if (candidateList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.CANDIDATE_NOT_FOUND, definitions.CANDIDATE_NOT_FOUND_MSG);
                } else if (candidateList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    responseModel = new BasicResponseModel(persist.loadObject(Candidate.class, id), authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    //get all



    @RequestMapping(value = "/candidate/deleteCandidate", method = RequestMethod.POST)
    public BasicResponseModel deleteCandidate(
            @RequestParam int id,
            @RequestParam boolean deleted,
            AuthUser authUser) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (id < 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                List<Candidate> candidateList = getCandidateQueryWithPermission(authUser, id).list();
                if (candidateList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.CANDIDATE_NOT_FOUND, definitions.CANDIDATE_NOT_FOUND_MSG);
                } else if (candidateList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Candidate candidate = persist.loadObject(Candidate.class, id);
                    candidate.setDeleted(deleted);
                    persist.save(candidate);
                    responseModel = new BasicResponseModel(candidate, authUser);
                }
            }
        } else if (authUser.getAuthUserpermission() == definitions.INVALID_TOKEN) {
            responseModel = new BasicResponseModel(definitions.INVALID_TOKEN, definitions.INVALID_TOKEN_MSG);
        } else {
            responseModel = new BasicResponseModel(definitions.NO_PERMISSIONS, definitions.NO_PERMISSIONS_MSG);
        }
        return responseModel;
    }

    @RequestMapping(value = "/candidate/update", method = RequestMethod.POST)
    public BasicResponseModel updateCandidate(
            @ModelAttribute("Candidate") Candidate candidate,
            AuthUser authUser
    ) {
        BasicResponseModel responseModel;
        if (permissions.validPermission(authUser.getAuthUserpermission(), definitions.ADMIN_DEPARTMENT_PERMISSION)) {
            if (candidate.getId() <= 0) {
                responseModel = new BasicResponseModel(definitions.MISSING_FIELDS, definitions.MISSING_FIELDS_MSG);
            } else {
                candidate.setInvitationObject(null); //unable to change Invitation.
                candidate.setEventDate(null); //unable to change Invitation.
                List<Candidate> candidateList = getCandidateQueryWithPermission(authUser, candidate.getId()).list();
                if (candidateList.isEmpty()) {
                    responseModel = new BasicResponseModel(definitions.CANDIDATE_NOT_FOUND, definitions.CANDIDATE_NOT_FOUND_MSG);
                } else if (candidateList.size() > 1) {
                    responseModel = new BasicResponseModel(definitions.MULTI_RECORD, definitions.MULTI_RECORD_MSG);
                } else {
                    Candidate oldCandidate = persist.loadObject(Candidate.class, candidate.getId());
                    oldCandidate.setObject(candidate);
                    persist.save(oldCandidate);
                    responseModel = new BasicResponseModel(oldCandidate, authUser);
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
