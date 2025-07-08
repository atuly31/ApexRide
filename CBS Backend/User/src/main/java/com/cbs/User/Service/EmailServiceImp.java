//package com.cbs.User.Service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper; // For HTML emails
//import org.springframework.stereotype.Service;
//import org.thymeleaf.context.Context; // For Thymeleaf
//import org.thymeleaf.spring6.SpringTemplateEngine; // For Thymeleaf
//
//import jakarta.mail.MessagingException; // Import for MimeMessageHelper
//import jakarta.mail.internet.MimeMessage; // Import for MimeMessage
//
//@Service
//public class EmailServiceImp implements IEmailService { // This line is now valid
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private SpringTemplateEngine templateEngine; // Inject Thymeleaf template engine
//
//    @Override
//    public void sendMail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("apexdrive31@gmail.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//        System.out.println("Plain text email sent successfully to: " + to);
//    }
//
//    @Override // Add @Override here as it's now implementing the interface method
//    public void sendHtmlMail(String to, String subject, String templateName, java.util.Map<String, Object> templateVariables) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
//
//            Context context = new Context();
//            context.setVariables(templateVariables);
//
//            String htmlContent = templateEngine.process(templateName, context);
//
//            helper.setFrom("apexdrive31@gmail.com");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(mimeMessage);
//            System.out.println("HTML email sent successfully to: " + to + " using template: " + templateName);
//        } catch (MessagingException e) {
//            System.err.println("Error sending HTML email: " + e.getMessage());
//            // Handle exception appropriately
//        }
//    }
//}