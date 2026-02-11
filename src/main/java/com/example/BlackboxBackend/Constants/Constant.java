package com.example.BlackboxBackend.Constants;

import com.example.BlackboxBackend.DTO.RoleEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Constant {

    static public Map<String, String> screens;
    static public Map<String, List<String>> permissionScreen;

    Constant(){
        screens = new HashMap<>();
        permissionScreen = new HashMap<>();
        loadScreens();
        loadPermissionScreens();
    }


    private void loadScreens(){
        screens.put("MAIN_ANALYTIC_DASHBOARD", "/main-analytic-dashboard");
        screens.put("DEPARTMENT_ANALYTIC_DASHBOARD", "/department-analytic-dashboard");
        screens.put("ISSUES", "/issue");
        screens.put("STAFF", "/staff");
        screens.put("DEPARTMENT", "/department");
    }

    private void loadPermissionScreens(){
        List<String> superAdminScreens = new ArrayList<>(Arrays.asList(
                screens.get("MAIN_ANALYTIC_DASHBOARD"),
                screens.get("DEPARTMENT_ANALYTIC_DASHBOARD"),
                screens.get("ISSUES"),
                screens.get("STAFF"),
                screens.get("DEPARTMENT")
        ));
        List<String> adminScreens = new ArrayList<>(Arrays.asList(
                screens.get("DEPARTMENT"),
                screens.get("ISSUES"),
                screens.get("STAFF"),
                screens.get("DEPARTMENT"),
                screens.get("DEPARTMENT_ANALYTIC_DASHBOARD")
        ));

        List<String> workerScreen = new ArrayList<>(
                Arrays.asList(
                        screens.get("ISSUES")
                )
        );

        permissionScreen.put(RoleEnum.SUPER_ADMIN.toString(), superAdminScreens);
        permissionScreen.put(RoleEnum.ADMIN.toString(), adminScreens);
        permissionScreen.put(RoleEnum.WORKER.toString(), workerScreen);
    }
}
