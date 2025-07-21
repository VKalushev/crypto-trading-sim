import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/AuthForm';
import '../styles/form.css';

const Register = () => {
    const [form, setForm] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
    });

    const apiUrl = process.env.REACT_APP_BACKEND_BASE_API_URL;
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (form.password !== form.confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        setError(null);
        setSuccess(null);

        const userPayload = {
            username: form.username,
            password: form.password,
            firstName: form.firstName,
            lastName: form.lastName,
            role: 'USER',
        };

        try {
            const response = await fetch(`${apiUrl}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userPayload),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setError(errorData.message || 'Registration failed');
            } else {
                setSuccess('Registration successful!');
                setForm({
                    username: '',
                    password: '',
                    confirmPassword: '',
                    firstName: '',
                    lastName: '',
                });
                setTimeout(() => {
                    navigate('/login');
                }, 1000);
            }
        } catch (err) {
            console.error('Registration error:', err);
            setError('An error occurred while registering. Please try again.');
        }
    };

    return (
        <AuthForm
            title="Create Account"
            form={form}
            setForm={setForm}
            handleSubmit={handleSubmit}
            error={error}
            success={success}
            isRegister={true}
        />
    );
};

export default Register;
