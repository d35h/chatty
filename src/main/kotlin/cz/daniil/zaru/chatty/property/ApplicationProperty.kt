package cz.daniil.zaru.chatty.property

import kotlin.text.Regex
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties
@Configuration
@ConfigurationProperties("application")
class ApplicationProperty {
    lateinit var baseCurrency: String
    lateinit var baseExchangeUrl: String
    lateinit var baseJokesUrl: String

    @NestedConfigurationProperty
    var conversation = Conversation()
}

class Conversation {
    lateinit var questions: Map<String, List<String>>
    lateinit var placeholderToRegex: Map<String, String>

    fun getQuestionsWithRegexes(): Map<String, List<Regex>> = questions.map { (questionType, originalQuestions) ->
        questionType to originalQuestions.map { originalQuestion ->
            var questionWithReplacedPlaceHolders = originalQuestion
            placeholderToRegex.map { (placeHolder, regex) ->
                questionWithReplacedPlaceHolders = questionWithReplacedPlaceHolders.replace(placeHolder, regex)
            }
            questionWithReplacedPlaceHolders.lowercase().toRegex()
        }
    }.toMap()

}
