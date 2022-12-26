import React, { useState, useEffect } from "react";
import { Grid, Row, Col } from 'react-bootstrap'
import Button from "./Button";
import Countdown from 'react-countdown';

function ShowWorkout(props) {
    const [data, setData] = useState([]);
    const workoutName = props.workoutName;

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
    // useEffect(() => {
    //     getData()
    // }, [])


    return (<Grid>


        <Row>
            <Col sm={12}>
                <h2 style={{ display: "flex" }}>{workoutName}</h2>
                <h3>GUI Work in progress...</h3>
            </Col>
        </Row>

    </Grid>
    );


}

export default ShowWorkout;
