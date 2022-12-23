import React, { Component } from "react"
import {
    Col,
    Button as BootstrapButton,
} from 'react-bootstrap';

class ExerciseItem extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        let { name, onClick } = this.props
        return (
            <Col sm={3} className={"buttonContainer"}>
                <BootstrapButton
                    key={name}
                    className={`button, button-81`}
                    onClick={() => { onClick(name) }}
                    disabled={false}
                >
                    {name}
                </BootstrapButton>
            </Col>
        )
    }
}

export default ExerciseItem
