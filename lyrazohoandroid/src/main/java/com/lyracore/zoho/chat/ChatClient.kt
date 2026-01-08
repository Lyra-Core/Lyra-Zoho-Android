package com.lyracore.zoho.chat

import android.app.Application
import android.content.Context
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.lyracore.zoho.chat.interfaces.ZohoChatListener
import com.lyracore.zoho.chat.models.ChatAdditionalInformation
import com.lyracore.zoho.chat.models.ChatEvent
import com.lyracore.zoho.chat.models.VisitorChatData
import com.lyracore.zoho.chat.models.enums.ChatEventType
import com.lyracore.zoho.core.CoreInitializer
import com.lyracore.zoho.core.models.ErrorEvent
import com.lyracore.zoho.core.models.ExceptionEvent
import com.lyracore.zoho.core.models.Translation
import com.lyracore.zoho.core.models.enums.ErrorLocation
import com.lyracore.zoho.core.models.enums.ExceptionLocation
import com.lyracore.zoho.department.DepartmentClient
import com.zoho.commons.LauncherModes
import com.zoho.commons.LauncherProperties
import com.zoho.livechat.android.VisitorChat
import com.zoho.livechat.android.constants.ConversationType
import com.zoho.livechat.android.listeners.ConversationListener
import com.zoho.livechat.android.listeners.SalesIQChatListener
import com.zoho.salesiqembed.ZohoSalesIQ
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.collections.get
import kotlinx.serialization.json.Json

/**
 * Enhanced ChatClient with full Zoho SalesIQ integration. Ported from the superior Capacitor plugin
 * implementation.
 */
object ChatClient {
    private var chatListener: ZohoChatListener? = null
    private var isListenersStarted = false
    private var languageCode = "en"
    private var pageTitle = "";

    /** Start listening to chat events and setup the launcher. */
    internal fun startListeners(listener: ZohoChatListener? = null) {
        try {
            if (!CoreInitializer.isZohoInitialized()) Exception("Zoho not initialized")
            if (isListenersStarted) return

            this.chatListener = listener

            val launcherProperties = LauncherProperties(LauncherModes.FLOATING)
            ZohoSalesIQ.setLauncherProperties(launcherProperties)

            showZohoLauncher()

            ZohoSalesIQ.Chat.setListener(
                    object : SalesIQChatListener {
                        override fun handleChatViewOpen(chatId: String) {
                            listener?.onChatViewOpen(chatId)
                        }

                        override fun handleChatViewClose(chatId: String) {
                            showZohoLauncher()
                            listener?.onChatViewClose(chatId)
                        }

                        override fun handleChatOpened(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.CHAT_OPENED,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onChatOpened(chatEvent)
                        }

                        override fun handleChatClosed(visitorChat: VisitorChat) {
                            showZohoLauncher()
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.CHAT_CLOSED,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onChatClosed(chatEvent)
                        }

                        override fun handleChatAttended(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.CHAT_ATTENDED,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onChatAttended(chatEvent)
                        }

                        override fun handleChatMissed(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.CHAT_MISSED,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onChatMissed(chatEvent)
                        }

                        override fun handleChatReOpened(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.CHAT_REOPENED,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onChatReOpened(chatEvent)
                        }

                        override fun handleRating(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.RATING,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onRating(chatEvent)
                        }

                        override fun handleFeedback(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.FEEDBACK,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onFeedback(chatEvent)
                        }

                        override fun handleQueuePositionChange(visitorChat: VisitorChat) {
                            val chatEvent =
                                    ChatEvent(
                                            type = ChatEventType.QUEUE_POSITION_CHANGE,
                                            chatId = visitorChat.chatID,
                                            visitorChat = parseVisitorChatData(visitorChat)
                                    )
                            listener?.onQueuePositionChange(chatEvent)
                        }
                    }
            )

            isListenersStarted = true
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_START_LISTENERS))
        }
    }

    /** Open the chat interface. */
    internal fun open(activity: AppCompatActivity) {
        try {
            if (!CoreInitializer.isZohoInitialized()) Exception("Zoho not initialized")
            activity.runOnUiThread {
                ZohoSalesIQ.Chat.getList(
                        ConversationType.OPEN,
                        object : ConversationListener {
                            override fun onSuccess(chats: ArrayList<VisitorChat>) {
                                ZohoSalesIQ.Chat.open(activity)
                            }

                            override fun onFailure(code: Int, message: String) {
                                // Handle failure
                                CoreInitializer.getExceptionHandlingCallback()
                                        ?.onError(ErrorEvent(code, message, ErrorLocation.CHAT_OPEN))
                            }
                        }
                )
            }
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_OPEN))
        }
    }

    /** Set the department for the chat. */
    internal suspend fun setDepartment(countryCode: String) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            var department = DepartmentClient.getDepartmentByCountry(countryCode)
            if (department == null)
                department = DepartmentClient.getDefaultDepartment()

            if (department == null)
                throw Exception("No default department available. Please contact support")

            val escapedDepartmentName = Html.escapeHtml(department?.name)
            ZohoSalesIQ.Chat.setDepartment(escapedDepartmentName)
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_SET_DEPARTMENT))
        }
    }

    /** Set the language for the chat. Default language is en */
    internal fun setLanguage(context: Context, languageCode: String) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")
            val languageMap: Map<String, String> =
                    mapOf(
                            "zh-hant" to "zh_TW",
                            "pt-br" to "pt_BR",
                            "pt-pt" to "pt_PT",
                            "es-la" to "es_LA"
                    )
            if (languageMap.containsKey(languageCode)) {
                ZohoSalesIQ.Chat.setLanguage(languageMap[languageCode])
                this.languageCode = languageMap.getValue(languageCode)
            } else {
                ZohoSalesIQ.Chat.setLanguage(languageCode)
                this.languageCode = languageCode
            }

            this.setPageTitle(this.pageTitle)
            this.setQuestion(context)
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_SET_LANGUAGE))
        }
    }

    /** Set additional information for the visitor. */
    internal fun setAdditionalInformation(additionalInfo: ChatAdditionalInformation) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            ZohoSalesIQ.Visitor.addInfo("Company Name", additionalInfo.companyName)
            ZohoSalesIQ.Visitor.addInfo("Primary Need", additionalInfo.primaryNeed)
            if (additionalInfo.primaryNeed == "None") {
                ZohoSalesIQ.Visitor.addInfo("Page", "Support")
                ZohoSalesIQ.Visitor.addInfo("Potential Risk", "Yes")
            } else {
                ZohoSalesIQ.Visitor.addInfo("Page", "Services")
                ZohoSalesIQ.Visitor.addInfo("Potential Risk", "No")
            }
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(
                            ExceptionEvent(ex, ExceptionLocation.CHAT_SET_ADDITIONAL_INFORMATION)
                    )
        }
    }

    /** Set the page title for tracking. */
    internal fun setPageTitle(title: String) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")
            this.pageTitle = title
            ZohoSalesIQ.Tracking.setPageTitle(title)
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_SET_PAGE_TITLE))
        }
    }

    /** Set a question for the visitor. */
    internal fun setQuestion(context: Context) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")

            val inputStream: InputStream =
                    context.assets.open("translations/${this.languageCode}.json")
            val size: Int = inputStream.available()
            val buffer: ByteArray = ByteArray(size)
            inputStream.read(buffer)
            val jsonString: String = String(buffer, Charsets.UTF_8)
            val obj = Json.decodeFromString<Translation>(jsonString)

            ZohoSalesIQ.Visitor.setQuestion(obj.ZOHO_QUESTION)
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_SET_QUESTION))
        }
    }

    /** End the current chat session. */
    internal fun endSession(application: Application) {
        try {
            if (!CoreInitializer.isZohoInitialized()) throw Exception("Zoho not initialized")
            endChat()
            ZohoSalesIQ.Launcher.show(ZohoSalesIQ.Launcher.VisibilityMode.WHEN_ACTIVE_CHAT)
            ZohoSalesIQ.clearData(application)
        } catch (ex: Exception) {
            // Handle exception
            CoreInitializer.getExceptionHandlingCallback()
                    ?.onException(ExceptionEvent(ex, ExceptionLocation.CHAT_END_SESSION))
        }
    }

    private fun endChat() {
        ZohoSalesIQ.Chat.getList(
                ConversationType.OPEN,
                object : ConversationListener {
                    override fun onSuccess(chats: ArrayList<VisitorChat>) {
                        if (chats.isNotEmpty()) {
                            ZohoSalesIQ.Chat.endChat(chats[0].chatID)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        // Handle failure
                    }
                }
        )
    }

    internal fun showZohoLauncher() {
        ZohoSalesIQ.Chat.getList(
                ConversationType.OPEN,
                object : ConversationListener {
                    override fun onSuccess(chats: ArrayList<VisitorChat>) {
                        val filteredChats = removeWaitingMissed(chats)
                        ZohoSalesIQ.showLauncher(filteredChats.isNotEmpty())

                        if (chats.isNotEmpty()) {
                            checkAndEndChat(chats[0].lastMessage.time)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        // Handle failure
                    }
                }
        )
    }

    private fun checkAndEndChat(inputUnixDate: Long) {
        val currentMillis = System.currentTimeMillis()
        val hoursDifference = currentMillis - inputUnixDate
        val hours = TimeUnit.MILLISECONDS.toHours(hoursDifference)
        if (hours > 24) {
            // Auto-end chat after 24 hours
        }
    }

    private fun removeWaitingMissed(chats: ArrayList<VisitorChat>): List<VisitorChat> {
        return chats.filter { chat ->
            !(chat.attenderId.isNullOrEmpty() && chat.chatStatus == "WAITING")
        }
    }

    private fun parseVisitorChatData(visitorChat: VisitorChat): VisitorChatData {
        return VisitorChatData(
                chatId = visitorChat.chatID,
                question = visitorChat.question,
                attenderName = visitorChat.attenderName,
                attenderEmail = visitorChat.attenderEmail,
                attenderId = visitorChat.attenderId,
                isBotAttender = visitorChat.isBotAttender,
                departmentName = visitorChat.departmentName,
                chatStatus = visitorChat.chatStatus,
                unreadCount = visitorChat.unreadCount,
                feedback = visitorChat.feedbackMessage,
                rating = visitorChat.rating,
                lastMessage = visitorChat.lastMessage?.toString(),
                lastMessageTime = visitorChat.lastMessage?.time ?: 0,
                lastMessageSender = visitorChat.lastMessage?.sender,
                queuePosition = visitorChat.queuePosition
        )
    }
}
