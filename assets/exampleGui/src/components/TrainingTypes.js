import React, { Component } from "react";
import {
    Container, Column, TrainingType
} from "./TrainingTypeStyles";

import {
    Grid, Row
} from 'react-bootstrap'

class TrainingTypes extends Component {

    render() {
        let { handler } = this.props

        return (
            <Grid>

                <Row>
                    <p style={{
                        justifyContent: "center",
                        display: "flex",
                        marginBottom: "2rem",
                        fontSize: "3rem",
                        color: "black",
                        fontWeight: "600"
                    }}>Training types</p>

                </Row>
                <Container>

                    <Column>
                        <TrainingType className={"training-type-sx"} onClick={() => { handler("Exercise") }}>

                            <img
                                style={{ objectFit: "contain", width: "100%" }}
                                src="./assets/img/exercise.png"
                            />
                            <div style={{ margin: "auto" }}>Exercise</div>
                        </TrainingType>
                    </Column>
                    <Column>
                        <TrainingType className="training-type-dx" onClick={() => { handler("Workout") }}>
                            <img
                                style={{ objectFit: "contain", width: "100%" }}
                                src="./assets/img/workout.png"
                            />
                            <div style={{ margin: "auto" }}>Workout</div>
                        </TrainingType>
                    </Column>
                </Container>

            </Grid >
        );
    }
}

export default TrainingTypes;
