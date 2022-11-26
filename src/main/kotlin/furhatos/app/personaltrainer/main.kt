package furhatos.app.personaltrainer

import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class PersonalTrainerSkill : Skill() {
    override fun start() {
        Flow().run(NoGUI)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
