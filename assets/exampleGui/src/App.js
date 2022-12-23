import React, { Component } from 'react'
import Header from "./components/Header"
import Home from "./components/Home"
import About from "./components/About"
import { Route, Routes } from "react-router-dom"
import FurhatGUI from 'furhat-gui'
import Loader from './components/Loader'
import { Grid, Row, Col } from 'react-bootstrap'
import ExercisesList from './components/ExercisesList'
import ShowExercise from './components/ShowExercise'
class App extends Component {

  constructor(props) {
    super(props)
    this.state = {
      "speaking": false,
      "buttons": [],
      "inputFields": [],
      "furhat": null
    }
    this.furhat = null
  }

  // setupSubscriptions() {
  //   // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
  //   this.furhat.subscribe('furhatos.app.personaltrainer.DataDelivery', (data) => {
  //     this.setState({
  //       ...this.state,
  //       buttons: data.buttons,
  //       inputFields: data.inputFields
  //     })
  //   })

  //   // This event contains to data so we defined it inline in the flow
  //   this.furhat.subscribe('SpeechDone', () => {
  //     this.setState({
  //       ...this.state,
  //       speaking: false
  //     })
  //   })
  // }

  componentDidMount() {
    FurhatGUI()
      .then(connection => {
        this.furhat = connection
        //this.setupSubscriptions()

        this.setState({
          ...this.state,
          furhat: connection,
        })
      })
      .catch(console.error)

  }

  render() {
    if (this.furhat == null) {
      return <NoSkillConnection />
    } else {
      return (
        <React.Fragment>
          <Header />
          <div className="container">
            <Routes>
              <Route path="/" element={<Home furhat={this.state.furhat} />} />
              <Route path="/about" element={<About />} />
            </Routes>
          </div>

        </React.Fragment>
      )
    }

  }
}


function NoSkillConnection() {
  return <Grid>
    <Row>
      <Loader />
    </Row>

    <Row>
      <h2>Waiting for Skill ...</h2>
    </Row>
  </Grid>;
}

export default App