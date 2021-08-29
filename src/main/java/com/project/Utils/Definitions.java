package com.project.Utils;

public class Definitions {
    public static final int MISSING_FIELDS = 500;
    public static final String MISSING_FIELDS_MSG = "ישנם פרטים חסרים בטופס!";

    public static final int INVALID_EMAIL = 500;
    public static final String INVALID_EMAIL_MSG = "אימל לא תקין!";

    public static final int EMAIL_EXISTS = 500;
    public static final String EMAIL_EXISTS_MSG = "האימל קיים כבר במערכת";

    public static final int USER_NOT_FOUND = 501;
    public static final String USER_NOT_FOUND_MSG = "משתמש לא קיים";

    public static final int INSTITUTE_NOT_FOUND = 501;
    public static final String INSTITUTE_NOT_FOUND_MSG = "המוסד לא קיים במערכת.";

    public static final int DEPARTMENT_NOT_FOUND = 501;
    public static final String DEPARTMENT_NOT_FOUND_MSG = "החוג לא קיים במערכת.";

    public static final int DEPARTMENTS_NOT_FOUND = 501;
    public static final String DEPARTMENTS_NOT_FOUND_MSG = "לא קיימים חוגים במערכת.";

    public static final int LOGIN_FAILED_NO_EXISTS = 600;
    public static final String LOGIN_FAILED_NO_EXISTS_MSG = "משתמש לא קיים";

    public static final int LOGIN_FAILED_WRONG_PASSWORD = 601;
    public static final String LOGIN_FAILED_WRONG_PASSWORD_MSG = "סיסמא שגויה!";

    public static final int MULTI_RECORD = 602;
    public static final String MULTI_RECORD_MSG = "Unique field exists more than 1!";

    public static final int EMPTY_LIST = 610;
    public static final String EMPTY_LIST_MSG = "לא קיימים נתונים במערכת.";

    public static final int INSTITUTE_NOT_EXISTS = 610;
    public static final String INSTITUTE_NOT_EXISTS_MSG = "המוסד לא קיים!";

    public static final int NO_INSTITUTE_EXISTS = 610;
    public static final String NO_INSTITUTE_EXISTS_MSG = "המוסד לא קיים!";

    public static final int NO_PERMISSIONS = 998;
    public static final String NO_PERMISSIONS_MSG = "אין הרשאה.";

    public static final int INVALID_TOKEN = 999;
    public static final String INVALID_TOKEN_MSG = "אירעה שגיאה, אנא התחבר מחדש.";


    public static final int ADMIN_PERMISSION = 1;
    public static final int ADMIN_INSTITUTE_PERMISSION = 2;
    public static final int ADMIN_DEPARTMENT_PERMISSION = 3;
    public static final int GUEST_PERMISSION = 4;

}
