package cz.daniil.zaru.chatty.service

object MessageService {

    const val UNSUPPORTED_QUESTION = "I'm sorry I don't understand the question"
    const val GREETINGS_MESSAGE = "Hi, I'm Chatty. How can I help you?"
    fun getConversionMessage(convertedAmount: Double, toCurrency: String) =
        "It's %.2f ${toCurrency.uppercase()}".format(convertedAmount)
    fun getUnreachableServiceMessage(url: String) = "I'm sorry, I can't answer now, service on url " +
            "$url seems to be unreachable"

}