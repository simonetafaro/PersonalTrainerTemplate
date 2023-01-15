import React, { useState, useEffect } from "react";
import ExerciseItem from "./ExerciseItem";

import { Grid, Row, Col } from 'react-bootstrap'
function ExercisesList(props) {
    const [data, setData] = useState([]);
    const { handler, list } = props;

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
                setData(myJson.exercise)
            });
    }
    useEffect(() => {
        getData()
    }, [])


    return (<Grid>
        <h1>Pick one exercise:</h1>
        {

            data && data.length > 0 && data.map((item) => {

                let props = {
                    name: item.name,
                    onClick: handler,
                    list: list
                }
                return <ExerciseItem {...props} />
            })
        }
    </Grid>
    );


}

export default ExercisesList;
