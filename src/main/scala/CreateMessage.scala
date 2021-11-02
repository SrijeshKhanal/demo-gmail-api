import java.io.File

import com.google.api.client.util.Base64
import com.google.api.services.gmail.model.Message
import javax.mail.MessagingException
import javax.mail.internet.{MimeBodyPart, MimeMultipart}
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.io.ByteArrayOutputStream
import java.io.IOException

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.io.IOException
import java.util.Properties

object CreateMessage {

  /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to       email address of the receiver
    * @param from     email address of the sender, the mailbox account
    * @param subject  subject of the email
    * @param bodyText body text of the email
    * @return the MimeMessage to be used to send email
    * @throws MessagingException
    */
  @throws[MessagingException]
  def createEmail(to: String, from: String, subject: String, bodyText: String): MimeMessage = {
    val props = new Properties
    val session = Session.getDefaultInstance(props, null)
    val email = new MimeMessage(session)
    email.setFrom(new InternetAddress(from))
    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to))
    email.setSubject(subject)
    email.setText(bodyText)
    email
  }


  /**
    * Create a message from an email.
    *
    * @param emailContent Email to be set to raw of message
    * @return a message containing a base64url encoded email
    * @throws IOException
    * @throws MessagingException
    */
  @throws[MessagingException]
  @throws[IOException]
  def createMessageWithEmail(emailContent: MimeMessage): Message = {
    val buffer = new ByteArrayOutputStream
    emailContent.writeTo(buffer)
    val bytes = buffer.toByteArray
    val encodedEmail = Base64.encodeBase64URLSafeString(bytes)
    val message = new Message
    message.setRaw(encodedEmail)
    message
  }


  /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to       Email address of the receiver.
    * @param from     Email address of the sender, the mailbox account.
    * @param subject  Subject of the email.
    * @param bodyText Body text of the email.
    * @param file     Path to the file to be attached.
    * @return MimeMessage to be used to send email.
    * @throws MessagingException
    */
  @throws[MessagingException]
  @throws[IOException]
  def createEmailWithAttachment(to: String, from: String, subject: String, bodyText: String, file: File): MimeMessage = {
    val props = new Properties
    val session = Session.getDefaultInstance(props, null)
    val email = new MimeMessage(session)
    email.setFrom(new InternetAddress(from))
    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to))
    email.setSubject(subject)
    var mimeBodyPart = new MimeBodyPart()
    mimeBodyPart.setContent(bodyText, "text/plain")
    val multipart = new MimeMultipart()
    multipart.addBodyPart(mimeBodyPart)
    mimeBodyPart = new MimeBodyPart()
    val source = new FileDataSource(file)
    mimeBodyPart.setDataHandler(new DataHandler(source))
    mimeBodyPart.setFileName(file.getName)
    multipart.addBodyPart(mimeBodyPart)
    email.setContent(multipart)
    email
  }

}
