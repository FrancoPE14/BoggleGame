'use client';

import Link from 'next/link';
import { useContext } from 'react';
import { Container, Nav, Navbar } from 'react-bootstrap';
import LoginStatusContext from './context/loginStatusContext';

export default function BoggleNav() {

const [loginStatus] = useContext(LoginStatusContext);

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        <Navbar.Brand as={Link} href="/">
          Boggle UW
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Nav.Link as={Link} href="/leaderboard">
              Leaderboard
            </Nav.Link>
            {!loginStatus ? (
              <>
                <Nav.Link as={Link} href="/auth/login">
                  Login
                </Nav.Link>
                <Nav.Link as={Link} href="/auth/register">
                  Register
                </Nav.Link>
              </>
            ) : (
              <Nav.Link as={Link} href="/auth/logout">
                Logout
              </Nav.Link>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
