package furhatos.app.personaltrainer

import furhatos.event.Event

/*
    Variables and events
 */
val PORT = 1234 // GUI Port
val SPEECH_DONE = "SpeechDone"
val SPEECH_INPROGRESS = "SpeechInProgress"


val RESTTIME_START = "TimeToRest"
val RESTTIME_STOP = "TimeToRestart"

// Event used to pass data to GUI
class DataDelivery(
        val title: String,
        val buttons : List<String>,
        val inputFields: List<String>,
        val inputType: String,
        val inputLabel: String
) : Event()

class PickOne(
        val title: String,
        val type : String,
        val exerciseList: List<String>
) : Event()

class ExerciseDelivery(
        val exerciseName : String,
        val reps: String,
        val sets: String,
        val rest: String,
        val currentSet: Int
) : Event()

class WorkoutDelivery(
        val workoutName : String,
        val exercises: List<SingleExercise>,
        val current: Int
) : Event()



