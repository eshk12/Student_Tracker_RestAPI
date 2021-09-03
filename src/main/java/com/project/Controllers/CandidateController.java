package com.project.Controllers;

import com.project.Objects.Entities.BasicResponseModel;
import com.project.Objects.Entities.FileResponse;
import com.project.Services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@Transactional
public class CandidateController {
    @Autowired private UploadService uploadService;

    @PostMapping("/upload")
    public BasicResponseModel upload(@RequestParam("file") MultipartFile file) throws Exception{
        BasicResponseModel responseModel = null;
        BasicResponseModel convertFileResponse = uploadService.uploadAndConvertXlsx2JSON(file);
        //System.out.println(convertFileResponse);
        if(convertFileResponse.getObject() != null && !convertFileResponse.getCustomMessage().equals(null)){
            responseModel = new BasicResponseModel(convertFileResponse.getObject().toString());
        }else{
            responseModel = new BasicResponseModel(convertFileResponse.getErrorCode(), convertFileResponse.getErrorName());
        }
        return responseModel;
    }
}
