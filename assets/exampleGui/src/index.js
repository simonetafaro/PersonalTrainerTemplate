// Render the top-level React component
import React from 'react';
import ReactDOM from 'react-dom'
import App from './App';
import Footer from './components/Footer';
import { BrowserRouter } from 'react-router-dom';

ReactDOM.render(<React.StrictMode>
    <BrowserRouter>
        <App />
        <Footer />
    </BrowserRouter>
</React.StrictMode>
    , document.getElementById('react-root'));