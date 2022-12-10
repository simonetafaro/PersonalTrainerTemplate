package furhatos.app.personaltrainer

//import furhatos.app.personaltrainer.flow.NoGUI
import furhatos.app.personaltrainer.flow.Idle
import furhatos.skills.Skill
import furhatos.flow.kotlin.*


class PersonalTrainerSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
