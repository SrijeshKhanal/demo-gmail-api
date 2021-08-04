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


object GmailIntegration extends App {

  private val APPLICATION_NAME = "Gmail Api Integration"
  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val USER_ID = "me"
  private val CLIENT_ID = "46082748080-1hrlvsgmhgi5f1q35or63fc0h2elhdge.apps.googleusercontent.com"

  //private val SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS)
  private val SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS)
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


  //Read Gmail Inbox
    //val messageResponse = service.users.messages().list(user).execute

    //val messageId = messageResponse.getMessages.get(0).getId

    //val messages = service.users().messages().get(user, messageId).execute

    //println("Email Message => " + messages.getPayload.getParts.get(0).getBody.getData)



  /*@throws[IOException]
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


  val hm = getGmailData("subject:new link")
  println(hm.get("subject"))
  println("Message Body Data ===> " + hm.getPayload.getBody.getData)
*/
}
