package furhatos.app.personaltrainer.flow

import furhatos.app.personaltrainer.*
import furhatos.app.personaltrainer.nlu.*
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.records.Record
import furhatos.skills.HostedGUI
import furhatos.records.Location

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"
var arrayOfExercises = ArrayList<SingleExercise>()

// Starting state, before our GUI has connected.
val NoGUI: State = state(null) {
    onEvent<SenseSkillGUIConnected> {
        goto(GUIConnected)
        //goto(Greeting)
    }
    }

/*
    Here we know our GUI has connected. Since the user might close down the GUI and then reopen
    again, we inherit our handler from the NoGUI state. An edge case might be that a second GUI
    is opened, but this is not accounted for here.

 */


val Greeting : State = state(Interaction){
    onEntry {
        random(
                {   furhat.say("Hi there") },
                {   furhat.say("Oh, hello there") }
        )
        goto(ExerciseVSWorkout)
    }
}

val ExerciseVSWorkout: State = state(Interaction){
    onEntry {
        send(DataDelivery(title ="Select one:", buttons = options, inputFields = listOf()));
        val howto = "Say it to me or click the button."

        random(
                {   furhat.ask("Do you want a predefined workout or select the single exercises?. $howto") },
                {   furhat.ask("Do you want to choose individual exercises or a pre-planned workout? $howto") }
        )
    }


    onEvent(CLICK_BUTTON) {
        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You want to do a ${it.get("data") ?: "something I'm not aware of" }")
        if(it.get("data") == "Exercise"){
            var customized =  CustomizedTraining();
            customized.text = "Single exercise"
            goto(customizedBranch(customized, arrayOfExercises))
        } else {
            var predefined = PredefinedTraining();
            predefined.text = "Predifined workout"
            goto(predefinedBranch(predefined))
        }
        // Let the GUI know we're done speaking, to unlock buttons

    }

    onResponse<Customized>{
        val selectedType = it.intent.customized
        if (selectedType != null) {

            goto(customizedBranch(selectedType, arrayOfExercises))
        }
        else {
            propagate()
        }

        //furhat.say("${fruits.text}, what a lovely choice!")
    }

   onResponse<Predefined>{
        val selectedType = it.intent.predefined

        if (selectedType != null) {

            goto(predefinedBranch(selectedType))
        }
        else {
            propagate()
        }

        //furhat.say("${fruits.text}, what a lovely choice!")
    }



    /*onResponse { // Catches everything else
        furhat.say("I didn't understand that")
        reentry()
    }*/

}

fun customizedBranch(customized: CustomizedTraining?, arrayOfExercises: ArrayList<SingleExercise>) : State = state (Interaction){
    onEntry {
        if ( arrayOfExercises.size == 0 ) {
            if (customized != null) {
                furhat.say("${customized.text}, what a lovely choice!")
            }
        } else {
            furhat.say( "You have ${arrayOfExercises.size} exercises in your training. Let's add one more!")
            for (el in arrayOfExercises) println(el.toString())
        }

        send(DataDelivery(title = "Pick one exercise:", buttons = exercises, inputFields = listOf()))


        send(SPEECH_DONE)


        random(
                { furhat.ask("Now, pick an exercise.") },
                { furhat.ask("Please, select the exercise you want to do") }
        )
    }

    onResponse<Exercise> {

        val exerciseName = it.intent.exerciseType
        furhat.say("${exerciseName}? Right?")
        //send(ExerciseDelivery(exerciseName = exerciseName.toString(), gifName = "", reps = ""  ))

        val firstEx = SingleExercise(exerciseName.toString(), null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }


    onEvent(CLICK_BUTTON) {
        val exerciseName = it.get("data") as String;
        // Directly respond with the value we get from the event, with a fallback
        furhat.say("You want to do a ${exerciseName ?: "something I'm not aware of" }")

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        //send(ExerciseDelivery(exerciseName = exerciseName, gifName = "", reps = ""  ))

        //Here we add the next exercise to the ArrayList of exercises (only with the name)
        //reps, sets and restTime will be set in the next states.
        var firstEx = SingleExercise(exerciseName.toString(), null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }
}

fun repsSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        //test:
        //print(arrayOfExercises[0].name)

        send(DataDelivery(buttons = listOf(), inputFields = inputFieldData.keys.toList(), title =  "Select the number of repetitions you want to perform during each set"))
        send(SPEECH_DONE)

        random(
                { furhat.ask("How many repetitions do you want to perform during each set?") },
                { furhat.ask("How many reps do you want to do for each set? ") }
        )
    }

    onResponse <RepsNumberIntent> {
        val reps = it.intent.number?.value
        furhat.say("Ok then. Let's do $reps repetitions!")
        arrayOfExercises[arrayOfExercises.size - 1].reps = reps

        //call to the gui

        goto(setsSelectionState(arrayOfExercises))
    }

    //onEvent (gui)
    onEvent(VARIABLE_SET) {
        // Get data from event
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        //val value = data.getString("value")
        val value = data.getInteger("value")


        // Get answer depending on what variable we changed and what the new value is, and speak it out
        //val answer = inputFieldData[variable]?.invoke(value)

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        //if (answer != null) {
            arrayOfExercises[arrayOfExercises.size - 1].reps = value
                    //nswer.toInt()
        //}
        //furhat.attend(Location.STRAIGHT_AHEAD);

        goto(setsSelectionState(arrayOfExercises))
    }
}

fun setsSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        send(DataDelivery(buttons = listOf(), inputFields = inputFieldData.keys.toList(), title =  "Select the number of sets you want to perform"))
        send(SPEECH_DONE)
        random(
                { furhat.ask("How many sets do you want to perform?") },
                { furhat.ask("How many sets do you want to do?") }
        )

    }

    onResponse <SetsNumberIntent> {
        val sets = it.intent.number?.value
        furhat.say("Ok then. Let's do $sets sets!")
        arrayOfExercises[arrayOfExercises.size - 1].sets = sets

        // call to the gui

        goto(restSelectionState(arrayOfExercises))
    }

    //onEvent (gui)
    onEvent(VARIABLE_SET) {
        // Get data from event
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        //val value = data.getString("value")
        val value = data.getInteger("value")


        // Get answer depending on what variable we changed and what the new value is, and speak it out
        //val answer = inputFieldData[variable]?.invoke(value)

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        //if (answer != null) {
        arrayOfExercises[arrayOfExercises.size - 1].sets = value

        goto(restSelectionState(arrayOfExercises))
    }
}

fun restSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        send(DataDelivery(buttons = listOf(), inputFields = inputFieldData.keys.toList(), title =  "Select the rest time between two sets"))
        send(SPEECH_DONE)
        random(
                { furhat.ask("How long do you want to rest between the sets?") }
        //more choices...
        )

    }

    onResponse <RestIntent> {
        val rest = it.intent.number?.value
        furhat.say("Ok then. Let's do $rest seconds of rest!")
        arrayOfExercises[arrayOfExercises.size - 1].restTime = rest

        // call to the gui

        goto(somethingElseState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        // Get data from event
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        //val value = data.getString("value")
        val value = data.getInteger("value")


        // Get answer depending on what variable we changed and what the new value is, and speak it out
        //val answer = inputFieldData[variable]?.invoke(value)

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        //if (answer != null) {
        arrayOfExercises[arrayOfExercises.size - 1].restTime = value

        goto(restSelectionState(arrayOfExercises))
    }


}

fun somethingElseState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        random(
                { furhat.ask("Do you want another exercise to the workout?") },
                { furhat.ask("Do you want to add another exercise to your training?") },
                { furhat.ask("Do you want me to add another exercise to your workout before starting?") }
        )
    }

    onResponse<Yes> {
        furhat.say("Feel energetic, uh?")
        goto(customizedBranch(null, arrayOfExercises))
    }

    onResponse<No> {

        val jsonString: String = File("./assets/src/components/data.json").readText(Charsets.UTF_8)

        val singleExercises = Gson().fromJson(jsonString, SingleExerciseParser::class.java)


        //creation of the final array:
        for (ex in arrayOfExercises){
            var tempArray: Array<String>

        }


        //-----
        // let's start the training
    }

}

fun main(){
    val jsonString: String = File("./assets/exampleGui/src/data.json").readText(Charsets.UTF_8)

    val exercisesJSONArray = JSONArray(JSONObject(jsonString)["exercise"].toString())
    val availableExercise = arrayListOf<SingleExerciseParser>()
    for (i in 0 until exercisesJSONArray.length()) {
        val exercise = exercisesJSONArray.getJSONObject(i)
        var currExercise = Gson().fromJson(exercise.toString(), SingleExerciseParser::class.java)
        availableExercise.add(currExercise)
        //println("${exercise.get("name")} by ${exercise.get("muscle group")}")
    }
    for (el in availableExercise) {
        print(el)
        print("\n")
    }
}



fun predefinedBranch(predefined: PredefinedTraining) : State = state (Interaction){
    onEntry{
        furhat.say("You selected ${predefined.text}")
    }


    /*onEvent(CLICK_BUTTON) {
        var workoutName = it.get("data") as String;
        // Directly respond with the value we get from the event, with a fallback
        furhat.say("You want to do a ${workoutName ?: "something I'm not aware of" }")

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        send(WorkoutDelivery(workoutName = workoutName))
        //send(ExerciseDelivery(exerciseName = exerciseName, gifName = "", reps = ""  ))

    }*/
}


val GUIConnected : State = state {
        onEntry {
            // Pass data to GUI
            send(DataDelivery(buttons = listOf(), inputFields = inputFieldData.keys.toList(), title =  "Insert your name to start"))
        }

        // Users clicked any of our buttons
        onEvent(CLICK_BUTTON) {
            // Directly respond with the value we get from the event, with a fallback
            furhat.say("You want to do a ${it.get("data") ?: "something I'm not aware of" }")

            // Let the GUI know we're done speaking, to unlock buttons
            send(SPEECH_DONE)

        }

        // Users saved some input
        onEvent(VARIABLE_SET) {
            // Get data from event
            val data = it.get("data") as Record
            val variable = data.getString("variable")
            val value = data.getString("value")

            // Get answer depending on what variable we changed and what the new value is, and speak it out
            val answer = inputFieldData[variable]?.invoke(value)
            furhat.say(answer ?: "Something went wrong")

            // Let the GUI know we're done speaking, to unlock buttons
            send(SPEECH_DONE)
            furhat.attend(Location.STRAIGHT_AHEAD);


            goto(ExerciseVSWorkout)
        }
    }
