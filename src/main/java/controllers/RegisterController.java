package controllers;

import enums.Hover;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import modules.email.EmailUtils;
import modules.otp.OTPUtils;
import styles.UIHovers;
import views.OTPVerificationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import views.RegisterView;
import views.UIPrompts;

@Slf4j
public class RegisterController implements ActionListener, MouseListener {

    private final RegisterView registerView;
    private final List<JTextField> inputFieldList;
    private final List<JButton> buttonList;
    private OTPVerificationView otpVerificationView;
    private UIHovers<RegisterView> uiHovers;

    public RegisterController(RegisterView registerView) {
        super();
        this.registerView = registerView;
        this.inputFieldList = Arrays.asList(
            registerView.getJTextField_Right_Middle_Email(),
            registerView.getJTextField_Right_Middle_FirstName(),
            registerView.getJTextField_Right_Middle_LastName(),
            registerView.getJPasswordField_Right_Middle_Password(),
            registerView.getJPasswordField_Right_Middle_Confirm_Password());
        this.buttonList = Arrays.asList(
            registerView.getJButton_Right_Bottom_Submit(),
            registerView.getJButton_Right_Bottom_Others());
        this.uiHovers = new UIHovers<>(registerView);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!registerView.isEmpty()) {
            if (registerView.isMatchingPattern()) {
                if (registerView.isMatchingPasswordAndConfirmPassword()) {
                    if (!registerView.isDuplicateEmail()) {
                        EmailUtils handleEmail = new EmailUtils();
                        String email = registerView.getDataWhenRegister().getEmail();
                        String otp = OTPUtils.generateOTP();
                        handleEmail.sendEmail(
                            handleEmail.subjectGreeting(registerView.getDataWhenRegister().getFirstName()),
                            handleEmail.emailSendOtp(registerView.getDataWhenRegister().getFirstName(),
                                otp), email);

                        OTPUtils.IS_NOTIFY_VERIFY_ACCOUNT();
                        otpVerificationView = new OTPVerificationView(otp,
                            new OTPVerificationListener() {
                                @Override
                                public void onOtpVerified() {
                                    registerView.insertMail();
                                    UIPrompts.IS_REGISTER_SUCCESS();
                                    registerView.setEnabled(true);
                                    log.info("User {} registered successfully", email);
                                }

                                @Override
                                public void onResendOtp() {
                                    // Handle resending OTP
                                    String newOtp = OTPUtils.generateOTP();
                                    handleEmail.sendEmail(handleEmail.subjectGreeting(
                                            registerView.getDataWhenRegister().getFirstName()),
                                        handleEmail.emailSendOtp(
                                            registerView.getDataWhenRegister().getFirstName(), newOtp),
                                        email);
                                    otpVerificationView.setGeneratedOtp(newOtp);
                                    log.info("Resend OTP to email {}", email);
                                }

                                @Override
                                public void onBlockUser() {
                                    handleEmail.sendEmail(handleEmail.subjectGreeting(
                                            registerView.getDataWhenRegister().getFirstName()),
                                        handleEmail.emailSendBlockAccount(
                                            registerView.getDataWhenRegister().getFirstName(),
                                            "Too many request, maybe abuse action, we added you to application blacklist"),
                                        email);
                                    log.warn(
                                        "Blocked user with email {}, too many request register in time",
                                        email);
                                }
                            });
                        otpVerificationView.setVisible(true);
                        registerView.setEnabled(false);
                    } else {
                        UIPrompts.IS_EXISTED_EMAIL();
                        log.error("Email already exists, please try again");
                    }
                } else {
                    UIPrompts.IS_WRONG_USERNAME_OR_PASSWORD();
                    log.error("Password and confirm password do not match, please try again");
                }
            }
        } else {
            UIPrompts.IS_EMPTY_FIELD();
            log.error("Empty field when register, please try again");
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }


    @Override
    public void mouseEntered(MouseEvent e) {
        inputFieldList.stream()
            .filter(inputField -> e.getSource() == inputField)
            .forEach(inputField -> {
                if (!registerView.getStatusToggle()) {
                    if (inputField == registerView.getJTextField_Right_Middle_Email()) {
                        uiHovers.setHoverEmail(Hover.ENABLE.isStatus(), "light");
                    }
                    if (inputField == registerView.getJTextField_Right_Middle_FirstName()) {
                        uiHovers.setHoverFirstName(Hover.ENABLE.isStatus(), "light");
                    }
                    if (inputField == registerView.getJTextField_Right_Middle_LastName()) {
                        uiHovers.setHoverLastName(Hover.ENABLE.isStatus(), "light");
                    }
                    if (inputField == registerView.getJPasswordField_Right_Middle_Password()) {
                        uiHovers.setHoverPassword(Hover.ENABLE.isStatus(), "light");
                    }
                    if (inputField
                        == registerView.getJPasswordField_Right_Middle_Confirm_Password()) {
                        uiHovers.setHoverConfirmPassword(Hover.ENABLE.isStatus(), "light");
                    }
                } else {
                    if(inputField == registerView.getJTextField_Right_Middle_Email()){
                        uiHovers.setHoverEmail(Hover.ENABLE.isStatus(), "dark");
                    }
                    if(inputField == registerView.getJTextField_Right_Middle_FirstName()){
                        uiHovers.setHoverFirstName(Hover.ENABLE.isStatus(),"dark");
                    }
                    if(inputField == registerView.getJTextField_Right_Middle_LastName()){
                        uiHovers.setHoverLastName(Hover.ENABLE.isStatus(), "dark");
                    }
                    if(inputField == registerView.getJPasswordField_Right_Middle_Password()){
                        uiHovers.setHoverPassword(Hover.ENABLE.isStatus(), "dark");
                    }
                    if(inputField == registerView.getJPasswordField_Right_Middle_Confirm_Password()){
                        uiHovers.setHoverConfirmPassword(Hover.ENABLE.isStatus(), "dark");
                    }
                }
            });

        buttonList.stream()
            .filter(button -> e.getSource() == button)
            .forEach(button -> {
                if (button.getText().equals("Submit")) {
                    if (!registerView.getStatusToggle()) {
                        uiHovers.setHoverButton(Hover.ENABLE.isStatus(), "light");
                    } else {
                        uiHovers.setHoverButton(Hover.ENABLE.isStatus(), "dark");
                    }
                } else {
                    uiHovers.setHoverOther(Hover.ENABLE.isStatus());
                }
            });
    }

    @Override
    public void mouseExited(MouseEvent e) {
        inputFieldList.stream()
            .filter(inputField -> e.getSource() == inputField)
            .forEach(inputField -> {
                if (!registerView.getStatusToggle()) {
                    uiHovers.setHoverEmail(Hover.DISABLE.isStatus(), "light");
                    uiHovers.setHoverFirstName(Hover.DISABLE.isStatus(), "light");
                    uiHovers.setHoverLastName(Hover.DISABLE.isStatus(), "light");
                    uiHovers.setHoverPassword(Hover.DISABLE.isStatus(), "light");
                    uiHovers.setHoverConfirmPassword(Hover.DISABLE.isStatus(), "light");
                } else {
                    uiHovers.setHoverEmail(Hover.DISABLE.isStatus(), "dark");
                    uiHovers.setHoverFirstName(Hover.DISABLE.isStatus(), "dark");
                    uiHovers.setHoverLastName(Hover.DISABLE.isStatus(), "dark");
                    uiHovers.setHoverPassword(Hover.DISABLE.isStatus(), "dark");
                    uiHovers.setHoverConfirmPassword(Hover.DISABLE.isStatus(), "dark");

                }
            });

        buttonList.stream()
            .filter(button -> e.getSource() == button)
            .forEach(button -> {
                if (button.getText().equals("Submit")) {
                    if (!registerView.getStatusToggle()) {
                        uiHovers.setHoverButton(Hover.DISABLE.isStatus(), "light");
                    } else {
                        uiHovers.setHoverButton(Hover.DISABLE.isStatus(), "dark");
                    }
                } else {
                    uiHovers.setHoverOther(Hover.DISABLE.isStatus());
                }
            });
    }
}
