package furhatos.app.personaltrainer

import furhatos.event.Event

val options = listOf("Yes", "No")

val workouts = listOf("Upper body", "Lower body", "Full body")

val difficulties = listOf("Beginner", "Intermediate", "Advanced")
/*
 Input fields, each with a answer to be spoken. The answer is defined as a lambda
 function
  */
val nameFieldData = mutableMapOf<String, (String) -> String>(
    "Name" to { name -> "Nice to meet you $name, let's start!" }
)

val repFieldData = mutableMapOf<String, (String) -> String>(
        "Reps" to { reps -> "Ok, then let's do $reps" }
)

val setFieldData = mutableMapOf<String, (String) -> String>(
        "Sets" to { sets -> "Ok, then let's do $sets" }
)

val restFieldData = mutableMapOf<String, (String) -> String>(
        "Rest" to { rest -> "Ok, then let's do $rest" }
)

enum class WorkoutsEnum{
    UPPERBODY, LOWERBODY, FULLBODY
}

enum class DifficultiesEnum{
    BEGINNER, INTERMEDIATE, ADVANCED
}
class SingleExercise(val name: String,
                     var reps: Int?,
                     var sets: Int?,
                     var restTime: Int?,
                     var tips: Array<String>?) : Event() {
    override fun toString(): String {
        var temp = "No tips"
        if( tips != null ) {
            temp = ""
            for (tip in tips!!) temp += "Tip: $tip \n"
        }
        return "Exercise: $name \n Reps: $reps \n Sets: $sets \n Rest time (in seconds): $restTime \n $temp"
    }
}

class SingleExerciseParser(val name: String,
                           val reps: Map<String, Int>,
                           val sets: Map<String, Int>,
                           val rest_time: Map<String, Int>,
                           val musclegroup: String,
                           val equipment: String,
                           val tips: Array<String>){
    override fun toString(): String {
        var stringTips = ""
        for(el in tips) stringTips += "$el "
        return "Exercise: $name, Tips: $stringTips,  Reps: $reps, Sets: $sets, Rest: $rest_time, "
    }
}

class WorkoutParser(val name: String, val exercises: ArrayList<String>)
{}
/*fun test (exercise: SingleExercise){
    print(exercise.name)
    print(exercise.reps)
}

fun main(){
    val exercise = SingleExercise("Gino", null, null,null)
    exercise.reps = 5
    test (exercise)
}
*/
