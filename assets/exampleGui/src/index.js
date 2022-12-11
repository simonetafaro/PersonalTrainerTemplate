// Render the top-level React component
import React from 'react';
import ReactDOM from 'react-dom'
import App from './components/App.js';
import Navbar from 'react-bootstrap/lib/Navbar';
import Nav from 'react-bootstrap/lib/Nav'
import NavItem from 'react-bootstrap/lib/NavItem'

ReactDOM.render(<div><Navbar>
    <Navbar.Header>
        <Navbar.Brand>
            <a href="#home">Furhat Personal Trainer Platform</a>
        </Navbar.Brand>
    </Navbar.Header>
    <Nav activeKey={1}>
        <NavItem eventKey={1} href="/home">
            Home
        </NavItem>
        <NavItem eventKey={2} href="/how" disabled>
            How it works
        </NavItem>
        <NavItem eventKey={2} href="/about" disabled>
            About Furhat
        </NavItem>

    </Nav>

</Navbar>
    <App /></div>, document.getElementById('react-root'));