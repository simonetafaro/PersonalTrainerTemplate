import React, { Component } from 'react'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'
import SpeakingLoader from './SpeakingLoader'
import TrainingTypes from './TrainingTypes'
import ExercisesList from './ExercisesList'
import ShowExercise from './ShowExercise'

let CompToRender;

class Home extends Component {

    constructor({ props, furhat }) {
        super(props)

        this.state = {
            "speaking": false,
            "buttons": [],
            "inputFields": [],
            "title": "",
            "exercise": {
                "name": "",
                "reps": "",
                "sets": "",
                "rest": ""
            }
        }
        this.furhat = furhat
    }

    setState(state) {
        //window.localStorage.setItem('state', JSON.stringify(state));
        super.setState(state);
    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.personaltrainer.DataDelivery', (data) => {
            if (data.buttons.length > 0) {
                CompToRender =
                    <Row>
                        <Col sm={12}>
                            <h2>{data.title}</h2>
                            {data.buttons.map((label) =>
                                <Button className="button-17" key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking} />
                            )}
                        </Col>
                    </Row>;
            } else {
                CompToRender =
                    <Row>
                        <Col sm={12}>
                            <h2>{data.title}</h2>
                            {data.inputFields.map((label) =>
                                <Input key={label} label={label} onSave={this.variableSet} speaking={this.state.speaking} />
                            )}
                        </Col>
                    </Row>;
            }
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields,
                title: data.title,
                speaking: data.speaking,
                exercise: {
                    name: "",
                    reps: "",
                    sets: "",
                    rest: ""
                }
            });

        })

        this.furhat.subscribe('furhatos.app.personaltrainer.ExerciseDelivery', (data) => {

            CompToRender = <ShowExercise exerciseName={
                data.exerciseName}
                reps={data.reps}
                rest={data.rest}
                sets={data.sets}
                doing={true}
                clickButton={this.sendInputButton} />

            this.setState({
                ...this.state,
                buttons: [],
                inputFields: [],
                speaking: true,
                exercise: {
                    name: data.exerciseName,
                    reps: data.reps,
                    rest: data.rest,
                    sets: data.sets
                }
            })
        })

        this.furhat.subscribe('furhatos.app.personaltrainer.ExerciseRestTime', (data) => {

            CompToRender = <ShowExercise exerciseName={
                data.exerciseName}
                reps={data.reps}
                rest={data.rest}
                sets={data.sets}
                doing={true}
                clickButton={this.sendInputButton} />
            this.setState({
                ...this.state
            })
        })



        this.furhat.subscribe('furhatos.app.personaltrainer.PickOne', (data) => {

            CompToRender = data.type == "Training" ? <TrainingTypes handler={this.clickButton} /> : <ExercisesList handler={this.clickButton} />

            this.setState({
                ...this.state,
                buttons: [],
                inputFields: [],
                title: data.title,
                type: data.type,
                speaking: data.speaking
            })

        })

        // This event contains to data so we defined it inline in the flow
        this.furhat.subscribe('SpeechDone', () => {
            this.setState({
                ...this.state,
                speaking: false
            })
        })

        this.furhat.subscribe('TimeToRest', () => {
            CompToRender = <ShowExercise exerciseName={
                this.state.exercise.name}
                reps={this.state.exercise.reps}
                rest={this.state.exercise.rest}
                sets={this.state.exercise.sets}
                clickButton={this.sendInputButton}
                doing={false} />
            this.setState({
                ...this.state
            })
        })

        this.furhat.subscribe('TimeToRestart', () => {
            CompToRender = <ShowExercise exerciseName={
                this.state.exercise.name}
                reps={this.state.exercise.reps}
                rest={this.state.exercise.rest}
                sets={this.state.exercise.sets}
                clickButton={this.sendInputButton}
                doing={true} />
            this.setState({
                ...this.state
            })
        })
    }

    componentDidMount() {
        if (this.furhat != null) {
            this.setupSubscriptions()
        }
    }

    clickButton = (button) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        CompToRender = <SpeakingLoader />

        this.furhat.send({
            event_name: "ClickButton",
            data: button
        })
    }

    sendInputButton = (button) => {

        this.furhat.send({
            event_name: "ClickButton",
            data: button
        })
    }

    variableSet = (variable, value) => {
        this.setState({
            ...this.state,
            speaking: true
        })

        CompToRender = <SpeakingLoader />
        this.furhat.send({
            event_name: "VariableSet",
            data: {
                variable,
                value
            }
        })
    }



    render() {

        return <Grid>
            {CompToRender}
        </Grid>
        /*
                if (this.state.speaking) {
                    return <Grid>
                        <Row>
                            <SpeakingLoader />
                        </Row>
                    </Grid>;
                } else {
                    if (this.type == "Training") {
                        <Grid>
                            <CompToRender />
                        </Grid>
                    } else {
                        if (this.state.buttons.length > 0) {
                            return <Grid>
                                <Row>
                                    <Col sm={12}>
                                        <h2>{this.state.title}</h2>
                                        {this.state.buttons.map((label) =>
                                            <Button className="button-17" key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking} />
                                        )}
                                    </Col>
        
                                </Row>
                            </Grid>;
                        } else {
                            if (this.state.inputFields.length > 0) {
                                return <Grid>
                                    <Row>
                                        <Col sm={12}>
                                            <h2>{this.state.title}</h2>
                                            {this.state.inputFields.map((label) =>
                                                <Input key={label} label={label} onSave={this.variableSet} speaking={this.state.speaking} />
                                            )}
                                        </Col>
                                    </Row>
                                </Grid>;
                            } else {
                                if (this.state.exerciseName != "") {
                                    return <Grid>
        
                                        <Row>
        
                                            <Col sm={12}>
                                                <h2>{this.state.exerciseName}</h2>
                                                <img
                                                    style={{ width: "100%", height: "80%" }}
                                                    src="./assets/gifs/gif.gif"
                                                />
                                            </Col>
                                        </Row>
                                    </Grid>;
                                } else {
                                    return <Grid>
        
                                        <Row>
        
                                            <Col sm={12}>
                                                <h2>Waiting for Furhat</h2>
                                            </Col>
                                        </Row>
                                    </Grid>;
                                }
        
                            }
                        }
                    }
        
                }*/

    }
}

export default Home;
