package com.jingshui.taoloveaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * QQ邮箱邮件发送工具类
 */
@Slf4j
public class QQEmailSenderTool {


    // ⚠️ 请替换为你的QQ邮箱
//    private static final String FROM_EMAIL = "your_email@qq.com";
//
//    // ⚠️ 请替换为你的SMTP授权码（16位字符）
//    private static final String AUTH_CODE = "your_smtp_auth_code";

    private final String fromEmail ;
    private final String authCode ;

    // QQ邮箱SMTP配置
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final String SMTP_PORT = "587"; // 推荐使用STARTTLS（端口587），也可用SSL（端口465）

    /** 简单邮箱格式校验 */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public QQEmailSenderTool(String fromEmail, String authCode) {
        this.fromEmail = fromEmail;
        this.authCode = fromEmail;
    }

    @Tool(description = "Sends a plain text email using QQ Mail's SMTP service. " +
            "Requires the recipient's email address, subject line, and plain text content. " +
            "The sender's QQ email address and SMTP authorization code must be pre-configured in the system.")
    public String sendTextEmail(
            @ToolParam(description = "Recipient's email address") String to,
            @ToolParam(description = "Email subject line") String subject,
            @ToolParam(description = "Plain text content of the email") String content) {

        String error = validate(to, subject, content);
        if (error != null) {
            return error;
        }

        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(content); // 纯文本

            Transport.send(message);
            log.info("✅ 文本邮件发送成功！收件人：{}", to);
            return "文本邮件发送成功，收件人：" + to;
        } catch (MessagingException e) {
            log.error("❌ 文本邮件发送失败，收件人：{}", to, e);
            return "文本邮件发送失败：" + e.getMessage();
        }
    }

    /**
     * 发送 HTML 格式邮件（供 AI Agent 调用）
     *
     * @param to          收件人邮箱地址
     * @param subject     邮件主题
     * @param htmlContent 邮件正文（HTML 格式，支持 &lt;b&gt;、&lt;p&gt;、&lt;a&gt; 等标签）
     * @return 执行结果描述（成功或失败原因）
     */
    @Tool(description = "Sends an HTML formatted email using QQ Mail's SMTP service. " +
            "Requires the recipient's email address, subject line, and HTML content. " +
            "The sender's QQ email address and SMTP authorization code must be pre-configured in the system.")
    public String sendHtmlEmail(
            @ToolParam(description = "Recipient's email address") String to,
            @ToolParam(description = "Email subject line") String subject,
            @ToolParam(description = "HTML content of the email") String htmlContent) {

        String error = validate(to, subject, htmlContent);
        if (error != null) {
            return error;
        }

        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html;charset=UTF-8"); // HTML

            Transport.send(message);
            log.info("✅ HTML邮件发送成功！收件人：{}", to);
            return "HTML邮件发送成功，收件人：" + to;
        } catch (MessagingException e) {
            log.error("❌ HTML邮件发送失败，收件人：{}", to, e);
            return "HTML邮件发送失败：" + e.getMessage();
        }
    }

    /**
     * 公共参数校验
     *
     * @return 校验通过返回 null，否则返回错误信息
     */
    private String validate(String to, String subject, String content) {
        if (to == null || !EMAIL_PATTERN.matcher(to).matches()) {
            log.warn("收件人邮箱格式非法：{}", to);
            return "邮件发送失败：收件人邮箱格式非法 -> " + to;
        }
        if (subject == null || subject.isBlank()) {
            return "邮件发送失败：邮件主题不能为空";
        }
        if (content == null || content.isBlank()) {
            return "邮件发送失败：邮件正文不能为空";
        }
        if (fromEmail == null || fromEmail.isBlank() || authCode == null || authCode.isBlank()) {
            log.error("QQ 邮箱配置缺失，请检查 qq.mail.from / qq.mail.auth-code");
            return "邮件发送失败：系统未配置发件邮箱或授权码";
        }
        return null;
    }

    /**
     * 创建带认证的 SMTP Session（含超时配置）
     */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        // 超时控制，避免网络异常时长时间阻塞
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, authCode);
            }
        });
    }
}
