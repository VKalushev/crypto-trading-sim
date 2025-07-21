import React from 'react';
import { useNavigate } from 'react-router-dom';
import './../styles/navbar.css';

const Navbar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <h1 className="navbar-title">Crypto Trading Sim</h1>
                <button onClick={handleLogout} className="navbar-logout-btn">
                    Logout
                </button>
            </div>
        </nav>
    );
};

export default Navbar;
