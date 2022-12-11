import React, { Component } from 'react'
import FurhatGUI from 'furhat-gui'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'
import Loader from './Loader'

class App extends Component {

    constructor(props) {
        super(props)
        this.state = {
            "speaking": false,
            "buttons": [],
            "inputFields": []
        }
        this.furhat = null
    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.personaltrainer.DataDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields
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
        FurhatGUI()
            .then(connection => {
                this.furhat = connection
                this.setupSubscriptions()
            })
            .catch(console.error)

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
        if (this.furhat == null) {

            return <NoSkillConnection />;
        } else {
            if (this.state.buttons.length > 0) {
                return <Grid>

                    <Row>
                        <Col sm={12}>
                            <h2>Choose</h2>
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
                                <h2>Insert your name to start</h2>
                                {this.state.inputFields.map((label) =>
                                    <Input key={label} label={label} onSave={this.variableSet} speaking={this.state.speaking} />
                                )}
                            </Col>
                        </Row>
                    </Grid>;
                } else {
                    return <Grid>

                        <Row>

                            <Col sm={12}>
                                <h2>Waiting for Furhat Personal Trainer ...</h2>

                            </Col>
                        </Row>
                    </Grid>;
                }
            }
        }
    }

}

function NoSkillConnection() {
    return <Grid>

        <Row>
            <Loader />
        </Row>  <Row>
            <h2>Waiting for Skill ...</h2>
        </Row>
    </Grid>;
}



export default App;
