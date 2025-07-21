import React from 'react';
import { useNavigate } from 'react-router-dom';

const AuthForm = ({
    title,
    form,
    setForm,
    handleSubmit,
    error,
    success,
    isRegister = false,
}) => {
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    };

    return (
        <div className="form-bg">
            <div className="form-card">
                <h2 className="form-title">{title}</h2>
                {error && <div className="form-error">{error}</div>}
                {success && <div className="form-success">{success}</div>}
                <form onSubmit={handleSubmit}>
                    {isRegister && (
                        <>
                            <input
                                name="firstName"
                                placeholder="First Name"
                                value={form.firstName}
                                onChange={handleChange}
                                required
                                className="form-input"
                            />
                            <input
                                name="lastName"
                                placeholder="Last Name"
                                value={form.lastName}
                                onChange={handleChange}
                                required
                                className="form-input"
                            />
                        </>
                    )}
                    <input
                        name="username"
                        placeholder="Username"
                        value={form.username}
                        onChange={handleChange}
                        required
                        className="form-input"
                    />
                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={form.password}
                        onChange={handleChange}
                        required
                        className="form-input"
                    />
                    {isRegister && (
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm Password"
                            value={form.confirmPassword}
                            onChange={handleChange}
                            required
                            className="form-input"
                        />
                    )}
                    <button type="submit" className="form-btn">
                        {title}
                    </button>
                </form>
                <div>
                    <span>
                        {isRegister
                            ? 'Already have an account?'
                            : "Don't have an account?"}
                    </span>
                    <span
                        className="form-link"
                        onClick={() =>
                            navigate(isRegister ? '/login' : '/register')
                        }
                    >
                        {isRegister ? 'Login' : 'Register'}
                    </span>
                </div>
            </div>
        </div>
    );
};

export default AuthForm;
