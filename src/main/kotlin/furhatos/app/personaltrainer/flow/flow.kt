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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.collections.ArrayList

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"
var arrayOfExercises = ArrayList<SingleExercise>()



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
        send(PickOne(title = "Select one option:", type= "Training"))
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

        if(it.get("data") == "Exercise"){
            furhat.say("Single exercise. ${furhat.voice.emphasis("Great!")}")
            goto(customizedBranch(arrayOfExercises))
        } else {
            furhat.say("Predefined workout. ${furhat.voice.emphasis("Great!")}")
            goto(predefinedBranch())
        }
        // Let the GUI know we're done speaking, to unlock buttons

    }

    onResponse<Customized>{

        val selectedType = it.intent.customized
        if (selectedType != null) {
            furhat.say("${selectedType}. ${furhat.voice.emphasis("Great!")}")
            goto(customizedBranch(arrayOfExercises))
        }
        else {
            propagate()
        }

    }

   onResponse<Predefined>{

        val selectedType = it.intent.predefined

        if (selectedType != null) {
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

        //send(DataDelivery(title = "Pick one exercise:", buttons = exercises, inputFields = listOf()))
        send(PickOne(title = "Select one exercise:", type= "Exercises"))

        random(
                { furhat.ask("Now, pick an exercise.",  60000) },
                { furhat.ask("Please, select the exercise you want to do.",  60000) }
        )
        send(SPEECH_DONE)
    }

    onResponse<Exercise> {

        val exerciseName = it.intent.exerciseType
        furhat.say("${exerciseName}? Right?")

        val firstEx = SingleExercise(exerciseName.toString(), null, null, null, null)
        arrayOfExercises.add(firstEx)

        goto(repsSelectionState(arrayOfExercises))
    }


    onEvent(CLICK_BUTTON) {
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
            val answer = repFieldData["Reps"]?.invoke(reps.toString())
            if(reps > 1)
                furhat.say(("$answer repetitions!"))
            else
                furhat.say(("$answer repetition!"))
        }
        arrayOfExercises[arrayOfExercises.size - 1].reps = reps

        goto(setsSelectionState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = repFieldData[variable]?.invoke(value)

        if (value.toInt() > 1)
            furhat.say((answer + "repetitions!"))
        else
            furhat.say((answer + "repetition!"))

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
            val answer = setFieldData["Sets"]?.invoke(sets.toString())
            if (sets > 1)
                furhat.say((answer + "sets!"))
            else
                furhat.say((answer + "set!"))
        }

        arrayOfExercises[arrayOfExercises.size - 1].sets = sets

        goto(restSelectionState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = setFieldData[variable]?.invoke(value)
        if (value.toInt() > 1)
            furhat.say((answer + "sets!"))
        else
            furhat.say((answer + "set!"))

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
            val answer = restFieldData["Rest"]?.invoke(rest.toString())
            if (rest > 1)
                furhat.say((answer + "seconds of rest!"))
            else
                furhat.say((answer + "second of rest!"))

        }
        arrayOfExercises[arrayOfExercises.size - 1].restTime = rest

        goto(somethingElseState(arrayOfExercises))
    }

    onResponse <RestIntentMinutes> {
        send(SPEECH_INPROGRESS)
        val rest = it.intent.number?.value
        if (rest != null) {
            val answer = restFieldData["Rest"]?.invoke(rest.toString())
            if (rest > 1)
                furhat.say((answer + "minutes of rest!"))
            else
                furhat.say((answer + "minute of rest!"))

        }
        if (rest != null) {
            arrayOfExercises[arrayOfExercises.size - 1].restTime = rest * 60
        }

        goto(somethingElseState(arrayOfExercises))
    }

    onEvent(VARIABLE_SET) {
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = restFieldData[variable]?.invoke(value)
        if (value.toInt() > 1)
            furhat.say((answer + "seconds of rest!"))
        else
            furhat.say((answer + "second of rest!"))


        // Let the GUI know we're done speaking, to unlock buttons
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
        goto(customizedBranch(arrayOfExercises))
    }

    onResponse<No> {
        //get the tips for the chosen exercises
        send(SPEECH_INPROGRESS)
        setTips(arrayOfExercises)
        furhat.say("Let's start with the workout then!")

        //goto(exerciseState(arrayOfExercises, 0))
        goto(workoutRecapState(arrayOfExercises, "Custom workout"))
    }

    onEvent(CLICK_BUTTON) {

        if(it.get("data") == "Yes"){
            furhat.say("Feel energetic?")
            goto(customizedBranch(arrayOfExercises))

        } else {
            setTips(arrayOfExercises)

            furhat.say("Let's start with the workout then!")

            goto(workoutRecapState(arrayOfExercises, "Custom workout"))
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

    /*onResponse<UpperBodyIntent> {
        val selectedWorkout = WorkoutsEnum.UPPERBODY
        goto(difficultySelectionState(selectedWorkout))

    }

    onResponse<LowerBodyIntent> {
        val selectedWorkout = WorkoutsEnum.LOWERBODY
        goto(difficultySelectionState(selectedWorkout))
    }

    onResponse<FullBodyIntent> {
        val selectedWorkout = WorkoutsEnum.FULLBODY
        goto(difficultySelectionState(selectedWorkout))
    }*/

    onResponse<WorkoutIntent> {
        val selectedType = it.intent.workoutType


        val workoutName = selectedType.toString().replace(" ","").toUpperCase()
        val selectedWorkout = WorkoutsEnum.valueOf(workoutName)
        goto(difficultySelectionState(selectedWorkout))

    }


    onEvent(CLICK_BUTTON) {
        var workoutName = it.get("data") as String
        // Directly respond with the value we get from the event, with a fallback
        furhat.say("${furhat.voice.emphasis("Great!")}, you want to do a $workoutName")

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

    onResponse<BeginnerIntent> {
        val selectedDifficulty = DifficultiesEnum.BEGINNER
        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)


       goto(exerciseState(exercises,0))
        //goto workoutrecap state
    }

    onResponse<IntermediateIntent> {
        val selectedDifficulty = DifficultiesEnum.INTERMEDIATE
        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)

        goto(exerciseState(exercises,0))
        //goto workoutrecap state
    }

    onResponse<AdvancedIntent> {
        val selectedDifficulty = DifficultiesEnum.ADVANCED
        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)

        goto(exerciseState(exercises,0))
        //goto workoutrecap state
    }

    onEvent(CLICK_BUTTON) {
        var difficulty = it.get("data") as String
        val selectedDifficulty : DifficultiesEnum?
        // Directly respond with the value we get from the event, with a fallback

        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)

        difficulty = difficulty.toUpperCase()
        selectedDifficulty = DifficultiesEnum.valueOf(difficulty)

        val exercises = createExercisesList(selectedWorkout, selectedDifficulty)
        goto(workoutRecapState(exercises, selectedWorkout.toString()))
    }


}

fun workoutRecapState(arrayOfExercises: ArrayList<SingleExercise>, selectedWorkout: String) : State = state (Interaction){
    onEntry{
        send(WorkoutDelivery(selectedWorkout, arrayOfExercises))
        furhat.say("The $selectedWorkout is composed by ${arrayOfExercises.size} exercises") //check sentence
        send(SPEECH_DONE)
        goto(exerciseState(arrayOfExercises,0))
    }
}



fun exerciseState(arrayOfExercises: ArrayList<SingleExercise>, exCounter : Int ): State = state(Interaction){
    onEntry {

        val exerciseCounter = exCounter + 1

        if(exerciseCounter > arrayOfExercises.size) {
            goto(endState())
        }
        send(DataDelivery(buttons = listOf("Start"), inputFields = listOf(), title =  "When you are ready click 'START'", inputType = "", inputLabel = ""))

        furhat.say("The ${ if (exerciseCounter == 1) "first" else "next" } exercise is ${arrayOfExercises[exerciseCounter - 1].name}.")

        furhat.say("You have to do ${arrayOfExercises[exerciseCounter - 1].sets} sets of ${arrayOfExercises[exerciseCounter - 1].reps} repetitions, with  ${arrayOfExercises[exerciseCounter - 1].restTime} seconds of rest in between.")


        furhat.ask("When you are ready, say start or click the button", 60000)
        send(SPEECH_DONE)
    }

    onResponse<StartIntent> {
        furhat.stopListening()
        goto (setState(arrayOfExercises, exCounter + 1, 0))
    }


    onEvent(CLICK_BUTTON){
        if(it.get("data") == "Start"){
            furhat.stopListening()
            goto (setState(arrayOfExercises, exCounter + 1, 0))
        } else {
            furhat.say("Wrong button, try again!")
        }
    }
}


fun setState(arrayOfExercises: ArrayList<SingleExercise>, exCounter : Int, setCounter : Int): State = state(Interaction) {

    onEntry{
        send(ExerciseDelivery(arrayOfExercises[exCounter - 1].name, arrayOfExercises[exCounter - 1].reps.toString(), arrayOfExercises[exCounter - 1].sets.toString(), arrayOfExercises[exCounter - 1].restTime.toString()))
        furhat.say("You can begin the ${ if(setCounter + 1 == 1 ) "first" else "next"} set, tell me when you did ${arrayOfExercises[exCounter - 1].reps} repetitions.")
        random(
                //change tone of furhat?
                    {arrayOfExercises[exCounter - 1].tips?.get(0)?.let { furhat.say(it /*, interruptable = true*/) }},
                    {arrayOfExercises[exCounter - 1].tips?.get(1)?.let { furhat.say(it/*, interruptable = true*/) }},
                    {arrayOfExercises[exCounter - 1].tips?.get(2)?.let { furhat.say(it/*, interruptable = true*/) }}
                    )
            furhat.listen(1000000)
       }

    onResponse<FinishIntent>{

        send(SPEECH_INPROGRESS)
        furhat.say("Well done. The rest time of ${arrayOfExercises[exCounter - 1].restTime} seconds starts from now.")

        send(RESTTIME_START)

        //With this solution furhat sleep, and it is not responsive. This is a temporary solution that might be improved in the future
        arrayOfExercises[exCounter - 1].restTime?.times(1000)?.let { it1 -> Thread.sleep(it1.toLong()) }

        send(RESTTIME_STOP)

        if (setCounter + 1 < arrayOfExercises[exCounter -1 ].sets!!) {
            goto(setState(arrayOfExercises, exCounter, setCounter + 1))
        } else if (setCounter + 1 == arrayOfExercises[exCounter - 1 ].sets!!){


            goto(exerciseState(arrayOfExercises, exCounter))
        }
    }

    onEvent(CLICK_BUTTON){
        if(it.get("data") == "Done"){
            furhat.stopSpeaking()

            send(SPEECH_INPROGRESS)
            furhat.say("Well done. The rest time of ${arrayOfExercises[exCounter - 1].restTime} seconds starts from now.")

            send(RESTTIME_START)

            //With this solution furhat sleep, and it is not responsive. This is a temporary solution that might be improved in the future
            arrayOfExercises[exCounter - 1].restTime?.times(1000)?.let { it1 -> Thread.sleep(it1.toLong()) }

            send(RESTTIME_STOP)


            if (setCounter + 1 < arrayOfExercises[exCounter -1 ].sets!!) {
                goto(setState(arrayOfExercises, exCounter, setCounter + 1))
            } else if (setCounter + 1 == arrayOfExercises[exCounter - 1 ].sets!!){
                goto(exerciseState(arrayOfExercises, exCounter))
            }
        } else {
            furhat.say("Wrong button, try again!")
        }
    }

}


fun endState(): State = state(Interaction){

    onEntry{
        print ("endState")
        furhat.say("We have reached the end of our training. Good job!")
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
        //println("${exercise.get("name")} by ${exercise.get("muscle group")}")
    }
    /*for (el in availableExercise) {
         print(el)
         print("\n")
     }*/

    return availableExercise
}

fun workoutListFromJson(): ArrayList<WorkoutParser> {

    val jsonString: String = File("./assets/exampleGui/assets/data.json").readText(Charsets.UTF_8)
    val workoutsJSONArray = JSONArray(JSONObject(jsonString)["workouts"].toString())
    val availableWorkouts = arrayListOf<WorkoutParser>()
    for (i in 0 until workoutsJSONArray.length()) {
        val workout = workoutsJSONArray.getJSONObject(i)
        var currWorkout = Gson().fromJson(workout.toString(), WorkoutParser::class.java)
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
        if (workout.name.keys.toList()[0].toUpperCase().replace(" ","") == selectedWorkout.toString()){
            val exercises = returnExerciseListFromJson()
            for (exercise in exercises){
                for (workoutExercise in workout.name.values.toList()[0])
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

