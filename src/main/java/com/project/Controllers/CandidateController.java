package com.project.Controllers;

import com.google.gson.JsonObject;
import com.project.Models.Candidate;
import com.project.Objects.Entities.AuthUser;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Objects.Entities.CandidatesState;
import com.project.Persist;
import com.project.Services.UploadService;
import com.google.gson.JsonArray;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Transactional
public class CandidateController extends BaseController{
    @Autowired private UploadService uploadService;
    @Autowired private Persist persist;

    @RequestMapping(value = "/candidate/uploadxlsx", method = RequestMethod.POST)
    public BasicResponseModel upload(
            @RequestParam("file") MultipartFile file,
            AuthUser authUser) throws Exception
    {
        BasicResponseModel responseModel;
        BasicResponseModel convertFileResponse = uploadService.uploadAndConvertXlsx2JSON(file);
        if(convertFileResponse.getObject() != null && !convertFileResponse.getCustomMessage().equals(null)){
            //HERE WE INSERT TO DATABASE
            JsonArray candidates = (JsonArray) convertFileResponse.getObject();
            List<CandidatesState> candidatesStatus = new ArrayList<>();
            for (int i = 0; i < candidates.size() ; i++) {
                Candidate candidate = new Candidate();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                JsonObject can = candidates.get(i).getAsJsonObject();
                if (can.get("uid") != null && can.get("candidateName") != null) {
                    if(can.get("uid").getAsString().equals("")  || can.get("candidateName").getAsString().equals("")) {
                        candidatesStatus.add(new CandidatesState(can.get("candidateName").getAsString(), true));
                    }else{
                        candidate.setUid(can.get("uid").getAsString()); //required
                        candidate.setCandidateName(can.get("candidateName").getAsString()); // required
                        candidate.setEventDate(String.valueOf(timestamp.getTime()));

                        candidate.setScheduleDate((can.get("scheduleDate") != null) ? can.get("scheduleDate").getAsString() : "");
                        candidate.setScheduleDate((can.get("candidateStatus") != null) ? can.get("candidateStatus").getAsString() : "");
                        candidate.setScheduleDate((can.get("comment") != null) ? can.get("comment").getAsString() : "");

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
        }else{
            responseModel = new BasicResponseModel(convertFileResponse.getErrorCode(), convertFileResponse.getErrorName());
        }
        return responseModel;
    }
}
