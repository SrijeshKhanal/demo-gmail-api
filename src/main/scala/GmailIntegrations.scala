import java.io.{File, FileNotFoundException, IOException, InputStreamReader}
import java.nio.file.Paths
import java.util.Collections

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.gmail.{Gmail, GmailScopes}
import java.util.Collections

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.util.store.FileDataStoreFactory
import java.io.IOException
import java.util
import java.util.Base64

import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.Gmail
import javax.mail.internet.MimeMessage
import java.io.IOException

import com.google.api.client.util.StringUtils
import javax.mail.MessagingException
import com.google.api.services.gmail.model.Draft


object GmailIntegrations extends App {

  private val APPLICATION_NAME = "Gmail Email Service"
  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val USER_ID = "me"
  private val CLIENT_ID = "287847834300-8avkck7scuc7b0jkp7pcs85usdrbil0o.apps.googleusercontent.com"
  val CLIENT_SECRET = "jjGAh9z9H1GN345tw0VxmJFr"

  //private val SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS)
  private val SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM)
  //private val CREDENTIALS_FILE_PATH = "/src/main/resources/credentials/credentials.json"

  private val currentPath = Paths.get(System.getProperty("user.dir"))

//  private val CREDENTIALS_FILE_PATH = Paths.get(currentPath.toString, "src", "main", "resources",
//    "credentials", "credentials.json").toString

  private val CREDENTIALS_FILE_PATH = "/credentials/credentials.json"

  private val TOKENS_DIRECTORY_PATH = "tokens"

/*
  private val tokenDirectoryPath = Paths.get(System.getProperty("user.dir"))

  private val TOKENS_DIRECTORY_PATH = Paths.get(tokenDirectoryPath.toString, "src", "main", "resources",
    "credentials").toString
*/



  /**
    * Creates an authorized Credential object.
    *
    * @param HTTP_TRANSPORT The network HTTP Transport.
    * @return An authorized Credential object.
    * @throws IOException If the credentials.json file cannot be found.
    *
    **/

  @throws[IOException]
  private def getCredentials(HTTP_TRANSPORT: NetHttpTransport) = {
    // Load client secrets.

    val in = classOf[NetHttpTransport].getResourceAsStream(CREDENTIALS_FILE_PATH)
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
    }
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))
    // Build flow and trigger user authorization request.

    val flow: GoogleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
      clientSecrets, SCOPES)
      .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
      .setAccessType("offline")
      .build


    val receiver = new LocalServerReceiver.Builder().setPort(8888).build
    //val aaUrl =  flow.newAuthorizationUrl().setRedirectUri(receiver.getRedirectUri)


    //aaUrl backend le return garnu paryo , UI le API call garxa ani backend le return garne
    //getAuthUrl(redirectionUrl)
    //tyo redirention url le link return garne backend

    //store accecc token garxa UI le vaadin bata (backend le return gareko url)
    //backen le store garen



    new AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
  }



  // Build a new authorized API client service.

  val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport
  val service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    .setApplicationName(APPLICATION_NAME).build

  // Print the labels in the user's account.
  val user = "me"
  val listResponse = service.users.labels().list(user).execute
  val labels = listResponse.getLabels.toArray()

//  val messageResponse = service.users.messages().list(user).execute
//
//  val messageId = messageResponse.getMessages.get(0).getId
//
//  val messages = service.users().messages().get(user, messageId).execute
//
//  println("Email Message => " + messages.getPayload.getParts.get(0).getBody.getData)

  if (labels.isEmpty) println("No labels found.")
  else {
    println("Labels:")
    for (label <- labels) {
      printf("- %s\n", label)
    }
  }

  println("--------Read Email---------")
  println(getCredentials(HTTP_TRANSPORT).getAccessToken)

  println("Expires " + getCredentials(HTTP_TRANSPORT).getExpiresInSeconds)

  println("Refresh Token " + getCredentials(HTTP_TRANSPORT).getRefreshToken)

  //Read Gmail Inbox
    //val messageResponse = service.users.messages().list(user).execute

    //val messageId = messageResponse.getMessages.get(0).getId

    //val messages = service.users().messages().get(user, messageId).execute

    //println("Email Message => " + messages.getPayload.getParts.get(0).getBody.getData)

  println(service.users().getProfile(USER_ID).execute().getEmailAddress)
  val aaa = service.users().messages().list(USER_ID).setLabelIds(Collections.singletonList("INBOX"))
  var res = aaa.execute()

  println(">>>>>>>>>>>>>." + service.users().messages().get("me", "17cd5d4cdebf9ad3").execute())
  println(">>>>>>>>>>>>>." + StringUtils.newStringUtf8(Base64.getDecoder.decode(service.users().messages().get("me", "17cd5d4cdebf9ad3").execute().getPayload.getParts.get(1).getBody.getData)))
  res.getMessages.forEach(m => println(m))
  println("Email ================================ > " + res.getMessages())


/*

  @throws[IOException]
  def listMessagesMatchingQuery(service: Gmail, userId: String, query: String) = {
    var response = service.users.messages.list(userId).setQ(query).execute
    val messages = new util.ArrayList[Message]
    while ( {
      response.getMessages != null
    }) {
      messages.addAll(response.getMessages)
      if (response.getNextPageToken != null) {
        val pageToken = response.getNextPageToken
        response = service.users.messages.list(userId).setQ(query).setPageToken(pageToken).execute
      }
    }
    messages
  }

  @throws[IOException]
  def getMessage(service: Gmail, userId: String, messages: util.ArrayList[Message], index: Int) = {
    val message = service.users.messages.get(userId, messages.get(index).getId).execute
    message
  }

  def getGmailData(query: String) = try {
    val service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
      .setApplicationName(APPLICATION_NAME).build
    val messages = listMessagesMatchingQuery(service, USER_ID, query)
    val message = getMessage(service, USER_ID, messages, 0)

    message

  } catch {
    case e: Exception =>
      System.out.println("email not found....")
      throw new RuntimeException(e)
  }


  val hm = getGmailData("subject:Test Project")
  println(hm.get("subject"))
  println("Message Body Data ===> " + hm.getPayload.getBody.getData)
*/

  val mailService = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    .setApplicationName(APPLICATION_NAME).build

  /**
    * Send an email from the user's mailbox to its recipient.
    *
    * @param service      Authorized Gmail API instance.
    * @param userId       User's email address. The special value "me"
    *                     can be used to indicate the authenticated user.
    * @param emailContent Email to be sent.
    * @return The sent message
    * @throws MessagingException
    * @throws IOException
    */
  @throws[MessagingException]
  @throws[IOException]
  def sendMessage(service: Gmail, userId: String, emailContent: MimeMessage):Message = {
    var message = CreateMessage.createMessageWithEmail(emailContent)
    message = service.users.messages.send(userId, message).execute
    println("Message id: " + message.getId)
    println(message.toPrettyString)
    message
  }

  val mailContent = CreateMessage.createEmail("khanalsir.srijesh@gmail.com",
  "test.sk@gmail.com", "Email From API Client", "Hello, Milan. Nice to meet you. Have a good day."
  )


  sendMessage(mailService, USER_ID, mailContent)


  println("---------- Draft Message ----------")

  /**
    * Create draft email.
    *
    * @param service      an authorized Gmail API instance
    * @param userId       user's email address. The special value "me"
    *                     can be used to indicate the authenticated user
    * @param emailContent the MimeMessage used as email within the draft
    * @return the created draft
    * @throws MessagingException
    * @throws IOException
    */
  @throws[MessagingException]
  @throws[IOException]
  def createDraft(service: Gmail, userId: String, emailContent: MimeMessage): Draft = {
    val message = CreateMessage.createMessageWithEmail(emailContent)
    var draft = new Draft
    draft.setMessage(message)
    draft = service.users.drafts.create(userId, draft).execute
    System.out.println("Draft id: " + draft.getId)
    System.out.println(draft.toPrettyString)
    draft
  }

  val mailContentForDraft = CreateMessage.createEmail("khanalsir.srijesh@gmail.com",
    "test.sk@gmail.com", "Testing Mail From API Client", "Hello, Srijesh. Nice to meet you. Have a good day."
  )


 val draftCreated = createDraft(mailService, USER_ID, mailContentForDraft)

  println("*************** Sending Draft Mail ***************")
  //println(draftCreated)

  //val draftId = draftCreated.getId

  val aa = mailService.users().drafts.get("", "")
  val bbbb = mailService.users().drafts.send(USER_ID, draftCreated)


  println("*************** Draft Mail Sent ***************")




}
