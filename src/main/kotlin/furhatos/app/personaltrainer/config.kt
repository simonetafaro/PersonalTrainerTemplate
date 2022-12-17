package furhatos.app.personaltrainer

import furhatos.event.Event

/*
    Variables and events
 */
val PORT = 1234 // GUI Port
val SPEECH_DONE = "SpeechDone"

// Event used to pass data to GUI
class DataDelivery(
        val title: String,
        val buttons : List<String>,
        val inputFields: List<String>
) : Event()

class PickOne(
        val title: String,
        val type : String
) : Event()

class ExerciseDelivery(
        val exerciseName : String,
        val gifName: String,
        val reps: String
) : Event()

class WorkoutDelivery(
        val workoutName : String

) : Event()



