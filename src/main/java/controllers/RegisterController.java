package controllers;

import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import modules.email.EmailUtils;
import modules.otp.OTPUtils;
import views.OTPVerificationView;
import views.RegisterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RegisterController implements ActionListener, MouseListener {

    private RegisterView registerView;
    private List<JTextField> inputFieldList;
    private List<JButton> buttonList;
    private OTPVerificationView otpVerificationView;

    public RegisterController(RegisterView registerView) {
        super();
        this.registerView = registerView;
        this.inputFieldList = Arrays.asList(
            registerView.jTextField_Right_Middle_Email,
            registerView.jTextField_Right_Middle_FirstName,
            registerView.jTextField_Right_Middle_LastName,
            registerView.jPasswordField_Right_Middle_Password,
            registerView.jPasswordField_Right_Middle_Confirm_Password);
        this.buttonList = Arrays.asList(
            registerView.jButton_Right_Bottom_Submit,
            registerView.jButton_Right_Bottom_Others);
    }

    // Su dung ArrayList<Account> de luu tru account
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!registerView.isEmpty()) {
            if (registerView.isMatchingPattern()) {
                if (registerView.isMatchingPasswordAndConfirmPassword()) {
                    if (!registerView.isDuplicateEmail()) {
                        EmailUtils handleEmail = new EmailUtils();
                        String email = registerView.getRegister().email();
                        String otp = OTPUtils.generateOTP();
                        handleEmail.sendEmail(handleEmail.subjectGreeting(registerView.getRegister().firstName()),
                            handleEmail.emailSendOtp(registerView.getRegister().firstName(), otp), email);

                        OTPUtils.IS_NOTIFY_VERIFY_ACCOUNT();
                        otpVerificationView = new OTPVerificationView(otp, new OTPVerificationView.OTPVerificationListener() {
                            @Override
                            public void onOtpVerified() {
                                registerView.insertMail();
                                registerView.handleSuccess();
                                registerView.setEnabled(true);
                            }

                            @Override
                            public void onResendOtp() {
                                // Handle resending OTP
                                String newOtp = OTPUtils.generateOTP();
                                handleEmail.sendEmail(handleEmail.subjectGreeting(registerView.getRegister().firstName()),
                                    handleEmail.emailSendOtp(registerView.getRegister().firstName(), newOtp), email);
                                otpVerificationView.setGeneratedOtp(newOtp);
                            }

                            @Override
                            public void onBlockUser() {
                                handleEmail.sendEmail(handleEmail.subjectGreeting(registerView.getRegister().firstName()),
                                    handleEmail.emailSendBlockAccount(registerView.getRegister().firstName(), "Too many request, maybe abuse action, we added you to application blacklist"), email);
                                System.exit(0);
                            }
                        });
                        otpVerificationView.setVisible(true);
                        registerView.setEnabled(false);
                    } else {
                        registerView.handleDuplicateEmail();
                    }
                } else {
                    registerView.handleNotMatchingPasswordAndConfirmPassword();
                }
            }
        } else {
            registerView.handleEmpty();
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
                    registerView.setHoverEmail(true, "light");
                    registerView.setHoverFirstName(true, "light");
                    registerView.setHoverLastName(true, "light");
                    registerView.setHoverPassword(true, "light");
                    registerView.setHoverConfirmPassword(true, "light");
                } else {
                    registerView.setHoverEmail(true, "dark");
                    registerView.setHoverFirstName(true, "dark");
                    registerView.setHoverLastName(true, "dark");
                    registerView.setHoverPassword(true, "dark");
                    registerView.setHoverConfirmPassword(true, "dark");
                }
            });

        buttonList.stream()
            .filter(button -> e.getSource() == button)
            .forEach(button -> {
                if (button.getText().equals("Submit")) {
                    if (!registerView.getStatusToggle()) {
                        registerView.setHoverButton(true, "light");
                    } else {
                        registerView.setHoverButton(true, "dark");
                    }
                } else {
                    registerView.setHoverOther(true);
                }
            });
    }

    @Override
    public void mouseExited(MouseEvent e) {
        inputFieldList.stream()
            .filter(inputField -> e.getSource() == inputField)
            .forEach(inputField -> {
                if (!registerView.getStatusToggle()) {
                    registerView.setHoverEmail(false, "light");
                    registerView.setHoverFirstName(false, "light");
                    registerView.setHoverLastName(false, "light");
                    registerView.setHoverPassword(false, "light");
                    registerView.setHoverConfirmPassword(false, "light");
                } else {
                    registerView.setHoverEmail(false, "dark");
                    registerView.setHoverFirstName(false, "dark");
                    registerView.setHoverLastName(false, "dark");
                    registerView.setHoverPassword(false, "dark");
                    registerView.setHoverConfirmPassword(false, "dark");

                }
            });

        buttonList.stream()
            .filter(button -> e.getSource() == button)
            .forEach(button -> {
                if (button.getText().equals("Submit")) {
                    if (!registerView.getStatusToggle()) {
                        registerView.setHoverButton(false, "light");
                    } else {
                        registerView.setHoverButton(false, "dark");
                    }
                } else {
                    registerView.setHoverOther(false);
                }
            });
    }
}
