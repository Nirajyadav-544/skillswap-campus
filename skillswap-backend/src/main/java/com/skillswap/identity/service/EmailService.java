
package com.skillswap.identity.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    public EmailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ============================================================
    // OTP EMAIL
    // ============================================================

    public void sendOtp(String email, String otp) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(
                    from,
                    fromName
            );

            helper.setTo(email);

            helper.setSubject(
                    "SkillSwap Campus - Your Verification Code"
            );

            Context context =
                    new Context();

            context.setVariable(
                    "otp",
                    otp
            );

            context.setVariable(
                    "expiryMinutes",
                    otpExpiryMinutes
            );

            String htmlContent =
                    templateEngine.process(
                            "email/otp-verification",
                            context
                    );

            helper.setText(
                    htmlContent,
                    true
            );

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {

            throw new IllegalStateException(
                    "Unable to send verification email",
                    e
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to send verification email",
                    e
            );
        }
    }

    // ============================================================
    // PASSWORD RESET EMAIL
    // ============================================================

    public void sendPasswordResetEmail(
            String email,
            String token
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(email);

            helper.setFrom(
                    from,
                    fromName
            );

            helper.setSubject(
                    "Reset your SkillSwap Campus password"
            );

            String resetUrl =
                    "http://localhost:5173/reset-password?token="
                            + token;

            String html =
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Password Reset</title>
                    </head>

                    <body style="
                        margin:0;
                        padding:0;
                        background:#f4f6f8;
                        font-family:Arial,Helvetica,sans-serif;
                    ">

                    <div style="
                        max-width:600px;
                        margin:40px auto;
                        background:white;
                        border-radius:12px;
                        padding:40px;
                        box-shadow:0 4px 20px rgba(0,0,0,0.08);
                    ">

                        <h1 style="
                            margin-top:0;
                            color:#111827;
                        ">
                            SkillSwap Campus
                        </h1>

                        <h2>
                            Password Reset Request
                        </h2>

                        <p>
                            We received a request to reset the password
                            for your SkillSwap Campus account.
                        </p>

                        <p>
                            Click the button below to create a new password.
                        </p>

                        <div style="
                            margin:30px 0;
                            text-align:center;
                        ">

                            <a href="%s"
                               style="
                                   display:inline-block;
                                   padding:14px 28px;
                                   background:#111827;
                                   color:white;
                                   text-decoration:none;
                                   border-radius:8px;
                                   font-weight:bold;
                               ">
                                Reset Password
                            </a>

                        </div>

                        <p>
                            This password-reset link expires in
                            <strong>30 minutes</strong>.
                        </p>

                        <p>
                            If you did not request a password reset,
                            you can safely ignore this email.
                        </p>

                        <hr style="
                            border:none;
                            border-top:1px solid #e5e7eb;
                            margin:30px 0;
                        ">

                        <p style="
                            font-size:12px;
                            color:#6b7280;
                        ">
                            SkillSwap Campus security notification.
                        </p>

                    </div>

                    </body>
                    </html>
                    """
                    .formatted(resetUrl);

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {

            throw new IllegalStateException(
                    "Unable to send password reset email",
                    e
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to send password reset email",
                    e
            );
        }
    }

    // ============================================================
    // PASSWORD CHANGED SECURITY EMAIL
    // ============================================================

    public void sendPasswordChangedEmail(
            String email
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(
                    from,
                    fromName
            );

            helper.setTo(email);

            helper.setSubject(
                    "SkillSwap Campus - Password Changed"
            );

            String html =
                    """
                    <!DOCTYPE html>
                    <html>

                    <head>
                        <meta charset="UTF-8">
                        <title>Password Changed</title>
                    </head>

                    <body style="
                        margin:0;
                        padding:0;
                        background:#f4f6f8;
                        font-family:Arial,Helvetica,sans-serif;
                    ">

                    <div style="
                        max-width:600px;
                        margin:40px auto;
                        background:white;
                        border-radius:12px;
                        padding:40px;
                        box-shadow:0 4px 20px rgba(0,0,0,0.08);
                    ">

                        <h1 style="
                            margin-top:0;
                            color:#111827;
                        ">
                            SkillSwap Campus
                        </h1>

                        <h2>
                            Password Changed Successfully
                        </h2>

                        <p>
                            Your SkillSwap Campus account password
                            was successfully changed.
                        </p>

                        <p>
                            For your security, your existing login
                            sessions have been signed out.
                        </p>

                        <p>
                            If you made this change, no further action
                            is required.
                        </p>

                        <p>
                            If you did not change your password,
                            please secure your account immediately
                            by using the password reset option.
                        </p>

                        <hr style="
                            border:none;
                            border-top:1px solid #e5e7eb;
                            margin:30px 0;
                        ">

                        <p style="
                            font-size:12px;
                            color:#6b7280;
                        ">
                            SkillSwap Campus security notification.
                        </p>

                    </div>

                    </body>
                    </html>
                    """;

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {

            throw new IllegalStateException(
                    "Unable to send password changed email",
                    e
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to send password changed email",
                    e
            );
        }
    }
}

