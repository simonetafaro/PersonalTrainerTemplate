package furhatos.app.personaltrainer.nlu

import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language

// Predefined Training entity.
/*class PredefinedTraining : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("predefined training", "predefined workout", "planned workout")
    }
}*/

// Customized Training entity
class CustomizedTraining(val name: String? = null) : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("single exercise", "customized training", "customized workout", "personalized workout")
    }
}


/*class Predefined(var predefined : PredefinedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @predefined", "I want a @predefined", "I would like a @predefined")
    }
}*/

class Customized(var customized : CustomizedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @customized", "I want a @customized", "I would like a @customized")
    }


}


