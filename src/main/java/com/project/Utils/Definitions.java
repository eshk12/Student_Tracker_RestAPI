package com.project.Utils;

import org.springframework.stereotype.Component;

@Component
public class Definitions {
    public final int MISSING_FIELDS = 500;
    public final String MISSING_FIELDS_MSG = "ישנם פרטים חסרים בטופס!";

    public final int INVALID_EMAIL = 500;
    public final String INVALID_EMAIL_MSG = "אימל לא תקין!";

    public final int EMAIL_EXISTS = 500;
    public final String EMAIL_EXISTS_MSG = "האימל קיים כבר במערכת";

    public final int USER_NOT_FOUND = 501;
    public final String USER_NOT_FOUND_MSG = "משתמש לא קיים";

    public final int INSTITUTE_NOT_FOUND = 501;
    public final String INSTITUTE_NOT_FOUND_MSG = "המוסד לא קיים במערכת.";

    public final int DEPARTMENT_NOT_FOUND = 501;
    public final String DEPARTMENT_NOT_FOUND_MSG = "החוג לא קיים במערכת.";

    public final int EVENT_NOT_FOUND = 501;
    public final String EVENT_NOT_FOUND_MSG = "האירוע לא קיים במערכת.";

    public final int DEPARTMENTS_NOT_FOUND = 501;
    public final String DEPARTMENTS_NOT_FOUND_MSG = "לא קיימים חוגים במערכת.";

    public final int LOGIN_FAILED_NO_EXISTS = 600;
    public final String LOGIN_FAILED_NO_EXISTS_MSG = "משתמש לא קיים";

    public final int LOGIN_FAILED_WRONG_PASSWORD = 601;
    public final String LOGIN_FAILED_WRONG_PASSWORD_MSG = "סיסמא שגויה!";

    public final int MULTI_RECORD = 602;
    public final String MULTI_RECORD_MSG = "Unique field exists more than 1!";

    public final int EMPTY_LIST = 610;
    public final String EMPTY_LIST_MSG = "לא קיימים נתונים במערכת.";

    public final int INSTITUTE_NOT_EXISTS = 610;
    public final String INSTITUTE_NOT_EXISTS_MSG = "המוסד לא קיים!";

    public final int NO_INSTITUTE_EXISTS = 610;
    public final String NO_INSTITUTE_EXISTS_MSG = "המוסד לא קיים!";

    public final int NO_PERMISSIONS = 998;
    public final String NO_PERMISSIONS_MSG = "אין הרשאה.";

    public final int INVALID_TOKEN = 999;
    public final String INVALID_TOKEN_MSG = "אירעה שגיאה, אנא התחבר מחדש.";

    public final int UPLOAD_FAILED = 505;
    public final String UPLOAD_FAILED_MSG = "העלאה נכשלה.";

    //PERMISSIONS
    public final int ADMIN_PERMISSION = 1;
    public final int ADMIN_INSTITUTE_PERMISSION = 2;
    public final int ADMIN_DEPARTMENT_PERMISSION = 3;
    public final int GUEST_PERMISSION = 4;

    //CANDIDATE STATE
    public final int CANDIDATE_ALREADY_SET_SCHEDULE = 1;
    public final int CANDIDATE_WILL_SET_SCHEDULE_LATER = 2;
    public final int CANDIDATE_IS_UNDECIDED = 3;
    public final int CANDIDATE_ASK_ANOTHER_COURSE = 4;
    public final int CANDIDATE_ALREADY_SET_IN_ANOTHER_COURSE = 5;
    public final int CANDIDATE_CANCEL_REGISTRATION = 6;
    public final int CANDIDATE_NO_PHONE_CONTACT = 7;




}
