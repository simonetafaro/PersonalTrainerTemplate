import React, { Component } from 'react'
import { Grid, Row, Col } from 'react-bootstrap'

function About() {
    return <Grid style={{ width: "100%" }}>
        <Row>
            <Col sm={12}>
                <h1>Welcome in Training with Furhat</h1>
                <h5>A virtual personal trainer that keeps you motivated while you are training</h5>
            </Col>
        </Row>
        <Col sm={12} style={{ marginTop: "20px" }}>
            <Row>
                <Col sm={12}>
                    <h3></h3>
                </Col>
            </Row>
            <Row>
                <Col sm={6} style={{ justifyContent: "end", display: "flex" }}>
                    <img
                        style={{ objectFit: "contain", width: "50%" }}
                        src="./assets/img/furhat.png"
                    />

                </Col>
                <Col sm={6} style={{ justifyContent: "start", display: "flex" }}>
                    <div style={{ fontSize: 16, fontWeight: "200", width: "80%", textAlign: "justify" }}>
                        Fuhrat is a human-like robot head which first prototype was developed at KTH in 2011. It comes as an hybrid solution in between virtual embodied conversational agents and mechanical robot heads. It was tested that Furhat is able to overcome the Mona Lisa effect which is present in interactions with three-dimensional objects that are displayed on a two-dimensional surface. This, combined with the easiness of manipulation of the lips movement, the gaze and facial expressions of the robot, make it suitable to interact with people in social contexts. For this reason, Furhat could be a good coach for training. Able to show the correct way of doing exercises, praise and motivate when needed. Furthermore, adopting Furhat could be a way to cut the costs of gyms that can decide to purchase it to support clients during their training reducing the personnel.
                    </div>

                </Col>

            </Row>
        </Col>
        {/* <Col sm={12} className="mt-auto">
            <Row>
                <Col sm={12}>
                    <h3>The team</h3>
                </Col>
            </Row>
            <Row>
                <Col sm={3}>
                    <h4>Carlo L. Reinotti</h4>

                </Col>
                <Col sm={3}>
                    <h4>Gabriele Carollo</h4>

                </Col>
                <Col sm={3}>
                    <h4>Simon Toblad</h4>

                </Col>
                <Col sm={3}>
                    <h4>Simone Tafaro</h4>

                </Col>
            </Row>
        </Col> */}

    </Grid>
}

export default About;