import React, { Component } from "react";
import {
    Container, Column, TrainingType
} from "./TrainingTypeStyles";


class TrainingTypes extends Component {
    render() {
        return (
            <Container>
                <Column>
                    <TrainingType className="training-type-sx" onClick={() => { this.props.handler("Exercise") }}>
                        <img
                            style={{ "object-fit": "contain", width: "100%" }}
                            src="./assets/img/exercise.png"
                        />
                        <div style={{ margin: "auto" }}>Exercise</div>
                    </TrainingType>
                </Column>
                <Column>
                    <TrainingType className="training-type-dx" onClick={() => { this.props.handler("Workout") }}>
                        <img
                            style={{ "object-fit": "contain", width: "100%" }}
                            src="./assets/img/workout.png"
                        />
                        <div style={{ margin: "auto" }}>Workout</div>
                    </TrainingType>
                </Column>
            </Container>
        );
    }
}

export default TrainingTypes;
