package dataProviders;

import org.testng.Assert;
import org.testng.annotations.DataProvider;

public class LoginDataProvider {
    @DataProvider
    public Object [][] validLoginData(){
        Object[][] loginData = {{"standard_user","secret_sauce"}};
        return loginData;
    }
    @DataProvider
    public Object [][] invalidPasswordData(){
        Object[][] loginData = {{"standard_user","secret_user"}};
        return loginData;
    }
    @DataProvider
    public Object [][] emptyFieldsData(){
        Object[][] loginData = {{"",""}};
        return loginData;
    }
    @DataProvider
    public Object [][] lockedUserData(){
        Object[][] loginData = {{"locked_out_user","secret_sauce"}};
        return loginData;
    }
    @DataProvider
    public Object [][] problemUserData(){
        Object[][] loginData = {{"problem_user","secret_sauce"}};
        return loginData;
    }
    @DataProvider
    public Object[][] errorUserData(){
        Object[][] loginData = {{"error_user","secret_sauce"}};
        return loginData;
    }

    @DataProvider
    public Object[][] performanceGlitchUserData(){
        Object[][] loginData = {{"performance_glitch_user","secret_sauce"}};
        return loginData;
    }

    @DataProvider
    public Object[][] visualUserData(){
        Object[][] loginData = {{"visual_user","secret_sauce"}};
        return loginData;
    }

}
