package com.example.healthcareproject.domain.usecase.alert

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.AccessToken
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendEmailUseCase {

    /**
     * Constants for the Gmail API.
     */
    companion object {
        private const val APPLICATION_NAME = "Healthcare Project"
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    }

    /**
     * Creates a Gmail service instance using the provided access token.
     *
     * @param accessToken The OAuth 2.0 access token.
     * @return A Gmail service instance.
     */
    private fun getGmailService(accessToken: String): Gmail {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val token = AccessToken(accessToken, null)
        val credentials = GoogleCredentials.create(token)
        val requestInitializer = HttpCredentialsAdapter(credentials)

        return Gmail.Builder(httpTransport, JSON_FACTORY, requestInitializer)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    /**
     * Sends an email using the Gmail API.
     *
     * @param userId The user's email address or "me" to indicate the authenticated user.
     * @param from The sender's email address.
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param bodyText The body text of the email.
     * @param accessToken The OAuth 2.0 access token for authentication.
     */
    fun sendEmail(userId: String, from: String, to: String, subject: String, bodyText: String, accessToken: String) {
        val service = getGmailService(accessToken)
        val email = createEmail(to, from, subject, bodyText)
        sendMessage(service, userId, email)
    }

    /**
     * Creates a MimeMessage object for the email.
     *
     * @param to The recipient's email address.
     * @param from The sender's email address.
     * @param subject The subject of the email.
     * @param bodyText The body text of the email.
     * @return A MimeMessage object representing the email.
     */
    private fun createEmail(to: String, from: String, subject: String, bodyText: String): MimeMessage {
        val props = System.getProperties()
        val session = Session.getDefaultInstance(props, null)

        return MimeMessage(session).apply {
            setFrom(InternetAddress(from))
            addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
            this.subject = subject
            setText(bodyText)
        }
    }

    /**
     * Sends the email message using the Gmail API.
     *
     * @param service The Gmail service instance.
     * @param userId The user's email address or "me" to indicate the authenticated user.
     * @param email The MimeMessage object representing the email.
     */
    private fun sendMessage(service: Gmail, userId: String, email: MimeMessage) {
        val buffer = ByteArrayOutputStream()
        email.writeTo(buffer)
        val encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray())
        val message = Message().apply { raw = encodedEmail }
        service.users().messages().send(userId, message).execute()
    }
}