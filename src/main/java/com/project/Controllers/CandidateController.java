package com.project.Controllers;

import com.google.gson.JsonObject;
import com.project.Models.Candidate;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Services.UploadService;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Transactional
public class CandidateController {
    @Autowired private UploadService uploadService;
    @Autowired private Persist persist;

    @RequestMapping(value = "/candidate/uploadxlsx", method = RequestMethod.POST)
    public BasicResponseModel upload(@RequestParam("file") MultipartFile file) throws Exception{
        BasicResponseModel responseModel = null;
        BasicResponseModel convertFileResponse = uploadService.uploadAndConvertXlsx2JSON(file);
        if(convertFileResponse.getObject() != null && !convertFileResponse.getCustomMessage().equals(null)){
            //HERE WE FETCH TO DATABASE
            JsonArray candidates = (JsonArray) convertFileResponse.getObject();
            for (int i = 0; i < candidates.size() ; i++) {
                Candidate candidate = new Candidate();
                JsonObject can = candidates.get(i).getAsJsonObject();
                candidate.setUid(can.get("uid").getAsString());
                candidate.setCandidateName(can.get("candidateName").getAsString());
                candidate.setCandidateStatus(can.get("candidateStatus").getAsString());
                candidate.setComment(can.get("comment").getAsString());
                persist.save(candidate);
            }
            //responseModel = new BasicResponseModel();
        }else{
            responseModel = new BasicResponseModel(convertFileResponse.getErrorCode(), convertFileResponse.getErrorName());
        }
        return responseModel;
    }
}
