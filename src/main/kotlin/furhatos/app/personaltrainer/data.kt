package furhatos.app.personaltrainer

// Buttons
val buttons = listOf("Arms", "Legs", "Abs", "Back")

// Options
val options = listOf("Exercise", "Workout")


/*
 Input fields, each with a answer to be spoken. The answer is defined as a lambda
 function since we want to have different answers depending on what favorite robot the
 user inputs
  */
val inputFieldData = mutableMapOf<String, (String) -> String>(
    "Name" to { name -> "Nice to meet you $name, let's start!   " }
)

