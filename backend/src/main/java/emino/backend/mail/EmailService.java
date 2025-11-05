package emino.backend.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.antlr.v4.runtime.misc.Utils.readFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import emino.backend.models.Employee;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("rekomendacjemasazy@gmail.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }

    private String readTemplate(String path) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);

            if (inputStream == null) {
                throw new RuntimeException("Template not found: " + path);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read template: " + path, e);
        }
    }

    public void sendHtmlPasswordEmail(Employee to, String password, String companyName) throws MessagingException {
        
        String htmlTemplate = readTemplate("templates/passwordTemplate.html");

        MimeMessage message = emailSender.createMimeMessage();

        message.setFrom("rekomendacjemasazy@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, to.getEmail());
        message.setSubject("Potwierdzenie utworzenia konta");

        String htmlContent = htmlTemplate
            .replace("${name}", to.getName())
            .replace("${company}", companyName)
            .replace("${email}", to.getEmail())
            .replace("${password}", password);

        message.setContent(htmlContent, "text/html; charset=utf-8");

        emailSender.send(message);
    }
}