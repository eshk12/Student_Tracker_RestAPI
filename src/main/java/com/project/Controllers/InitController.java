package com.project.Controllers;


import com.project.Models.Department;
import com.project.Models.Institute;
import com.project.Models.User;
import com.project.Persist;
import com.project.Utils.PasswordAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
public class InitController {
    @Autowired
    private Persist persist;

    @RequestMapping(value = "/init/firstrun", method = RequestMethod.POST)
    public void firstRun()
    {
        Institute institute = new Institute("Admin Institute2","Itzik Barabie","052-3937296","Brechia","Shir Hashirim","23");
        persist.save(institute);
        Department department = new Department("Admin Department2",institute);
        persist.save(department);

        User user = new User(
                "05",
                "Itzik",
                "Barabie",
                "1234",
                "itshakbar1@gmail.com",
                "052-3937296",
                "",
                1,
                department,
                institute,
                false
                );
        user.setInstituteObject(institute);
        user.setPassword(PasswordAuthentication.hashPassword(user.getPassword()));
        persist.save(user);
    }

}
