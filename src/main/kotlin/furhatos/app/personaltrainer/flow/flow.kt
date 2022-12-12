package furhatos.app.personaltrainer.flow

import furhatos.app.personaltrainer.*
import furhatos.app.personaltrainer.nlu.*
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.records.Record
import furhatos.records.User
import furhatos.skills.HostedGUI
import furhatos.records.Location

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

val Greeting : State = state(null){
    onEntry {
        random(
                {   furhat.say("Hi there") },
                {   furhat.say("Oh, hello there") }
        )
        goto(ExerciseVSWorkout)
    }
}

val ExerciseVSWorkout: State = state(null){
    onEntry {
        send(DataDelivery(title ="Select one:", buttons = options, inputFields = listOf()));


        random(
                {   furhat.ask("Do you want a predefined workout or select the single exercises?. You can either tell me or selecting it by clicking on the corresponding button.") },
                {   furhat.ask("Do you want to choose individual exercises or a pre-planned workout? You can either tell me or selecting it by clicking on the corresponding button.") }
        )
    }


    onEvent(CLICK_BUTTON) {
        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You want to do a ${it.get("data") ?: "something I'm not aware of" }")
        if(it.get("data") == "Exercise"){
            var customized =  CustomizedTraining();
            customized.text = "Single exercise"
            goto(customizedBranch(customized))
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

            goto(customizedBranch(selectedType))
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



    onResponse { // Catches everything else
        furhat.say("I didn't understand that")
        reentry()
    }

}

fun customizedBranch(customized: CustomizedTraining) : State = state (null){
    onEntry {
        furhat.say("${customized.text}, what a lovely choice!")

        send(DataDelivery(title = "Pick one exercise:", buttons = exercises, inputFields = listOf()))
        send(SPEECH_DONE)


        random(
                { furhat.ask("Now, pick an exercise.") },
                { furhat.ask("Please, elect the exercise you want to do") }
        )
    }

    onResponse<Exercise> {

        var exerciseName = it.intent.exerciseType
        furhat.say("${exerciseName}? Right?")
        send(ExerciseDelivery(exerciseName = exerciseName.toString(), gifName = "", reps = ""  ))

        var firstEx = SingleExercise(exerciseName.toString(), null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }


    onEvent(CLICK_BUTTON) {
        var exerciseName = it.get("data") as String;
        // Directly respond with the value we get from the event, with a fallback
        furhat.say("You want to do a ${exerciseName ?: "something I'm not aware of" }")

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        send(ExerciseDelivery(exerciseName = exerciseName, gifName = "", reps = ""  ))

        //Here we add the next exercise to the ArrayList of exercises (only with the name)
        //reps, sets and restTime will be set in the next states.
        var firstEx = SingleExercise(exerciseName.toString(), null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }
}

fun repsSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(null){
    onEntry{
        //test:
        print(arrayOfExercises[0].name)
    }

}



fun predefinedBranch(predefined: PredefinedTraining) : State = state (null){
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
