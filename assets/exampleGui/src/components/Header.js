
import Navbar from 'react-bootstrap/lib/Navbar';
import Nav from 'react-bootstrap/lib/Nav'
import NavItem from 'react-bootstrap/lib/NavItem'
import React from 'react'
import { Link, useMatch, useResolvedPath } from "react-router-dom"

export default function Header() {
    return <Navbar>
        <Navbar.Header>
            <Navbar.Brand>
                <a href="/">Furhat Personal Trainer Platform</a>
            </Navbar.Brand>
        </Navbar.Header>
        <Nav>
            <CustomLink to="/">Training</CustomLink>
            <CustomLink to="/about">About</CustomLink>
        </Nav>

    </Navbar>
}

// import { Link, useMatch, useResolvedPath } from "react-router-dom"

// export default function Header() {
//     return (
//         <nav className="nav">
//             <Link to="/" className="site-title">
//                 Site Name
//             </Link>
//             <ul>
//                 <CustomLink to="/pricing">Pricing</CustomLink>
//                 <CustomLink to="/about">About</CustomLink>
//             </ul>
//         </nav>
//     )
// }

function CustomLink({ to, children, ...props }) {
    const resolvedPath = useResolvedPath(to)
    const isActive = useMatch({ path: resolvedPath.pathname, end: true })

    return (
        <li className={isActive ? "active" : ""}>
            <Link to={to} {...props}>
                {children}
            </Link>
        </li>
    )
}