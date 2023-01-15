package furhatos.app.personaltrainer.flow

import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.util.*

val Idle: State = state(null) {

    onEvent<SenseSkillGUIConnected> {
        goto(GUIConnected)

    }
}

val Interaction: State = state {

    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(Greeting)
                //goto(NoGUI)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(Idle)
        }
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }

}