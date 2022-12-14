package furhatos.app.personaltrainer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// Buttons
val buttons = listOf("Arms", "Legs", "Abs", "Back")

// Options
val options = listOf("Exercise", "Workout")

// Exercise
val exercises = listOf("Push-up", "Sit-up", "Jumping jack")

/*
 Input fields, each with a answer to be spoken. The answer is defined as a lambda
 function since we want to have different answers depending on what favorite robot the
 user inputs
  */
val inputFieldData = mutableMapOf<String, (String) -> String>(
    "Name" to { name -> "Nice to meet you $name, let's start!   " }
)

class SingleExercise(val name: String,
                     var reps: Int?,
                     var sets: Int?,
                     var restTime: Int?
                     /*@SerializedName("tips") @Expose var tips: Array<String>? = null*/) {
    override fun toString(): String {
        return "Exercise: $name \n Reps: $reps \n Sets: $sets \n Rest time (in seconds): $restTime"
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
        return "Exercise: $name, Tips: $stringTips"
    }
}


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
