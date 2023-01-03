import React, { Component } from "react"
import {
  Button as BootstrapButton,
  FormGroup,
  ControlLabel,
  FormControl
} from 'react-bootstrap';

class Input extends Component {
  constructor(props) {
    super(props)
    this.state = {
      disabled: true,
      value: ""
    };
  }

  isAllowedSubmit = () => {
    return !this.state.disabled && !this.props.speaking && (this.props.inputType == "number" ? this.state.value > 0 : true)
  }

  handleChange = (e) => {
    this.setState({
      value: e.target.value,
      disabled: e.target.value == ""
    });
  }

  handleClick = () => {
    this.save()
  }

  handleEnter = (e) => {
    if (e.key == "Enter") {
      e.preventDefault()
      if (this.isAllowedSubmit()) {
        this.save()
      }
    } else
      if (this.props.inputType == "number") {
        if (this.state.value == "") {
          //input is null
          if (!(e.charCode != 8 && e.charCode == 0 || (e.charCode >= 49 && e.charCode <= 57))) {
            e.preventDefault()
          }
        } else {
          if (!(e.charCode != 8 && e.charCode == 0 || (e.charCode >= 48 && e.charCode <= 57))) {
            e.preventDefault()
          }
        }
      }
  }

  save = () => {
    this.props.onSave(this.props.label, this.state.value)
  }

  render() {
    let { label, inputType, inputLabel } = this.props
    return (
      <div>
        <form>
          <FormGroup controlId={label}>
            <ControlLabel>{label}</ControlLabel>
            <FormControl
              type={inputType}
              className="input-box"
              min={1}
              value={this.state.value}
              placeholder={inputLabel}
              onChange={this.handleChange}
              onKeyPress={this.handleEnter}
            />
          </FormGroup>
        </form>
        <div className="button-container">
          <BootstrapButton
            className={`input-button`}
            onClick={this.handleClick}
            disabled={!this.isAllowedSubmit()}
            block
          >
            Send
          </BootstrapButton>
        </div>
      </div>
    )
  }
}

export default Input
