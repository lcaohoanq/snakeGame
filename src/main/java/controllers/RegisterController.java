package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import constants.Messages;
import constants.Paths;
import constants.Regex;
import models.Account;
import models.LoginData;
import models.RegisterData;
import services.DBServices;
import utils.DataHandler;
import utils.PasswordHandler;
import views.MyFrame;

public class RegisterController extends FrameController implements ActionListener, RegisterData {

    private String username;
    private String password;
    private String confirmPassword;
    private PasswordHandler passwordHandler;

    public RegisterController() {
        super();
        this.passwordHandler = new PasswordHandler();
    }

    // Su dung ArrayList<Account> de luu tru account
    @Override
    public void actionPerformed(ActionEvent e) {
        username = MyFrame.jTextField_Right_Middle_Username.getText();
        password = String.valueOf(MyFrame.jPasswordField_Right_Middle_Password.getPassword());
        confirmPassword = String
                .valueOf(MyFrame.jPasswordField_Right_Middle_Confirm_Password.getPassword());
        if(!username.matches(Regex.USERNAME)){
            Messages.IS_WRONG_FORMAT_USERNAME();
            return;
        } else if(!password.matches(Regex.PASSWORD)){
            Messages.IS_WRONG_FORMAT_PASSWORD();
            return;
        } else if(!confirmPassword.matches(Regex.PASSWORD)){
            Messages.IS_WRONG_FORMAT_PASSWORD();
            return;
        }

        if ((isEmpty(username, password, confirmPassword))) {
            handleEmpty();
        } else if (!isMatching(password, confirmPassword)) {
            handleWrong();
        } else {
            if (isDuplicateUsername(username)) {
                handleDuplicateUsername();
            } else {
                handleSuccess();
            }
        }

    }

    @Override
    public void handleEmpty() {
        Messages.IS_EMPTY_FIELD();
        System.out.println("Register failed: " + "username:" + username + " password:" + password);
    }

    @Override
    public void handleWrong() {
        Messages.IS_NOT_MATCH_PASSWORD_AND_CONFIRM_PASSWORD();
        System.out.println("Register failed: " + "username:" + username + " password:" + password);
    }

    @Override
    public void handleDuplicateUsername() {
        Messages.IS_EXISTED_USERNAME();
        System.out.println("Register failed: " + "username:" + username);
    }

    @Override
    public void handleSuccess() {
        password = passwordHandler.hash(password); // replace password with the hashed
        System.out.println("Register success: " + "username:" + username + " password:" + password);
        DBServices.insert(username, password, 0);
        DataHandler.accountList.add(new Account(username, password));
        dataHandler.writeFile(Paths.URL_ACCOUNT);
        Messages.IS_REGISTER_SUCCESS();
    }

    @Override
    public boolean isEmpty(String username, String password) {
        return false;
    }

    @Override
    public boolean isMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


    @Override
    public boolean isEmpty(String username, String password, String confirmPassword) {
        return username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty();
    }

    @Override
    public boolean isDuplicateUsername(String username) {
        for (Account item : DataHandler.accountList) {
            if (item.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


}