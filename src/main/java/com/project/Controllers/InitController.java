package com.project.Controllers;


import com.project.Models.Department;
import com.project.Models.Institute;
import com.project.Models.User;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Persist;
import com.project.Utils.Definitions;
import com.project.Utils.PasswordAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Transactional
public class InitController {
    @Autowired private Persist persist;
    @Autowired private PasswordAuthentication passwordAuthentication;
    @Autowired private Definitions definitions;

    @RequestMapping(value = "/init/firstrun", method = RequestMethod.POST)
    public BasicResponseModel firstRun()
    {
        BasicResponseModel responseModel;
        List<User> userRow = persist.getQuerySession()
                .createQuery("FROM User")
                .list();
        if (userRow.isEmpty()) { //
            Institute institute = new Institute("מכללת אשקלון","Itzik Barabie","052-3937296","Brechia","Shir Hashirim","23");
            persist.save(institute);
            Department department = new Department("מדעי המחשב",institute);
            persist.save(department);

            User user = new User(
                    "05",
                    "Itzik",
                    "Barabie",
                    "12345678",
                    "itshakbar@gmail.com",
                    "052-3937296",
                    "",
                    1,
                    department,
                    institute,
                    false
            );
            user.setInstituteObject(institute);
            user.setPassword(passwordAuthentication.hashPassword(user.getPassword()));
            persist.save(user);
            responseModel = new BasicResponseModel(user);
        } else {
            responseModel = new BasicResponseModel(definitions.INITILIZE_FAILED, definitions.INITILIZE_FAILED_MSG);
        }
        return responseModel;
    }

}
