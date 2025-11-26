package com.atp.fwfe.service.mailer;

import com.atp.fwfe.model.work.WorkPosted;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    // ---------- Gửi email chào mừng ----------
    @Async
    public void sendWelcomeEmail(String email, String name) {
        String subject = "🎉 Chào mừng bạn đến với hệ thống của chúng tôi!";
        String html = """
               <div style="font-family: Arial, sans-serif; color: #333;">
                  <h2 style="color: #2d8cf0;">👋 Xin chào %s!</h2>
                  <p>Cảm ơn bạn đã đăng ký và trở thành một phần của <strong>cộng đồng việc làm</strong> của chúng tôi! 🌟</p>
                  <hr style="margin: 20px 0;" />
                  <p>Chúc bạn một ngày tuyệt vời và nhiều thành công! 💼</p>
                  <p style="margin-top: 20px;">Trân trọng,<br/><strong>Đội ngũ Hệ thống Việc Làm</strong></p>
               </div>
               """.formatted(name);

        sendHtml(email, subject, html);
    }

    // ---------- Gửi email cảm ơn hàng tuần ----------
    @Async
    public void sendWeeklyThanks(String email, String name) {
        String subject = "Cảm ơn bạn đã luôn đồng hành cùng cộng đồng!";
        String html = """
                 <div style="font-family: Arial, sans-serif; color: #333;">
                    <p>Chúng tôi rất biết ơn sự đồng hành của bạn trong tuần vừa qua.</p>
                    <p>Hẹn gặp lại bạn vào những tuần tới với nhiều cơ hội việc làm hấp dẫn!</p>
                    <p>Chúc bạn ngày cuối tuần an yên bên gia đình và thật tận hưởng hôm nay nhé %s :)</p>
                    <p><strong>Hệ thống Việc Làm</strong></p>
                </div>
                """.formatted(name);

        sendHtml(email, subject, html);
    }

    // ---------- Gửi thông báo việc làm mới ----------
    @Async
    public void sendNewJobNotification(String email, List<WorkPosted> jobs) {
        String subject = "🆕 Việc làm mới dành cho bạn!";
        StringBuilder jobListHtml = new StringBuilder();

        for (WorkPosted job : jobs) {
            jobListHtml.append("<li>")
                    .append("<strong>").append("Vị trí tuyển dụng: ").append(job.getPosition()).append("</strong>")
                    .append(" - Mức lương cơ bản: ").append(job.getSalary()).append("₫")
                    .append(" Địa chỉ: ").append(job.getCompany().getAddress())
                    .append("</li>");
        }

        String html = """
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h3>Xin chào!</h3>
                    <p>Dưới đây là một số công việc mới bạn có thể quan tâm:</p>
                    <ul>%s</ul>
                    <p>Hãy truy cập hệ thống để biết thêm chi tiết!</p>
                </div>
                """.formatted(jobListHtml);

        sendHtml(email, subject, html);
    }

    // ---------- Gửi email HTML chung ----------
    @Async
    public void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("dokyha2004@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            logger.info("✅ Đã gửi email tới người dùng: {}", to);
        } catch (MessagingException e) {
            logger.error("❌ Gửi email thất bại tới: {}. Lỗi: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Có lỗi không xác định khi gửi email tới {}: {}", to, e.getMessage(), e);
        }
    }
}
