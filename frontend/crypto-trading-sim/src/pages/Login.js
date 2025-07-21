import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/AuthForm';
import '../styles/form.css';

const Login = () => {
    const apiUrl = process.env.REACT_APP_BACKEND_BASE_API_URL;
    const [form, setForm] = useState({
        username: '',
        password: '',
    });
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        try {
            const response = await fetch(`${apiUrl}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: form.username,
                    password: form.password,
                }),
            });

            if (response.ok) {
                const token = await response.text();
                localStorage.setItem('token', token);
                navigate('/');
            } else {
                setErrorMessage('Invalid username or password');
            }
        } catch (err) {
            setErrorMessage('Something went wrong. Please try again later.');
            console.error('Login error:', err);
        }
    };

    return (
        <AuthForm
            title="Sign In"
            form={form}
            setForm={setForm}
            handleSubmit={handleLogin}
            error={errorMessage}
        />
    );
};

export default Login;
