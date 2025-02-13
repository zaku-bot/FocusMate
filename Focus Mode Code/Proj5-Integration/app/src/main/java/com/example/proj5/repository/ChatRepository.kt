package com.example.proj5.repository


import android.util.Log
import com.example.proj5.utils.CHAT_GPT_MODEL
import com.example.proj5.network.ApiClient
import com.example.proj5.response.ChatRequest
import com.example.proj5.response.ChatResponse
import com.example.proj5.response.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.resume

class ChatRepository {
    private val TAG = "CHATGPT-APP-"
    private val apiClient = ApiClient.getInstance()

    suspend fun createChatCompletion(message: String, apiKey: String): String? {

        return suspendCancellableCoroutine { continuation ->
            try {
                val chatRequest = ChatRequest(
                    arrayListOf(
                        Message(
                            "I have an app which marks geofences of the visited locations by users and when they enter the geofence, my app provides notifications," + "For example, \"Hey,You are near [place_name], [relevant_notification_message_from_you]]?\"" + "I WANT you to give me one such suggestion notification message for geofence trigger " + "ENTRY, by finding what type of location it is from the given list of categories and find one major category from them and use it " + "for providing a \"relevant\" message for the given location which is  \"location: Hayden Library\"  and category being in [library, study]," + " don't use all the category list values in your output, make sense of the" + " overall category from the given list of categories and give a nice text in one line, and if the " + "list of categories being provided to you as input,if it has a value named \"default\" then just give the output as \"Hey, you have entered the place [place_name]\", else give the relevant suggestion in one line",
                            "system"
                        ), Message(
                            message, role = "user"
                        )
                    ), CHAT_GPT_MODEL,

                )
                apiClient.createChatCompletion(chatRequest = chatRequest, authorization = "Bearer $apiKey")
                    .enqueue(object : Callback<ChatResponse> {
                        override fun onResponse(
                            call: Call<ChatResponse>,
                            response: Response<ChatResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.choices?.get(0)?.message?.let {
                                    Log.d(TAG + "APP_MESSAGE", it.toString())
                                    continuation.resume(it.content)
                                }
                            } else {
                                Log.d(
                                    TAG + "APP-ERROR",
                                    "Code is ${response.code()} and response is ${response.toString()}"
                                )
                                continuation.resume(null)
                            }
                        }

                        override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                            Log.d(TAG, "Something went wrong, ${t.printStackTrace()}")
                            continuation.resume(null)
                        }

                    })


            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }
}