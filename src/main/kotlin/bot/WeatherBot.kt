package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import data.remote.repository.WeatherRepository
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter.*
import com.github.kotlintelegrambot.logging.LogLevel
import data.remote.API_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//consts
private const val BOT_ANSWER_TIMEOUT = 10 // for waiting time to get answer
private const val BOT_TOKEN = "5893964010:AAGlHQBksAwHhHeP3s7fAkfxX4pdSFlOBjI" //token from BotFather
private const val LINK_YW = "https://yandex.ru/pogoda/"

//there is creating the bot
class WeatherBot(private val weatherRepository: WeatherRepository) {

    private var _chatID: ChatId? = null //to chat
    private val chatID by lazy { requireNotNull(_chatID) } //to chat, to postpone init
    private lateinit var country: String //for user's name country

    //to create bot
    fun createBot(): Bot {
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN
            logLevel = LogLevel.Error
            //to answer to user's query
            dispatch {
                setUpCommands() //to set up commands for bot's reacting
                setUpCallback() //command handler
            }
        }
    }

    //extension function to extend class Dispatcher
    private fun Dispatcher.setUpCallback() {

        //FIRST: about your location and then check it
        callbackQuery(callbackData = "getLocation") {
            bot.sendMessage(
                chatId = chatID,
                text = "Send your location"
            )

            //react to sent from user info, then sends lat and lon to server
            location {
                CoroutineScope(Dispatchers.IO).launch {
                    val userCountry = weatherRepository.getCountryName(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        "json"
                    ).address.country

                    country = userCountry

                    //for check
                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "That's true",
                                callbackData = "YES"
                            )
                        )
                    )

                    bot.sendMessage(
                        chatId = chatID,
                        text = "Your location is ${country}, isn't it? If not, send again.",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }
            }
        }

        //SECOND: entering your location
        callbackQuery(callbackData = "enterLocation") {
            bot.sendMessage(
                chatId = chatID,
                text = "Enter your location."
            )

            //different, not like text, with some filters (Filter.Text)
            message(Text) {
                country = message.text.toString()

                //for check
                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = "That's true",
                            callbackData = "YES"
                        )
                    )
                )

                bot.sendMessage(
                    chatId = chatID,
                    text = "Your location is ${country}, isn't it? If not, enter again.",
                    replyMarkup = inlineKeyboardMarkup
                )
            }

        }

        //THIRD: YES user answer
        callbackQuery(callbackData = "YES") {
            bot.sendMessage(
                chatId = chatID,
                text = "Let's know the weather."
            )
            //pretends typing
            bot.sendChatAction(
                chatId = chatID,
                action = ChatAction.TYPING
            )

            //to get info from repository
            CoroutineScope(Dispatchers.IO).launch {
                val currentWeather = weatherRepository.getCurrentWeather(
                    API_KEY,
                    country,
                    "no"
                )

                //this cool
                bot.sendMessage(
                    chatId = chatID,
                    text = """
                         Temprature (degrees): ${currentWeather.current.tempDegrees}
                         RealFeel: ${currentWeather.current.feelsLikeDegrees}
                         Cloudes: ${currentWeather.current.cloud}
                         Humidity: ${currentWeather.current.humidity} 
                         Wind Speed: ${currentWeather.current.windDegree}
                         Pressure: ${currentWeather.current.pressureIn}
                            """.trimIndent()
                )

                bot.sendMessage(
                    chatId = chatID,
                    text = "Would you know new information about the weather?\n If you would, use /weather"
                )

                //new country
                country = ""
            }
        }

        //FOURTH: send yanex link
        callbackQuery(callbackData = "getLinkYW") {
            bot.sendMessage(
                chatId = chatID,
                text = "Open and see the weather $LINK_YW"
            )
            //pretends typing
            bot.sendChatAction(
                chatId = chatID,
                action = ChatAction.TYPING
            )

            //back
            bot.sendMessage(
                chatId = chatID,
                text = "Would you know new information about the weather?\n If you would, use /weather"
            )
        }

        //KNOW THE WEATHER

    }

    //extension function to extend class Dispatcher
    private fun Dispatcher.setUpCommands() {
        command("start") {
            _chatID = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatID,
                text = "Hello, let's know about weather in your country!\n Enter command /weather"
            )
        }

        //commands and their buttons
        command("weather") {
            val _inlineKeyboard = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Determine my location",
                        callbackData = "getLocation"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Enter your location",
                        callbackData = "enterLocation"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Get a link to the YandexWeather",
                        callbackData = "getLinkYW"
                    )
                )
            )

            //bot message
            bot.sendMessage(
                chatId = chatID,
                text = "To get info about weather you should indicate your location.",
                replyMarkup = _inlineKeyboard
            )
        }
    }
}