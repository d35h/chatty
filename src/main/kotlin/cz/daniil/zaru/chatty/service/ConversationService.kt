package cz.daniil.zaru.chatty.service

import cz.daniil.zaru.chatty.domain.ParsedConversion
import cz.daniil.zaru.chatty.property.ApplicationProperty
import cz.daniil.zaru.chatty.service.MessageService.UNSUPPORTED_QUESTION
import cz.daniil.zaru.chatty.service.MessageService.GREETINGS_MESSAGE
import cz.daniil.zaru.chatty.service.QuestionType.GREETINGS
import cz.daniil.zaru.chatty.service.QuestionType.JOKES
import cz.daniil.zaru.chatty.service.QuestionType.RATES

class ConversationService(
    private val rateConversionService: RateConversionService,
    private val jokeService: JokeService,
    applicationProperty: ApplicationProperty
) {
    private var supportedQuestions: Map<String, List<Regex>>

    init {
        supportedQuestions = applicationProperty.conversation.getQuestionsWithRegexes()
    }

    suspend fun getAnswer(question: String): String {
        val questionLowerCase = question.lowercase()
        val questionTypeToAnswerRegex = getMatchedAnswer(questionLowerCase)
        return when (questionTypeToAnswerRegex?.first) {
            RATES.name.lowercase() -> convertAndGetMessage(questionLowerCase, questionTypeToAnswerRegex.second)
            GREETINGS.name.lowercase() -> GREETINGS_MESSAGE
            JOKES.name.lowercase() -> jokeService.getRandomJoke().value
            else -> UNSUPPORTED_QUESTION
        }
    }

    private suspend fun convertAndGetMessage(question: String, conversionRegex: Regex): String {
        val (from, to, amount) = parseConversion(question, conversionRegex)
        return MessageService.getConversionMessage(rateConversionService.convert(from, to, amount), to)
    }

    private fun parseConversion(question: String, conversionRegex: Regex): ParsedConversion {
        val parsedGroups = conversionRegex.findAll(question).first().groupValues
        // According to our regex's groups 'from' is always at '2' position, 'to' at '3' and 'amount' at '1'
        return ParsedConversion(parsedGroups[2], parsedGroups[3], parsedGroups[1].toDouble())
    }

    private fun getMatchedAnswer(candidate: String): Pair<String, Regex>? =
        supportedQuestions.firstNotNullOfOrNull { (questionType, supportedRegexes) ->
            getParsingRegex(supportedRegexes, candidate)?.let { matchedRegex ->
                questionType to matchedRegex
            }
        }

    private fun getParsingRegex(regexes: List<Regex>, candidate: String): Regex? =
        regexes.firstOrNull { it.containsMatchIn(candidate) }

}

enum class QuestionType {
    RATES, GREETINGS, JOKES
}