import React, { useState, useEffect } from "react";
import { Grid, Row, Col } from 'react-bootstrap'
import Button from "./Button";
import Countdown from 'react-countdown';

function ShowExercise(props) {
    const [data, setData] = useState([]);
    const exerciseName = props.exerciseName;
    const exerciseReps = props.reps;
    const exerciseSets = props.sets;
    const exerciseRest = props.rest;
    const doing = props.doing;
    const clickButton = props.clickButton;

    const speaking = props.speaking;

    const getData = () => {
        fetch('./assets/data.json'
            , {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        )
            .then(function (response) {
                return response.json();
            })
            .then(function (myJson) {
                myJson.exercise.map((ex) => {
                    if (ex.name == exerciseName) {
                        setData(ex)
                    }
                })

            });
    }
    useEffect(() => {
        getData()
    }, [])


    return (<Grid>


        <Row>
            <Col sm={6}>
                <h2 style={{ display: "flex" }}>{exerciseName}</h2>
                <p style={{ fontSize: 18, color: "#000", fontWeight: "200" }}>{exerciseSets} sets of {exerciseReps} repetitions each</p>

                <Row style={{
                    marginTop: "35px", fontSize: "60px",
                    justifyContent: "center",
                    display: "flex"
                }}>
                    {doing ? <Button className="button-17" key={"Done"} label={"Done"} onClick={clickButton} speaking={speaking} /> : <Countdown date={Date.now() + (parseInt(exerciseRest, 10) * 1000)} />}
                </Row>
            </Col>
            <Col sm={6}>

                <img
                    style={{}}
                    src={data.gif}
                />
            </Col>
        </Row>

    </Grid>
    );


}

export default ShowExercise;
