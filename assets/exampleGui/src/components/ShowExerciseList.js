import React from "react";
import { Grid, Row, Col } from 'react-bootstrap'

function ShowExerciseList(props) {
    const exercises = props.exercises;
    const counter = props.counter;
    return (<Grid>
        {exercises.map((exercise, index) =>
            <Row>
                <Col sm={6}>
                    <h2 style={{ display: "flex", fontSize: 18, color: counter == index ? "#000" : "#808080" }}>{exercise.name}</h2>
                    <p style={{ fontSize: 16, color: counter == index ? "#000" : "#808080", fontWeight: "200" }}>{exercise.sets} sets of {exercise.reps} repetitions each</p>
                    <p style={{ fontSize: 16, color: counter == index ? "#000" : "#808080", fontWeight: "200" }}>Rest time of {exercise.restTime} seconds</p>
                </Col>
            </Row>
        )}
    </Grid>
    );
}

export default ShowExerciseList;
