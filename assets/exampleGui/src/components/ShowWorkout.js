import React, { useState, useEffect } from "react";
import { Grid, Row, Col } from 'react-bootstrap'
import Button from "./Button";
import Countdown from 'react-countdown';
import ShowExerciseList from "./ShowExerciseList";

function ShowWorkout(props) {
    const workoutName = props.workoutName;
    const exercises = props.exercises;
    const counter = props.counter;
    const clickButton = props.clickButton;

    return (<Grid>

        <Row>
            <Col sm={12}>
                <h2 style={{ display: "flex", width: "100%" }}>{workoutName}</h2>
            </Col>
        </Row>
        <Row>
            <Col sm={6}>
                <ShowExerciseList exercises={
                    exercises}
                    counter={counter}
                />
            </Col>
            <Col sm={6}>
                <h2>When you are ready click 'START'</h2>
                <div style={{
                    display: "flex",
                    justifyContent: "center",
                    width: "100%"
                }}>
                    <Button className="button-17" key={"Start"} label={"Start"} onClick={clickButton} speaking={false} /></div>
            </Col>
        </Row>

    </Grid>
    );


}

export default ShowWorkout;
