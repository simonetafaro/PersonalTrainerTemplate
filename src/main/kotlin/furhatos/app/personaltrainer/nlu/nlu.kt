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
class CustomizedTraining : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("single exercise", "customized training", "customized workout", "personalized workout")
    }
}

class ExerciseType : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("regular push-ups: push-ups, regular push-ups", "sit-ups", "jumping jacks", "tricep push-ups", "squats", "bicycle sit-ups", "superman", "lungee", "burpee", "knee to elbow plank", "tricep dips", "high knees", "mountain climbers", "lower back raises", "bicep curls", "leg raises")
    }
}

class WorkoutType : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("full_body: full body, whole body", "lower_body: lower body, legs", "upper_body: upper body, arms, chest, shoulder, core, back")
    }
}

class Difficulty : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("beginner: beginner, easy, easiest", "intermediate: intermediate, medium", "advanced: advanced, hard, hardest, shoulder, core, back")
    }
}
class Exercise(var exerciseType: ExerciseType? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @exerciseType", "I want to do @exerciseType", "@exerciseType")
    }
}

class Predefined(var predefined : PredefinedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @predefined", "I want a @predefined", "I would like a @predefined", "@predefined")
    }
}

class Customized(var customized : CustomizedTraining? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to do a @customized", "I want a @customized", "I would like a @customized", "@customized")
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

class StartIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("start", "I would like to start", "I'm ready", "Let's go", "Let's begin", "ready")
    }
}

class FinishIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("done", "finish", "completed", "I'm done", "I've finished")
    }
}

class WorkoutIntent(var workoutType : WorkoutType? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@workoutType", "I would like to train the @workoutType", "@workoutType workout")
    }
}

class DifficultyIntent(var difficulty : Difficulty? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@difficulty", "I would like to train at @difficulty level", "@difficulty workout", "I want to do at @difficulty level", "@difficulty difficulty", "@difficulty level", "I would like to train at @difficulty difficulty", "I want to do at @difficulty difficulty" )
    }
}

class UndoIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Sorry, I made a mistake", "can I go back?", "go back", "undo", "undo action")
    }
}

