import React, { Component } from 'react'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'

class Home extends Component {

    constructor({ props, furhat }) {
        super(props)
        this.state = {
            "speaking": false,
            "buttons": [],
            "inputFields": [],
            "title": "",
            "exerciseName": "",
            "gifName": "",
            "reps": ""
        }
        this.furhat = furhat
    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.personaltrainer.DataDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields,
                title: data.title
            })
        })
        this.furhat.subscribe('furhatos.app.personaltrainer.ExerciseDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: [],
                inputFields: [],
                exerciseName: data.exerciseName,
                gifName: data.gifName,
                reps: data.reps
            })
        })
        // This event contains to data so we defined it inline in the flow
        this.furhat.subscribe('SpeechDone', () => {
            this.setState({
                ...this.state,
                speaking: false
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
        this.furhat.send({
            event_name: "VariableSet",
            data: {
                variable,
                value
            }
        })
    }



    render() {

        if (this.state.buttons.length > 0) {
            return <Grid>

                <Row>
                    <Col sm={12}>
                        <h2>{this.state.title}</h2>
                        {this.state.buttons.map((label) =>
                            <Button key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking} />
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
}

export default Home;
