import React, { Component } from "react"
import {
    Col,
    Button as BootstrapButton,
} from 'react-bootstrap';
import { FallingLines } from "react-loader-spinner";

class ExerciseItem extends Component {
    constructor(props) {
        super(props)
    }
    checkExerciseSelection = (ex) => {
        return (this.props.list.indexOf(ex) > -1)
    }
    render() {
        let { name, onClick } = this.props
        return (
            <Col sm={3} className={"buttonContainer"}>
                <BootstrapButton
                    key={name}
                    className={`button, button-81`}
                    onClick={() => { onClick(name) }}
                    disabled={this.checkExerciseSelection(name)}
                >
                    {name}
                </BootstrapButton>
            </Col>
        )
    }
}

export default ExerciseItem
