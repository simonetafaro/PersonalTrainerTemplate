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
import furhatos.flow.kotlin.voice.PollyNeuralVoice
import furhatos.flow.kotlin.voice.Voice
import furhatos.gestures.Gestures
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.collections.ArrayList
import kotlin.streams.toList

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"


// Starting state, before our GUI has connected.
val NoGUI: State = state(null) {
    onEvent<SenseSkillGUIConnected> {

        //change for a more pleasant voice
        furhat.voice = PollyNeuralVoice.Matthew().also { it.style = PollyNeuralVoice.Style.Conversational}

        goto(GUIConnected)
        //goto(Greeting)
    }
    }

/*
    Here we know our GUI has connected. Since the user might close down the GUI and then reopen
    again, we inherit our handler from the NoGUI state. An edge case might be that a second GUI
    is opened, but this is not accounted for here.

 */

val GUIConnected : State = state {
    onEntry {
        // Pass data to GUI
        send(DataDelivery(buttons = listOf(), inputFields = nameFieldData.keys.toList(), title =  "Insert your name to start", inputType = "text", inputLabel = "Enter your name"))
    }

    onUserEnter {
        goto(Greeting)
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
        val answer = nameFieldData[variable]?.invoke(value)
        furhat.say(answer ?: "Something went wrong")

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
        furhat.attend(Location.STRAIGHT_AHEAD)


        goto(ExerciseVSWorkout)
    }
}


val Greeting : State = state(Interaction){
    onEntry {
        random(
                {   furhat.say("Hi there") },
                {   furhat.say("Oh, hello there") }
        )
        send(SPEECH_DONE)
        goto(ExerciseVSWorkout)
    }
}

val ExerciseVSWorkout: State = state(Interaction){
    onEntry {
        //send(DataDelivery(title ="Select one:", buttons = options, inputFields = listOf()));
        send(PickOne(title = "Select one option:", type= "Training", exerciseList= listOf()))
        val howto = "Say it to me or click the button."
        furhat.stopListening()
        random(
                {   furhat.say("Do you want to do a predefined workout or select single exercises?. $howto") },
                {   furhat.say("Do you want to choose individual exercises or a pre-planned workout? $howto") }
        )
        send(SPEECH_DONE)
        furhat.listen(60000)
    }


    onEvent(CLICK_BUTTON) {

        furhat.stopSpeaking()
        furhat.gesture(Gestures.Smile(duration = 3.0))
        if(it.get("data") == "Exercise"){
            furhat.say("Single exercise. ${furhat.voice.emphasis("Great!")}")
            goto(customizedBranch(ArrayList()))
        } else {
            furhat.say("Predefined workout. ${furhat.voice.emphasis("Great!")}")
            goto(predefinedBranch())
        }
        // Let the GUI know we're done speaking, to unlock buttons

    }

    onResponse<Customized>{

        val selectedType = it.intent.customized
        if (selectedType != null) {
            furhat.gesture(Gestures.Smile(duration = 3.0))
            furhat.say("${selectedType}. ${furhat.voice.emphasis("Great!")}")
            goto(customizedBranch(ArrayList()))
        }
        else {
            propagate()
        }
    }

   onResponse<Predefined>{
        val selectedType = it.intent.predefined

        if (selectedType != null) {
            furhat.gesture(Gestures.Smile(duration = 3.0))
            furhat.say("${selectedType}. ${furhat.voice.emphasis("Great!")}")
            goto(predefinedBranch())
        }
        else {
            propagate()
        }
    }
}

fun customizedBranch(arrayOfExercises: ArrayList<SingleExercise>) : State = state (Interaction){
    onEntry {
        furhat.stopListening()

        if (arrayOfExercises.size > 0) {
            furhat.say( "You have ${arrayOfExercises.size} exercises in your training. Let's add one more!")
            //for (el in arrayOfExercises) println(el.toString())
        }

        send(PickOne(title = "Select one exercise:", type= "Exercises", exerciseList= arrayOfExercises.stream().map { ("${it.name}") }.toList()))

        random(
                { furhat.ask("Now, pick an exercise.",  60000) },
                { furhat.ask("Please, select the exercise you want to do.",  60000) }
        )
        send(SPEECH_DONE)
    }

    onResponse<Exercise> {

        val exerciseName = it.intent.exerciseType?.value
        furhat.say("${exerciseName}? Right?")

        val firstEx = SingleExercise(exerciseName.toString(), null, null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }


    onEvent(CLICK_BUTTON) {
        furhat.stopSpeaking()
        val exerciseName = it.get("data") as String
        // Directly respond with the value we get from the event, with a fallback
        furhat.stopListening()
        furhat.say("You want to do $exerciseName")

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        //Here we add the next exercise to the ArrayList of exercises (only with the name)
        //reps, sets and restTime will be set in the next states.
        val firstEx = SingleExercise(exerciseName, null, null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }
}

fun repsSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{

        send(DataDelivery(buttons = listOf(), inputFields = repFieldData.keys.toList(), title =  "Select the number of repetitions you want to perform during each set", inputType = "number", inputLabel = "Enter number of reps"))


        furhat.stopListening()
        random(
                { furhat.ask("How many repetitions do you want to perform during each set?",  60000) },
                { furhat.ask("How many reps do you want to do for each set? ",  60000) }
        )

        send(SPEECH_DONE)

    }

    onResponse <RepsNumberIntent> {
        send(SPEECH_INPROGRESS)
        val reps = it.intent.number?.value
        if (reps != null) {
            //val answer = repFieldData["Reps"]?.invoke(reps.toString())
            furhat.gesture(Gestures.Smile(duration = 3.0))
            if(reps > 1)
                random(
                        {furhat.say("Ok, $reps repetitions!")},
                        {furhat.say("Great, $reps repetitions!")},
                        {furhat.say("Perfect, $reps repetitions!")}
                )
            else
                random(
                        {furhat.say("Ok, $reps repetition!")},
                        {furhat.say("Great, $reps repetition!")},
                        {furhat.say("Perfect, $reps repetition!")}

                )
            furhat.gesture(Gestures.Smile)
        }
        arrayOfExercises[arrayOfExercises.size - 1].reps = reps

        goto(setsSelectionState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        furhat.stopSpeaking()
        val data = it.get("data") as Record
        //val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        //val answer = repFieldData[variable]?.invoke(value)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        if (value.toInt() > 1)
            random(
                    {furhat.say("Ok, $value repetitions!")},
                    {furhat.say("Great, $value repetitions!")},
                    {furhat.say("Perfect, $value repetitions!")}
            )
        else
            random(
                    {furhat.say("Ok, $value repetition!")},
                    {furhat.say("Great, $value repetition!")},
                    {furhat.say("Perfect, $value repetition!")}
            )


        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        arrayOfExercises[arrayOfExercises.size - 1].reps = value.toInt()

        goto(setsSelectionState(arrayOfExercises))
    }
}

fun setsSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        send(DataDelivery(buttons = listOf(), inputFields = setFieldData.keys.toList(), title =  "Select the number of sets you want to perform", inputType = "number", inputLabel = "Enter number of sets"))

        furhat.stopListening()
        random(
                { furhat.say("How many sets do you want to perform?") },
                { furhat.say("How many sets do you want to do?") }
        )
        send(SPEECH_DONE)
        furhat.listen( 60000)

    }

    onResponse <SetsNumberIntent> {
        send(SPEECH_INPROGRESS)
        val sets = it.intent.number?.value
        if (sets != null) {
            //val answer = setFieldData["Sets"]?.invoke(sets.toString())
            furhat.gesture(Gestures.Smile(duration = 3.0))
            if (sets > 1)
                random(
                        {furhat.say("Ok, $sets sets!")},
                        {furhat.say("Great, $sets sets!")},
                        {furhat.say("Perfect, $sets sets!")}
                )
            else
                random(
                        {furhat.say("Ok, $sets set!")},
                        {furhat.say("Great, $sets set!")},
                        {furhat.say("Perfect, $sets set!")}
                )
        }

        arrayOfExercises[arrayOfExercises.size - 1].sets = sets

        goto(restSelectionState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        furhat.stopSpeaking()
        val data = it.get("data") as Record
        //val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        //val answer = setFieldData[variable]?.invoke(value)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        if (value.toInt() > 1)
            random(
                    {furhat.say("Ok, $value sets!")},
                    {furhat.say("Great, $value sets!")},
                    {furhat.say("Perfect, $value sets!")}
            )
        else
            random(
                    {furhat.say("Ok, $value set!")},
                    {furhat.say("Great, $value set!")},
                    {furhat.say("Perfect, $value set!")}
            )

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
        arrayOfExercises[arrayOfExercises.size - 1].sets = value.toInt()

        goto(restSelectionState(arrayOfExercises))
    }
}

fun restSelectionState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        send(DataDelivery(buttons = listOf(), inputFields = restFieldData.keys.toList(), title =  "Select the rest time between two sets (in seconds)", inputType = "number", inputLabel = "Enter rest time [seconds]"))
        random(
                { furhat.say("How long do you want to rest between the sets?") }
        //more choices...
        )

        send(SPEECH_DONE)
        furhat.listen( 60000)

    }

    onResponse <RestIntentSeconds> {
        send(SPEECH_INPROGRESS)
        val rest = it.intent.number?.value
        if (rest != null) {
            //val answer = restFieldData["Rest"]?.invoke(rest.toString())
            furhat.gesture(Gestures.Smile(duration = 3.0))
            if (rest > 1)
                random(
                        {furhat.say("Ok, $rest seconds of rest!")},
                        {furhat.say("Great, $rest seconds of rest!")},
                        {furhat.say("Perfect, $rest seconds of rest!")}
                )
            else
                random(
                        {furhat.say("Ok, $rest second of rest!")},
                        {furhat.say("Great, $rest second of rest!")},
                        {furhat.say("Perfect, $rest second of rest!")}
                )

        }
        arrayOfExercises[arrayOfExercises.size - 1].restTime = rest

        goto(somethingElseState(arrayOfExercises))
    }

    onResponse <RestIntentMinutes> {
        send(SPEECH_INPROGRESS)
        val rest = it.intent.number?.value
        if (rest != null) {
            //val answer = restFieldData["Rest"]?.invoke(rest.toString())
            furhat.gesture(Gestures.Smile(duration = 3.0))
            if (rest > 1)
                random(
                        {furhat.say("Ok, $rest minutes of rest!")},
                        {furhat.say("Great, $rest minutes of rest!")},
                        {furhat.say("Perfect, $rest minutes of rest!")}
                )
            else
                random(
                        {furhat.say("Ok, $rest minute of rest!")},
                        {furhat.say("Great, $rest minute of rest!")},
                        {furhat.say("Perfect, $rest minute of rest!")}
                )
        }
        if (rest != null) {
            arrayOfExercises[arrayOfExercises.size - 1].restTime = rest * 60
        }

        goto(somethingElseState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        furhat.stopSpeaking()
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = restFieldData[variable]?.invoke(value)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        if (value.toInt() > 1)
            random(
                    {furhat.say("Ok, $value seconds of rest!")},
                    {furhat.say("Great, $value seconds of rest!")},
                    {furhat.say("Perfect, $value seconds of rest!")}
            )        else
            random(
                    {furhat.say("Ok, $value second of rest!")},
                    {furhat.say("Great, $value second of rest!")},
                    {furhat.say("Perfect, $value second of rest!")}
            )        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
        arrayOfExercises[arrayOfExercises.size - 1].restTime = value.toInt()

        goto(somethingElseState(arrayOfExercises))
    }
}

fun somethingElseState(arrayOfExercises: ArrayList<SingleExercise>): State = state(Interaction){
    onEntry{
        furhat.stopListening()
        send(DataDelivery(title = "Do you want to add another exercise to the workout?", buttons = options, inputFields = listOf(), inputType = "", inputLabel = ""))

        random(
                { furhat.ask("Do you want another exercise to the workout?") },
                { furhat.ask("Do you want to add another exercise to your training?") },
                { furhat.ask("Do you want me to add another exercise to your workout before starting?") }
        )
        send(SPEECH_DONE)
    }

    onResponse<Yes> {
        send(SPEECH_INPROGRESS)
        furhat.say("Feel energetic?")
        furhat.gesture(Gestures.Wink(duration = 0.7))
        delay(500)
        goto(customizedBranch(arrayOfExercises))
    }

    onResponse<No> {
        //get the tips for the chosen exercises
        send(SPEECH_INPROGRESS)
        setTips(arrayOfExercises)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        furhat.say("Let's start with the workout then!")

        goto(workoutRecapState(arrayOfExercises, "Custom"))
    }

    onEvent(CLICK_BUTTON) {
        furhat.stopSpeaking()
        if(it.get("data") == "Yes"){
            furhat.say("Feel energetic?")
            furhat.gesture(Gestures.Wink(duration = 0.7))
            delay(500)
            goto(customizedBranch(arrayOfExercises))

        } else {
            setTips(arrayOfExercises)
            furhat.gesture(Gestures.Smile(duration = 3.0))
            furhat.say("Let's start with the workout then!")

            goto(workoutRecapState(arrayOfExercises, "Custom"))
        }


    }
}

fun predefinedBranch() : State = state (Interaction){
    onEntry{
        furhat.stopListening()
        send(SPEECH_DONE)
        send(DataDelivery(title = "Select the workout that you want to perform in this session", buttons = workouts, inputFields = listOf(), inputType = "", inputLabel = ""))

        furhat.ask("Please select the workout that you want to perform in this session",  60000)

    }

    onResponse<WorkoutIntent> {
        val selectedType = it.intent.workoutType?.value

        val workoutName = selectedType.toString().replace("_","").toUpperCase()
        val selectedWorkout = WorkoutsEnum.valueOf(workoutName)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        furhat.say(furhat.voice.emphasis("Perfect!"))

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
        goto(difficultySelectionState(selectedWorkout))

    }


    onEvent(CLICK_BUTTON) {
        furhat.stopSpeaking()
        var workoutName = it.get("data") as String
        // Directly respond with the value we get from the event, with a fallback
        furhat.gesture(Gestures.Smile(duration = 3.0))
        //furhat.say("${furhat.voice.emphasis("Great!")}, you want to do a $workoutName")
        furhat.say(furhat.voice.emphasis("Perfect!"))

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        workoutName = workoutName.replace(" ","").toUpperCase()
        val selectedWorkout = WorkoutsEnum.valueOf(workoutName)
        goto(difficultySelectionState(selectedWorkout))

    }
}

fun difficultySelectionState(selectedWorkout: WorkoutsEnum) : State = state (Interaction){

    onEntry{
        furhat.stopListening()
        send(DataDelivery(title = "Select the difficulty that you want to train at", buttons = difficulties, inputFields = listOf(), inputType = "", inputLabel = ""))

        furhat.ask("Which difficulty level do you want to train at?",  60000) //check sentence

        send(SPEECH_DONE)
    }

    onResponse<DifficultyIntent> {
        val selectedDifficulty = DifficultiesEnum.valueOf(it.intent.difficulty?.value.toString().toUpperCase())
        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)
        delay(1000)
        goto(workoutRecapState(exercises,selectedWorkout.toString()))
    }

    onEvent(CLICK_BUTTON) {
        furhat.stopSpeaking()
        var difficulty = it.get("data") as String
        val selectedDifficulty : DifficultiesEnum?

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        difficulty = difficulty.toUpperCase()
        selectedDifficulty = DifficultiesEnum.valueOf(difficulty)

        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)
        delay(1000)
        goto(workoutRecapState(exercises, selectedWorkout.toString()))
    }


}

fun workoutRecapState(arrayOfExercises: ArrayList<SingleExercise>, selectedWorkout: String) : State = state (Interaction){
    onEntry{

        furhat.say("The $selectedWorkout workout is composed by ${arrayOfExercises.size} exercises") //check sentence
        send(SPEECH_DONE)
        goto(exerciseState(arrayOfExercises,0, selectedWorkout))
    }
}



fun exerciseState(arrayOfExercises: ArrayList<SingleExercise>, exCounter : Int, selectedWorkout: String): State = state(Interaction){
    onEntry {

        val exerciseCounter = exCounter + 1

        if(exerciseCounter > arrayOfExercises.size) {
            goto(endState())
        }
        send(WorkoutDelivery(workoutName=selectedWorkout, exercises = arrayOfExercises.toList(), current = exCounter))

        //send(DataDelivery(buttons = listOf("Start"), inputFields = listOf(), title =  "When you are ready click 'START'", inputType = "", inputLabel = ""))
        //send(ExercisesDelivery(exercises = arrayOfExercises, current = exCounter))

        furhat.say("The ${ if (exerciseCounter == 1) "first" else "next" } exercise is ${arrayOfExercises[exerciseCounter - 1].name}.")

        furhat.say("You have to do ${arrayOfExercises[exerciseCounter - 1].sets} sets of ${arrayOfExercises[exerciseCounter - 1].reps} repetitions, with  ${arrayOfExercises[exerciseCounter - 1].restTime} seconds of rest in between.")

        furhat.ask("When you are ready, say start or click the button", 60000)
        send(SPEECH_DONE)
    }

    onResponse<StartIntent> {
        furhat.stopListening()
        goto (setState(arrayOfExercises, exCounter + 1, 0, selectedWorkout))
    }


    onEvent(CLICK_BUTTON){
        furhat.stopSpeaking()
        if(it.get("data") == "Start"){
            furhat.stopListening()
            goto (setState(arrayOfExercises, exCounter + 1, 0, selectedWorkout))
        } else {
            furhat.say("Wrong button, try again!")
        }
    }
}


fun setState(arrayOfExercises: ArrayList<SingleExercise>, exCounter : Int, setCounter : Int, selectedWorkout: String): State = state(Interaction) {

    onEntry{
        send(ExerciseDelivery(arrayOfExercises[exCounter - 1].name, arrayOfExercises[exCounter - 1].reps.toString(), arrayOfExercises[exCounter - 1].sets.toString(), arrayOfExercises[exCounter - 1].restTime.toString(), setCounter +1))
        furhat.say("You can begin the ${ if(setCounter + 1 == 1 ) "first" else "next"} set, tell me when you did ${arrayOfExercises[exCounter - 1].reps} repetitions.")

        delay(1000)


        random(
                    {arrayOfExercises[exCounter - 1].tips?.get(0)?.let { furhat.say(furhat.voice.prosody("Remind: $it", volume = "loud", rate= 0.9)/*, interruptable = true*/ ) }},
                    {arrayOfExercises[exCounter - 1].tips?.get(1)?.let { furhat.say(furhat.voice.prosody("Remind: $it", volume = "loud", rate= 0.9)/*, interruptable = true*/) }},
                    {arrayOfExercises[exCounter - 1].tips?.get(2)?.let { furhat.say(furhat.voice.prosody("Remind: $it", volume = "loud", rate= 0.9)/*, interruptable = true*/) }}
                    )

            furhat.listen(1000000)
       }

    onResponse<FinishIntent>{

        goto(restState(arrayOfExercises, exCounter, setCounter, selectedWorkout))

    }

    onEvent(CLICK_BUTTON){
        if(it.get("data") == "Done"){
            furhat.stopSpeaking()
            goto(restState(arrayOfExercises, exCounter, setCounter, selectedWorkout))

        } else {
            furhat.say("Wrong button, try again!")
        }
    }

}

fun restState(arrayOfExercises: ArrayList<SingleExercise>, exCounter : Int, setCounter : Int, selectedWorkout: String): State = state(Interaction){
    onEntry{
        send(RESTTIME_START)
        furhat.gesture(Gestures.Smile(duration = 3.0))
        furhat.say("Well done! The rest time of ${arrayOfExercises[exCounter - 1].restTime} seconds has started.")
    }

    arrayOfExercises[exCounter - 1].restTime?.let {
        onTime(delay= it.times(1000)){
            send(RESTTIME_STOP)
            if (setCounter + 1 < arrayOfExercises[exCounter - 1 ].sets!!) {
                goto(setState(arrayOfExercises, exCounter, setCounter + 1, selectedWorkout))
            } else if (setCounter + 1 == arrayOfExercises[exCounter - 1 ].sets!!){

                goto(exerciseState(arrayOfExercises, exCounter, selectedWorkout))
            }
        }
    }


    arrayOfExercises[exCounter - 1].restTime?.times(1000)?.let {
        onTime(delay= it.div(2)){
            furhat.say("${arrayOfExercises[exCounter - 1].restTime?.div(2)} seconds have passed")
        }
    }
}

fun endState(): State = state(Interaction){

    onEntry{
        //print ("endState")
        send(SPEECH_INPROGRESS)
        furhat.say("We have reached the end of our training. Good job!")
        furhat.gesture(Gestures.BigSmile(duration = 2.0))
        goto(GUIConnected)
    }
}


fun returnExerciseListFromJson(): ArrayList<SingleExerciseParser> {

    val jsonString: String = File("./assets/exampleGui/assets/data.json").readText(Charsets.UTF_8)

    val exercisesJSONArray = JSONArray(JSONObject(jsonString)["exercise"].toString())
    val availableExercise = arrayListOf<SingleExerciseParser>()
    for (i in 0 until exercisesJSONArray.length()) {
        val exercise = exercisesJSONArray.getJSONObject(i)
        val currExercise = Gson().fromJson(exercise.toString(), SingleExerciseParser::class.java)
        availableExercise.add(currExercise)
    }
    return availableExercise
}

fun workoutListFromJson(): ArrayList<WorkoutParser> {

    val jsonString: String = File("./assets/exampleGui/assets/data.json").readText(Charsets.UTF_8)
    val workoutsJSONArray = JSONArray(JSONObject(jsonString)["workouts"].toString())
    val availableWorkouts = arrayListOf<WorkoutParser>()
    for (i in 0 until workoutsJSONArray.length()) {
        val workout = workoutsJSONArray.getJSONObject(i)
        val currWorkout = Gson().fromJson(workout.toString(), WorkoutParser::class.java)
        availableWorkouts.add(currWorkout)
    }
    return availableWorkouts
}

fun setTips(arrayOfExercises: ArrayList<SingleExercise>) {
    val listFromJson = returnExerciseListFromJson()
    for (ex in arrayOfExercises){

        for (el in listFromJson){
            if(ex.name == el.name)
                ex.tips = el.tips
        }
    }
}

fun createExercisesList(selectedWorkout: WorkoutsEnum, selectedDifficulty: DifficultiesEnum): ArrayList<SingleExercise> {
    val toReturn = ArrayList<SingleExercise>()
    val workouts = workoutListFromJson()
    for (workout in workouts) {
        if (workout.name.toUpperCase().replace(" ","") == selectedWorkout.toString()){
            val exercises = returnExerciseListFromJson()
            for (workoutExercise in workout.exercises)
                for (exercise in exercises){
                    if (exercise.name == workoutExercise){
                        val temp = SingleExercise(exercise.name, exercise.reps.getValue(selectedDifficulty.toString().toLowerCase()), exercise.sets.getValue(selectedDifficulty.toString().toLowerCase()), exercise.rest_time.getValue(selectedDifficulty.toString().toLowerCase()), exercise.tips)
                        toReturn.add(temp)
                    }
                }
            break
        }
    }
    return toReturn
}


