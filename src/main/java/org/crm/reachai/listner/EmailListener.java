package org.crm.reachai.listner;
import org.crm.reachai.config.RabbitMQConfig;
import org.crm.reachai.dto.EmailEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void sendVerificationEmail(EmailEvent emailEvent) {
        System.out.println("Received email event for: " + emailEvent.getTo());

        // Create and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailEvent.getTo());
        message.setSubject(emailEvent.getSubject());
        message.setText(emailEvent.getBody());

        try {
            mailSender.send(message);
            System.out.println("otp for email Verification sent to: " + emailEvent.getTo());
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
