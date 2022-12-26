package furhatos.app.personaltrainer.nlu

import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.common.Number
import furhatos.util.Language

// Predefined Training entity.
class PredefinedTraining : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("predefined training", "predefined workout", "planned workout", "pre planned workout")
    }
}

// Customized Training entity
class CustomizedTraining(val name: String? = null) : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("single exercise", "customized training", "customized workout", "personalized workout")
    }
}

class ExerciseType : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("regular push-ups", "sit-ups", "jumping jacks")
    }
}

class WorkoutType : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("full_body: full body, whole body", "lower_body: lower body, legs", "upper_body: upper body, arms, chest, shoulder, core, back")
    }
}

class Exercise(var exerciseType: ExerciseType? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @exerciseType", "I want to do @exerciseType", "@exerciseType")
    }
}




class Predefined(var predefined : PredefinedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @predefined", "I want a @predefined", "I would like a @predefined")
    }
}

class Customized(var customized : CustomizedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @customized", "I want a @customized", "I would like a @customized")
    }
}

class RepsNumberIntent(var number : Number? = null ): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to do @number repetitions", "I want to do @number reps", "I would like to do @number reps", "I would like to do @number repetitions", "@number", "@number reps", "@number repetitions")
    }
}

class SetsNumberIntent(var number : Number ? = null ): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to do @number sets", "I want to do @number sets", "I would like to do @number sets", "I would like to do @number sets", "@number", "@number sets", "@number sets")
    }
}


class RestIntentSeconds(var number : Number ? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to do @number seconds rest", "I want to rest @number seconds", "I would like to do @number seconds rest", "@number", "@number seconds", "I would like to rest @number seconds")
    }
}

class RestIntentMinutes(var number : Number ? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to do @number minutes rest", "I want to rest @number minutes", "I would like to do @number minutes rest", "@number minutes", "I would like to rest @number minutes")
    }
}

class StartIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("start", "I would like to start", "I'm ready", "Let's go", "Let's begin", "ready")
    }
}

class FinishIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("done", "finish", "completed", "I'm done", "I've finished")
    }
}

class WorkoutIntent(var workoutType : WorkoutType? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@workoutType", "I would like to train the @workoutType", "@workoutType workout")
    }
}
/*class UpperBodyIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("upper body", "I would like to train the upper body", "arms", "chest", "shoulder", "core", "back", "lower body workout")
    }
}*/

/*class LowerBodyIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("lower body", "I would like to train the lower body", "legs", "lower body workout")
    }
}*/

/*class FullBodyIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("full body", "I would like to train the whole body", "full body workout")
    }
}*/

class BeginnerIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("easy", "I'm a beginner", "easy level", "easy difficulty", "beginner", "beginner difficulty")
    }
}

class IntermediateIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("intermediate", "I'm not a beginner nor an expert ", "medium", "medium level", "intermediate level", "medium difficulty", "intermediate difficulty")
    }
}


class AdvancedIntent(): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("hard", "I'm an expert ", "hard level", "hard difficulty", "advanced", "advanced difficulty")
    }
}

