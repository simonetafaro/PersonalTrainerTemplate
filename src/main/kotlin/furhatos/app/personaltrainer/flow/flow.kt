package furhatos.app.personaltrainer.flow

import furhatos.app.personaltrainer.*
import furhatos.app.personaltrainer.nlu.Customized
import furhatos.app.personaltrainer.nlu.CustomizedTraining
//import furhatos.app.personaltrainer.nlu.Predefined
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.records.Record
import furhatos.skills.HostedGUI

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"

// Starting state, before our GUI has connected.
/*val NoGUI: State = state(null) {
    onEvent<SenseSkillGUIConnected> {
        //goto(GUIConnected)
        //print("hgfdsa")
        goto(Greeting)
    }
    }*/

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
        random(
                {   furhat.ask("Do you want a predefined workout or select the single exercises?") },
                {   furhat.ask("Do you want to choose individual exercises or a pre-planned workout?") }
        )
    }

    /*onResponse<Predefined>{

    }*/

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

    onResponse { // Catches everything else
        furhat.say("I didn't understand that")
        reentry()
    }

}

fun customizedBranch(customized: CustomizedTraining) : State = state (null){
    onEntry {
        furhat.say("${customized.text}, what a lovely choice!")
    }
}


val GUIConnected : State = state {
        onEntry {
            // Pass data to GUI
            send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList()))
        }

        // Users clicked any of our buttons
        onEvent(CLICK_BUTTON) {
            // Directly respond with the value we get from the event, with a fallback
            furhat.say("You want to train your ${it.get("data") ?: "something I'm not aware of" }")

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
        }
    }
